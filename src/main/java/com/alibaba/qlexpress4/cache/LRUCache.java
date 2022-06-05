package com.alibaba.qlexpress4.cache;

/**
 * @Author TaoKan
 * @Date 2022/5/28 下午3:10
 */
public class LRUCache<K, V> implements ICache<K, V> {
    private LRUHashMap<K, V> longTerm;

    public LRUCache(int size) {
        init(size);
    }

    @Override
    public void init(int size) {
        this.longTerm = new LRUHashMap(size);
    }

    @Override
    public V get(K k) {
        return this.longTerm.getElement(k);
    }

    @Override
    public void put(K k, V v) {
        this.longTerm.putElement(k, v);
    }


    public int size(){
        return this.longTerm.size();
    }
}
