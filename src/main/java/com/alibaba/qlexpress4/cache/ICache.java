package com.alibaba.qlexpress4.cache;

/**
 * @Author TaoKan
 * @Date 2022/4/7 下午3:12
 */
public interface ICache<K,V> {
    /**
     * cache init
     * @param size
     */
    void init(int size);

    /**
     * cache get element
     * @param k
     * @return
     */
    V get(K k);

    /**
     * cache put element
     * @param k
     * @param v
     */
    void put(K k, V v);
}
