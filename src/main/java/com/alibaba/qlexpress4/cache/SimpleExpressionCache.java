package com.alibaba.qlexpress4.cache;

import com.alibaba.qlexpress4.aparser.QCompileCache;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

/**
 * Simple unlimited expression cache implementation.
 * Uses ConcurrentHashMap for thread-safe storage.
 * <p>
 * This implementation has no size limit or expiration mechanism.
 * Suitable for scenarios with limited and predictable number of expressions.
 *
 * @author QLExpress Team
 */
public class SimpleExpressionCache implements ExpressionCache {

    private final ConcurrentHashMap<String, Future<QCompileCache>> cache;

    public SimpleExpressionCache() {
        this.cache = new ConcurrentHashMap<>();
    }

    public SimpleExpressionCache(int initialCapacity) {
        this.cache = new ConcurrentHashMap<>(initialCapacity);
    }

    @Override
    public Future<QCompileCache> get(String script) {
        return cache.get(script);
    }

    @Override
    public Future<QCompileCache> put(String script, Future<QCompileCache> future) {
        return cache.put(script, future);
    }

    @Override
    public Future<QCompileCache> putIfAbsent(String script, Future<QCompileCache> future) {
        return cache.putIfAbsent(script, future);
    }

    @Override
    public Future<QCompileCache> remove(String script) {
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
}
