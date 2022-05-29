package com.alibaba.qlexpress4.cache;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @Author TaoKan
 * @Date 2022/5/28 下午3:48
 */
public class LRUHashMap<K, V> extends LinkedHashMap<K, V> {
    private final float DEFAULT_LOAD_FACTOR = 0.75f;
    private int size;
    private LinkedHashMap<K, V> linkedHashMap;


    public LRUHashMap(int size) {
        this.size = size;
        int capacity = (int) Math.ceil(size / DEFAULT_LOAD_FACTOR) + 1;
        linkedHashMap = new LinkedHashMap(capacity, DEFAULT_LOAD_FACTOR, true);
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return size() > size;
    }

    public synchronized V getElement(K key) {
        return linkedHashMap.get(key);
    }

    public synchronized void putElement(K key, V value) {
        linkedHashMap.put(key, value);
    }
}
