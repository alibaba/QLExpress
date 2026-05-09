package com.alibaba.qlexpress4.cache;

import com.alibaba.qlexpress4.aparser.QCompileCache;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Default expression cache implementation with advanced features.
 * Supports:
 * - Maximum size limit with LRU eviction
 * - Time-to-live (TTL) expiration
 * - Thread-safe operations
 * <p>
 * When both maxSize and TTL are configured, entries may be evicted
 * based on either condition.
 *
 * @author QLExpress Team
 */
public class DefaultExpressionCache implements ExpressionCache {

    private final CacheConfig config;
    private final ConcurrentHashMap<String, CacheEntry> cache;
    private final ScheduledExecutorService cleanupExecutor;
    private final AtomicLong accessCounter;

    /**
     * Cache entry wrapper that tracks metadata for eviction.
     */
    private static class CacheEntry {
        final Future<QCompileCache> future;
        final long createTime;
        volatile long lastAccessTime;
        volatile long accessOrder;

        CacheEntry(Future<QCompileCache> future, long createTime) {
            this.future = future;
            this.createTime = createTime;
            this.lastAccessTime = createTime;
            this.accessOrder = 0;
        }
    }

    /**
     * Create a default cache with unlimited size and no expiration.
     */
    public DefaultExpressionCache() {
        this(CacheConfig.defaultConfig());
    }

    /**
     * Create a cache with specified configuration.
     *
     * @param config cache configuration
     */
    public DefaultExpressionCache(CacheConfig config) {
        this.config = config != null ? config : CacheConfig.defaultConfig();
        this.cache = new ConcurrentHashMap<>(this.config.getInitialCapacity());
        this.accessCounter = new AtomicLong(0);

        // Setup TTL cleanup if enabled
        if (this.config.isTtlEnabled()) {
            this.cleanupExecutor = new ScheduledThreadPoolExecutor(1, r -> {
                Thread t = new Thread(r, "qlexpress-cache-cleanup");
                t.setDaemon(true);
                return t;
            });
            // Schedule periodic cleanup every TTL/2 or at least every minute
            long cleanupInterval = Math.min(this.config.getTtlMillis() / 2, 60000);
            cleanupInterval = Math.max(cleanupInterval, 1000); // At least 1 second
            this.cleanupExecutor.scheduleWithFixedDelay(
                this::cleanupExpiredEntries,
                cleanupInterval,
                cleanupInterval,
                TimeUnit.MILLISECONDS
            );
        } else {
            this.cleanupExecutor = null;
        }
    }

    @Override
    public Future<QCompileCache> get(String script) {
        CacheEntry entry = cache.get(script);
        if (entry == null) {
            return null;
        }

        // Check TTL expiration
        if (config.isTtlEnabled() && isExpired(entry)) {
            cache.remove(script, entry);
            return null;
        }

        // Update access metadata
        entry.lastAccessTime = System.currentTimeMillis();
        entry.accessOrder = accessCounter.incrementAndGet();

        return entry.future;
    }

    @Override
    public Future<QCompileCache> put(String script, Future<QCompileCache> future) {
        // Check if we need to evict before adding
        if (config.isMaxSizeEnabled() && cache.size() >= config.getMaxSize()) {
            evictEntry();
        }

        CacheEntry newEntry = new CacheEntry(future, System.currentTimeMillis());
        newEntry.accessOrder = accessCounter.incrementAndGet();

        CacheEntry oldEntry = cache.put(script, newEntry);
        return oldEntry != null ? oldEntry.future : null;
    }

    @Override
    public Future<QCompileCache> putIfAbsent(String script, Future<QCompileCache> future) {
        CacheEntry existing = cache.get(script);
        if (existing != null) {
            // Check if existing entry is expired
            if (config.isTtlEnabled() && isExpired(existing)) {
                if (cache.remove(script, existing)) {
                    // Entry was expired and removed, continue to add new one
                } else {
                    // Someone else removed and added, get the new one
                    CacheEntry newExisting = cache.get(script);
                    return newExisting != null ? newExisting.future : null;
                }
            } else {
                return existing.future;
            }
        }

        // Check if we need to evict before adding
        if (config.isMaxSizeEnabled() && cache.size() >= config.getMaxSize()) {
            evictEntry();
        }

        CacheEntry newEntry = new CacheEntry(future, System.currentTimeMillis());
        newEntry.accessOrder = accessCounter.incrementAndGet();

        CacheEntry previous = cache.putIfAbsent(script, newEntry);
        return previous != null ? previous.future : null;
    }

    @Override
    public Future<QCompileCache> remove(String script) {
        CacheEntry entry = cache.remove(script);
        return entry != null ? entry.future : null;
    }

    @Override
    public void clear() {
        cache.clear();
    }

    @Override
    public int size() {
        if (config.isTtlEnabled()) {
            // Clean up expired entries before returning size for accuracy
            cleanupExpiredEntries();
        }
        return cache.size();
    }

    @Override
    public boolean contains(String script) {
        CacheEntry entry = cache.get(script);
        if (entry == null) {
            return false;
        }

        // Check TTL expiration
        if (config.isTtlEnabled() && isExpired(entry)) {
            cache.remove(script, entry);
            return false;
        }

        return true;
    }

    /**
     * Shutdown the cache and cleanup resources.
     * Should be called when the Express4Runner is no longer needed.
     */
    public void shutdown() {
        if (cleanupExecutor != null) {
            cleanupExecutor.shutdown();
        }
        clear();
    }

    /**
     * Check if an entry has expired based on TTL.
     */
    private boolean isExpired(CacheEntry entry) {
        return System.currentTimeMillis() - entry.createTime > config.getTtlMillis();
    }

    /**
     * Remove expired entries from cache.
     */
    private void cleanupExpiredEntries() {
        if (!config.isTtlEnabled()) {
            return;
        }

        long now = System.currentTimeMillis();
        cache.entrySet().removeIf(entry -> {
            CacheEntry ce = entry.getValue();
            return now - ce.createTime > config.getTtlMillis();
        });
    }

    /**
     * Evict an entry based on LRU policy.
     * Finds and removes the least recently accessed entry.
     */
    private void evictEntry() {
        if (!config.isLruEnabled() || cache.isEmpty()) {
            // If LRU is not enabled but we need to make space,
            // remove a random entry
            if (!cache.isEmpty()) {
                String keyToRemove = cache.keys().nextElement();
                cache.remove(keyToRemove);
            }
            return;
        }

        // Find LRU entry
        String lruKey = null;
        long minAccessOrder = Long.MAX_VALUE;

        for (java.util.Map.Entry<String, CacheEntry> entry : cache.entrySet()) {
            if (entry.getValue().accessOrder < minAccessOrder) {
                minAccessOrder = entry.getValue().accessOrder;
                lruKey = entry.getKey();
            }
        }

        if (lruKey != null) {
            cache.remove(lruKey);
        }
    }
}
