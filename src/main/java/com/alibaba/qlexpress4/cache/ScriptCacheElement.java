package com.alibaba.qlexpress4.cache;


/**
 * @Author TaoKan
 * @Date 2022/5/15 下午8:29
 */
public class ScriptCacheElement implements ICacheElement {
    private ICache<String, Object> SCRIPT_CACHE = null;

    @Override
    public void initCache(int size, boolean enableUseCacheClear) {
        SCRIPT_CACHE = CacheFactory.cacheBuilder(size, enableUseCacheClear);
    }

    @Override
    @Deprecated
    public String buildCacheKey(Class<?> baseClass, String name, Class<?>[] types) {
        return name;
    }

    @Override
    public Object getCacheElement(String key) {
        return SCRIPT_CACHE.get(key);
    }

    @Override
    public void setCacheElement(String key, Object value) {
        SCRIPT_CACHE.put(key, value);
    }
}