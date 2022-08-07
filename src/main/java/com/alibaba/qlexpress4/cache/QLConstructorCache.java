package com.alibaba.qlexpress4.cache;

import com.alibaba.qlexpress4.runtime.data.implicit.QLImplicitConstructor;

/**
 * @Author TaoKan
 * @Date 2022/6/26 下午4:40
 */
public class QLConstructorCache implements IBizCache<QLImplicitConstructor,QLConstructorCache> {
    private ICache<String, QLImplicitConstructor> CONSTRUCTOR_CACHE = null;


    @Override
    public QLConstructorCache initCache(int size, boolean enableUseCacheClear) {
        CONSTRUCTOR_CACHE = CacheFactory.cacheBuilder(size, enableUseCacheClear);
        return this;
    }

    @Override
    public QLImplicitConstructor getCacheElement(String key) {
        return CONSTRUCTOR_CACHE.get(key);
    }

    @Override
    public void setCacheElement(String key, QLImplicitConstructor value) {
        CONSTRUCTOR_CACHE.put(key, value);
    }


}
