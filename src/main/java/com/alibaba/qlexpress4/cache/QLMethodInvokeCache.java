package com.alibaba.qlexpress4.cache;

import java.lang.reflect.Method;

/**
 * @Author TaoKan
 * @Date 2022/6/26 下午4:47
 */
public class QLMethodInvokeCache implements IBizCache<Method,QLMethodInvokeCache> {
    private ICache<String, Method> METHOD_CACHE = null;

    @Override
    public QLMethodInvokeCache initCache(int size, boolean enableUseCacheClear) {
        METHOD_CACHE = CacheFactory.cacheBuilder(size, enableUseCacheClear);
        return this;
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
