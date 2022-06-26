package com.alibaba.qlexpress4.cache;

/**
 * @Author TaoKan
 * @Date 2022/6/26 下午4:47
 */
public class QLScriptCache implements IBizCache<String,QLScriptCache> {
    private ICache<String, String> SCRIPT_CACHE = null;

    @Override
    public QLScriptCache initCache(int size, boolean enableUseCacheClear) {
        SCRIPT_CACHE = CacheFactory.cacheBuilder(size, enableUseCacheClear);
        return this;
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