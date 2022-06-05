package com.alibaba.qlexpress4.cache;


import com.alibaba.qlexpress4.runtime.data.cache.CacheFieldValue;

/**
 * @Author TaoKan
 * @Date 2022/4/7 下午5:20
 */
public class FieldCacheElement implements ICacheElement<CacheFieldValue> {
    private ICache<String, CacheFieldValue> FIELD_CACHE = null;

    @Override
    public void initCache(int size, boolean enableUseCacheClear) {
        FIELD_CACHE = CacheFactory.cacheBuilder(size, enableUseCacheClear);
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
