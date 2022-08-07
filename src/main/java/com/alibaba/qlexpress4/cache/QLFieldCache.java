package com.alibaba.qlexpress4.cache;

import com.alibaba.qlexpress4.runtime.data.cache.CacheFieldValue;

/**
 * @Author TaoKan
 * @Date 2022/6/26 下午4:45
 */
public class QLFieldCache implements IBizCache<CacheFieldValue,QLFieldCache> {
    private ICache<String, CacheFieldValue> FIELD_CACHE = null;

    @Override
    public QLFieldCache initCache(int size, boolean enableUseCacheClear) {
        FIELD_CACHE = CacheFactory.cacheBuilder(size, enableUseCacheClear);
        return this;
    }

    @Override
    public CacheFieldValue getCacheElement(String key) {
        return FIELD_CACHE.get(key);
    }

    @Override
    public void setCacheElement(String key, CacheFieldValue value) {
        FIELD_CACHE.put(key, value);
    }

}
