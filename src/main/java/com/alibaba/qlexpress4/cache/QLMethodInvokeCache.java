package com.alibaba.qlexpress4.cache;

import com.alibaba.qlexpress4.runtime.data.implicit.QLImplicitMethod;

/**
 * @Author TaoKan
 * @Date 2022/6/26 下午4:47
 */
public class QLMethodInvokeCache implements IBizCache<QLImplicitMethod,QLMethodInvokeCache> {
    private ICache<String, QLImplicitMethod> METHOD_CACHE = null;

    @Override
    public QLMethodInvokeCache initCache(int size, boolean enableUseCacheClear) {
        METHOD_CACHE = CacheFactory.cacheBuilder(size, enableUseCacheClear);
        return this;
    }

    @Override
    public QLImplicitMethod getCacheElement(String key) {
        return METHOD_CACHE.get(key);
    }

    @Override
    public void setCacheElement(String key, QLImplicitMethod value) {
        METHOD_CACHE.put(key, value);
    }

}
