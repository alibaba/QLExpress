package com.alibaba.qlexpress4.cache;

import java.lang.reflect.Constructor;

/**
 * @Author TaoKan
 * @Date 2022/6/26 下午4:40
 */
public class QLConstructorCache implements IBizCache<Constructor<?>,QLConstructorCache> {
    private ICache<String, Constructor<?>> CONSTRUCTOR_CACHE = null;


    @Override
    public QLConstructorCache initCache(int size, boolean enableUseCacheClear) {
        CONSTRUCTOR_CACHE = CacheFactory.cacheBuilder(size, enableUseCacheClear);
        return this;
    }

    @Override
    public Constructor<?> getCacheElement(String key) {
        return CONSTRUCTOR_CACHE.get(key);
    }

    @Override
    public void setCacheElement(String key, Constructor<?> value) {
        CONSTRUCTOR_CACHE.put(key, value);
    }


}
