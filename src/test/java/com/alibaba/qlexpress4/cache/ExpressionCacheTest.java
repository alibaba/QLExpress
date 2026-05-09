package com.alibaba.qlexpress4.cache;

import com.alibaba.qlexpress4.InitOptions;
import com.alibaba.qlexpress4.Express4Runner;
import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.QLResult;
import org.junit.Test;

import java.util.Collections;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

/**
 * Test cases for expression cache functionality.
 */
public class ExpressionCacheTest {

    @Test
    public void testDefaultCache() {
        Express4Runner runner = new Express4Runner(InitOptions.DEFAULT_OPTIONS);

        // Execute same script multiple times
        String script = "1 + 2";
        QLOptions options = QLOptions.builder().cache(true).build();

        for (int i = 0; i < 5; i++) {
            QLResult result = runner.execute(script, Collections.emptyMap(), options);
            assertEquals(3, result.getResult());
        }

        // Cache should have 1 entry
        assertEquals(1, runner.getCompileCacheSize());
    }

    @Test
    public void testLRUCache() {
        // Create cache with max size of 3
        CacheConfig config = CacheConfig.lruConfig(3);
        InitOptions initOptions = InitOptions.builder()
            .cacheConfig(config)
            .build();
        Express4Runner runner = new Express4Runner(initOptions);
        QLOptions options = QLOptions.builder().cache(true).build();

        // Add 5 different scripts (only 3 should remain)
        for (int i = 0; i < 5; i++) {
            runner.execute("return " + i, Collections.emptyMap(), options);
        }

        // Cache size should be limited to 3
        assertEquals(3, runner.getCompileCacheSize());
    }

    @Test
    public void testTTLCache() throws InterruptedException {
        // Create cache with 100ms TTL
        CacheConfig config = CacheConfig.ttlConfig(100);
        InitOptions initOptions = InitOptions.builder()
            .cacheConfig(config)
            .build();
        Express4Runner runner = new Express4Runner(initOptions);
        QLOptions options = QLOptions.builder().cache(true).build();

        String script = "1 + 2";
        runner.execute(script, Collections.emptyMap(), options);
        assertEquals(1, runner.getCompileCacheSize());

        // Wait for expiration
        Thread.sleep(200);

        // After expiration, cache entry should be expired and cleaned up
        // Accessing size triggers cleanup
        int size = runner.getCompileCacheSize();
        assertEquals(0, size);
    }

    @Test
    public void testLRUAndTTLCache() throws InterruptedException {
        // Create cache with max size 5 and 1 second TTL
        CacheConfig config = CacheConfig.lruAndTtlConfig(5, 1000);
        InitOptions initOptions = InitOptions.builder()
            .cacheConfig(config)
            .build();
        Express4Runner runner = new Express4Runner(initOptions);
        QLOptions options = QLOptions.builder().cache(true).build();

        // Add 3 scripts
        for (int i = 0; i < 3; i++) {
            runner.execute("return " + i, Collections.emptyMap(), options);
        }
        assertEquals(3, runner.getCompileCacheSize());

        // Clear cache
        runner.clearCompileCache();
        assertEquals(0, runner.getCompileCacheSize());
    }

