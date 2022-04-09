package com.alibaba.qlexpress4.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author TaoKan
 * @Date 2022/4/7 下午3:12
 */
public class LongTermCache<K,V> implements ICache<K,V> {
    private int size;
    private Map<K,V> longTerm;

    public LongTermCache(int size){
        init(size);
    }

    @Override
    public void init(int size) {
        this.size = size;
        this.longTerm = new ConcurrentHashMap(size);
    }

    @Override
    public V get(K k) {
        return this.longTerm.get(k);
    }

    @Override
    public void put(K k, V v) {
        this.longTerm.put(k,v);
    }
}
