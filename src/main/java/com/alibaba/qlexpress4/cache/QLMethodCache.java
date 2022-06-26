package com.alibaba.qlexpress4.cache;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @Author TaoKan
 * @Date 2022/6/26 下午4:42
 */
public class QLMethodCache implements IBizCache<List<Method>,QLMethodCache> {
    private ICache<String, List<Method>> METHOD_CACHE = null;

    @Override
    public QLMethodCache initCache(int size, boolean enableUseCacheClear) {
        METHOD_CACHE = CacheFactory.cacheBuilder(size, enableUseCacheClear);
        return this;
    }

    @Override
    public List<Method> getCacheElement(String key) {
        return METHOD_CACHE.get(key);
    }

    @Override
    public void setCacheElement(String key, List<Method> value) {
        METHOD_CACHE.put(key, value);
    }

}
