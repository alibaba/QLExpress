package com.alibaba.qlexpress4.cache;


/**
 * @Author TaoKan
 * @Date 2022/5/15 下午8:29
 */
public class ScriptCacheElement implements ICacheElement<String> {
    private ICache<String, String> SCRIPT_CACHE = null;

    @Override
    public void initCache(int size, boolean enableUseCacheClear) {
        SCRIPT_CACHE = CacheFactory.cacheBuilder(size, enableUseCacheClear);
    }

    @Override
    public String getCacheElement(String key) {
        return SCRIPT_CACHE.get(key);
    }

    @Override
    public void setCacheElement(String key, String value) {
        SCRIPT_CACHE.put(key, value);
    }
}