package com.alibaba.qlexpress4.cache;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @Author TaoKan
 * @Date 2022/5/28 下午3:10
 */
public class LRUCache<K, V> implements ICache<K, V> {
    private LinkedHashMap<K,V> linkedHashMap;
    private int cacheSize;
    private ReentrantReadWriteLock lock;


    public LRUCache(int size){
        init(size);
    }

    public  void put(K key,V value){
        this.lock.writeLock().lock();
        try {
            linkedHashMap.put(key,value);
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    @Override
    public void init(int size) {
        this.cacheSize = size;
        this.lock = new ReentrantReadWriteLock();
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

    public V get(K key){
        this.lock.readLock().lock();
        try {
            return linkedHashMap.get(key);
        } finally {
            this.lock.readLock().unlock();
        }
    }

    public int size(){
        this.lock.readLock().lock();
        try {
            return linkedHashMap.size();
        } finally {
            this.lock.readLock().unlock();
        }
    }
}
