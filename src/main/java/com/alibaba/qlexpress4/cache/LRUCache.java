package com.alibaba.qlexpress4.cache;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Author TaoKan
 * @Date 2022/5/28 下午3:10
 */
public class LRUCache<K, V> implements ICache<K, V> {
    private LinkedHashMap<K,V> linkedHashMap;
    private int cacheSize;
    private Lock lock;


    public LRUCache(int size){
        init(size);
    }

    @Override
    public void init(int size) {
        this.cacheSize = size;
        this.lock = new ReentrantLock();
        this.linkedHashMap = new LinkedHashMap<K,V>(){
            @Override
            protected boolean removeEldestEntry(Map.Entry<K,V> eldest) {
                if(cacheSize + 1 == linkedHashMap.size()){
                    return true;
                }else {
                    return false;
                }
            }
        };
    }

    @Override
    public void put(K k, V v) {
        this.lock.lock();
        try {
            linkedHashMap.put(k, v);
        } finally {
            this.lock.unlock();
        }
    }

    @Override
    public V get(K key){
        this.lock.lock();
        try {
            return linkedHashMap.get(key);
        } finally {
            this.lock.unlock();
        }
    }


    public int size(){
        this.lock.lock();
        try {
            return linkedHashMap.size();
        } finally {
            this.lock.unlock();
        }
    }
}
