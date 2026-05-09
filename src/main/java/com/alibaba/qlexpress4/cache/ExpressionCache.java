package com.alibaba.qlexpress4.cache;

import com.alibaba.qlexpress4.aparser.QCompileCache;

import java.util.concurrent.Future;

/**
 * Expression compilation cache interface.
 * Provides cache operations for compiled expression results.
 *
 * Implementations can provide different cache strategies such as:
 * - LRU (Least Recently Used) eviction
 * - TTL (Time To Live) expiration
 * - Size-based eviction
 * - Custom eviction policies
 *
 * @author QLExpress Team
 */
public interface ExpressionCache {

    /**
     * Get cached compile result for the given script.
     *
     * @param script the expression script
     * @return Future of compiled cache, or null if not cached
     */
    Future<QCompileCache> get(String script);

    /**
     * Put a compiled result into cache.
     *
     * @param script the expression script
     * @param future the future of compiled result
     * @return the previous value associated with script, or null if there was no mapping
     */
    Future<QCompileCache> put(String script, Future<QCompileCache> future);

    /**
     * Put a compiled result into cache if absent.
     *
     * @param script the expression script
     * @param future the future of compiled result
     * @return the existing value if present, or null if the new value was inserted
     */
    Future<QCompileCache> putIfAbsent(String script, Future<QCompileCache> future);

    /**
     * Remove a cached entry.
     *
     * @param script the expression script
     * @return the removed value, or null if not present
     */
    Future<QCompileCache> remove(String script);

    /**
     * Clear all cached entries.
     */
    void clear();

    /**
     * Get the current cache size.
     *
     * @return number of cached entries
     */
    int size();

    /**
     * Check if cache contains the given script.
     *
     * @param script the expression script
     * @return true if cached
     */
    boolean contains(String script);
}