    @Test
    public void testCustomCacheImplementation() {
        // Create a custom cache that counts operations
        AtomicInteger getCount = new AtomicInteger(0);
        AtomicInteger putCount = new AtomicInteger(0);

        ExpressionCache customCache = new ExpressionCache() {
            private final java.util.concurrent.ConcurrentHashMap<String, java.util.concurrent.Future<com.alibaba.qlexpress4.aparser.QCompileCache>> cache =
                new java.util.concurrent.ConcurrentHashMap<>();

            @Override
            public java.util.concurrent.Future<com.alibaba.qlexpress4.aparser.QCompileCache> get(String script) {
                getCount.incrementAndGet();
                return cache.get(script);
            }

            @Override
            public java.util.concurrent.Future<com.alibaba.qlexpress4.aparser.QCompileCache> put(String script, java.util.concurrent.Future<com.alibaba.qlexpress4.aparser.QCompileCache> future) {
                putCount.incrementAndGet();
                return cache.put(script, future);
            }

            @Override
            public java.util.concurrent.Future<com.alibaba.qlexpress4.aparser.QCompileCache> putIfAbsent(String script, java.util.concurrent.Future<com.alibaba.qlexpress4.aparser.QCompileCache> future) {
                putCount.incrementAndGet();
                return cache.putIfAbsent(script, future);
            }

            @Override
            public java.util.concurrent.Future<com.alibaba.qlexpress4.aparser.QCompileCache> remove(String script) {
                return cache.remove(script);
            }

            @Override
            public void clear() {
                cache.clear();
            }

            @Override
            public int size() {
                return cache.size();
            }

            @Override
            public boolean contains(String script) {
                return cache.containsKey(script);
            }
        };

        InitOptions initOptions = InitOptions.builder()
            .expressionCache(customCache)
            .build();
        Express4Runner runner = new Express4Runner(initOptions);
        QLOptions options = QLOptions.builder().cache(true).build();

        // Execute same script twice
        String script = "1 + 2";
        runner.execute(script, Collections.emptyMap(), options);
        runner.execute(script, Collections.emptyMap(), options);

        // First time: get (miss) + put
        // Second time: get (hit)
        assertTrue("Get should be called", getCount.get() >= 2);
        assertTrue("Put should be called", putCount.get() >= 1);
    }

    @Test
    public void testCacheConfigBuilder() {
        CacheConfig config = CacheConfig.builder()
            .maxSize(100)
            .ttlMillis(60000)
            .lruEnabled(true)
            .initialCapacity(16)
            .build();

        assertEquals(100, config.getMaxSize());
        assertEquals(60000, config.getTtlMillis());
        assertTrue(config.isLruEnabled());
        assertTrue(config.isMaxSizeEnabled());
        assertTrue(config.isTtlEnabled());
    }

    @Test
    public void testCacheConfigBuilderWithTimeUnit() {
        CacheConfig config = CacheConfig.builder()
            .maxSize(50)
            .ttl(5, TimeUnit.MINUTES)
            .build();

        assertEquals(50, config.getMaxSize());
        assertEquals(300000, config.getTtlMillis()); // 5 minutes in milliseconds
    }

    @Test
    public void testSimpleExpressionCache() {
        SimpleExpressionCache cache = new SimpleExpressionCache(10);
        assertEquals(0, cache.size());

        // Test put and get
        java.util.concurrent.Future<com.alibaba.qlexpress4.aparser.QCompileCache> future =
            new java.util.concurrent.FutureTask<>(() -> null);
        cache.put("script1", future);
        assertEquals(1, cache.size());
        assertTrue(cache.contains("script1"));
        assertEquals(future, cache.get("script1"));

        // Test remove
        cache.remove("script1");
        assertEquals(0, cache.size());
        assertFalse(cache.contains("script1"));

        // Test clear
        cache.put("script2", future);
        cache.clear();
        assertEquals(0, cache.size());
    }

    @Test
    public void testDefaultCacheConfig() {
        CacheConfig config = CacheConfig.defaultConfig();
        assertEquals(-1, config.getMaxSize());
        assertEquals(-1, config.getTtlMillis());
        assertFalse(config.isLruEnabled());
        assertFalse(config.isMaxSizeEnabled());
        assertFalse(config.isTtlEnabled());
    }

    @Test
    public void testCacheDisabled() {
        Express4Runner runner = new Express4Runner(InitOptions.DEFAULT_OPTIONS);
        QLOptions options = QLOptions.builder().cache(false).build();

        // Execute script without cache
        runner.execute("1 + 2", Collections.emptyMap(), options);
        assertEquals(0, runner.getCompileCacheSize());
    }
}
