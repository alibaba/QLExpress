package com.alibaba.qlexpress4.cache;

import java.util.concurrent.TimeUnit;

/**
 * Configuration for expression cache.
 * Used to customize cache behavior in DefaultExpressionCache.
 *
 * @author QLExpress Team
 */
public class CacheConfig {

    /**
     * Default maximum cache size (unlimited: -1)
     */
    public static final int DEFAULT_MAX_SIZE = -1;

    /**
     * Default TTL in milliseconds (no expiration: -1)
     */
    public static final long DEFAULT_TTL_MILLIS = -1;

    /**
     * Default initial capacity
     */
    public static final int DEFAULT_INITIAL_CAPACITY = 16;

    /**
     * Maximum number of cached entries.
     * -1 means unlimited.
     */
    private final int maxSize;

    /**
     * Time to live in milliseconds.
     * -1 means no expiration.
     */
    private final long ttlMillis;

    /**
     * Initial capacity of the cache.
     */
    private final int initialCapacity;

    /**
     * Whether to enable LRU eviction when maxSize is reached.
     */
    private final boolean lruEnabled;

    private CacheConfig(int maxSize, long ttlMillis, int initialCapacity, boolean lruEnabled) {
        this.maxSize = maxSize;
        this.ttlMillis = ttlMillis;
        this.initialCapacity = initialCapacity;
        this.lruEnabled = lruEnabled;
    }

    public int getMaxSize() {
        return maxSize;
    }

    public long getTtlMillis() {
        return ttlMillis;
    }

    public int getInitialCapacity() {
        return initialCapacity;
    }

    public boolean isLruEnabled() {
        return lruEnabled;
    }

    public boolean isMaxSizeEnabled() {
        return maxSize > 0;
    }

    public boolean isTtlEnabled() {
        return ttlMillis > 0;
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * Create a default unlimited cache configuration.
     */
    public static CacheConfig defaultConfig() {
        return new CacheConfig(DEFAULT_MAX_SIZE, DEFAULT_TTL_MILLIS, DEFAULT_INITIAL_CAPACITY, false);
    }

    /**
     * Create an LRU cache configuration with specified max size.
     */
    public static CacheConfig lruConfig(int maxSize) {
        return new CacheConfig(maxSize, DEFAULT_TTL_MILLIS, DEFAULT_INITIAL_CAPACITY, true);
    }

    /**
     * Create a TTL cache configuration with specified time to live.
     */
    public static CacheConfig ttlConfig(long ttlMillis) {
        return new CacheConfig(DEFAULT_MAX_SIZE, ttlMillis, DEFAULT_INITIAL_CAPACITY, false);
    }

    /**
     * Create a cache configuration with both LRU and TTL.
     */
    public static CacheConfig lruAndTtlConfig(int maxSize, long ttlMillis) {
        return new CacheConfig(maxSize, ttlMillis, DEFAULT_INITIAL_CAPACITY, true);
    }

    public static class Builder {
        private int maxSize = DEFAULT_MAX_SIZE;
        private long ttlMillis = DEFAULT_TTL_MILLIS;
        private int initialCapacity = DEFAULT_INITIAL_CAPACITY;
        private boolean lruEnabled = false;

        public Builder maxSize(int maxSize) {
            this.maxSize = maxSize;
            return this;
        }

        public Builder ttlMillis(long ttlMillis) {
            this.ttlMillis = ttlMillis;
            return this;
        }

        public Builder ttl(long duration, TimeUnit unit) {
            this.ttlMillis = unit.toMillis(duration);
            return this;
        }

        public Builder initialCapacity(int initialCapacity) {
            this.initialCapacity = initialCapacity;
            return this;
        }

        public Builder lruEnabled(boolean lruEnabled) {
            this.lruEnabled = lruEnabled;
            return this;
        }

        public CacheConfig build() {
            return new CacheConfig(maxSize, ttlMillis, initialCapacity, lruEnabled);
        }
    }
}
