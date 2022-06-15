package com.alibaba.qlexpress4.cache;

import java.lang.reflect.Constructor;



/**
 * @Author TaoKan
 * @Date 2022/4/7 下午5:20
 */
public class ConstructorCacheElement implements ICacheElement<Constructor<?>> {
    private ICache<String, Constructor<?>> CONSTRUCTOR_CACHE = null;


    @Override
    public void initCache(int size, boolean enableUseCacheClear) {
        CONSTRUCTOR_CACHE = CacheFactory.cacheBuilder(size, enableUseCacheClear);
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
