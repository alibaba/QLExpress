package com.alibaba.qlexpress4.cache;

import java.lang.reflect.Constructor;



/**
 * @Author TaoKan
 * @Date 2022/4/7 下午5:20
 */
public class ConstructorCacheElement implements ICacheElement<Constructor<?>> {
    private ICache<String, Constructor<?>> CONSTR_CACHE = null;


    @Override
    public void initCache(int size, boolean enableUseCacheClear) {
        CONSTR_CACHE = CacheFactory.cacheBuilder(size, enableUseCacheClear);
    }

    @Override
    public Constructor<?> getCacheElement(String key) {
        return CONSTR_CACHE.get(key);
    }

    @Override
    public void setCacheElement(String key, Constructor<?> value) {
        CONSTR_CACHE.put(key, value);
    }


}
