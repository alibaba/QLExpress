package com.alibaba.qlexpress4.cache;


/**
 * @Author TaoKan
 * @Date 2022/4/7 下午5:20
 */
public class FieldCacheElement implements ICacheElement{
    private ICache<String, Object> FIELD_CACHE = null;

    @Override
    public void initCache(int size, boolean isUseCacheClear) {
        FIELD_CACHE = CacheFactory.cacheBuilder(size,isUseCacheClear);
    }


    @Override
    public Object buildCacheKey(Class<?> baseClass, String propertyName, Class<?>[] types) {
        StringBuilder builder = new StringBuilder();
        builder.append(baseClass.getName())
                .append("#")
                .append(propertyName)
                .append(";");
        return builder.toString();
    }

    @Override
    public Object getCacheElement(String key) {
        return FIELD_CACHE.get(key);
    }

    @Override
    public void setCacheElement(String key, Object value) {
        FIELD_CACHE.put(key,value);
    }

}
