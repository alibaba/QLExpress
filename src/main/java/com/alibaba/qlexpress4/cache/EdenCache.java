package com.alibaba.qlexpress4.cache;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author TaoKan
 * @Date 2022/4/7 下午3:12
 */
public class EdenCache<K,V> implements ICache<K,V> {
    private int size;
    private Map<K,V> eden;
    private Map<K,V> longTerm;

    public EdenCache(int size){
        init(size);
    }

    @Override
    public void init(int size) {
        this.size = size;
        this.eden = new ConcurrentHashMap(size);
        this.longTerm = new WeakHashMap(size);
    }

    @Override
    public V get(K k) {
        V v = this.eden.get(k);
        if (v == null) {
            v = this.longTerm.get(k);
            if (v != null) {
                this.eden.put(k, v);
            }
        }
        return v;
    }

    @Override
    public void put(K k, V v) {
        if (this.eden.size() >= size) {
            this.longTerm.putAll(this.eden);
            this.eden.clear();
        }
        this.eden.put(k, v);
    }
}
