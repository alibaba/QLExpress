package com.alibaba.qlexpress4.cache;

import java.lang.reflect.Method;


/**
 * @Author TaoKan
 * @Date 2022/6/5 上午9:46
 */
public class MethodInvokeCacheElement implements ICacheElement<Method> {
    private ICache<String, Method> METHOD_CACHE = null;

    @Override
    public void initCache(int size, boolean enableUseCacheClear) {
        METHOD_CACHE = CacheFactory.cacheBuilder(size, enableUseCacheClear);
    }

    @Override
    public Method getCacheElement(String key) {
        return METHOD_CACHE.get(key);
    }

    @Override
    public void setCacheElement(String key, Method value) {
        METHOD_CACHE.put(key, value);
    }

}
