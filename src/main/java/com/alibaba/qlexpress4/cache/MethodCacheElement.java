package com.alibaba.qlexpress4.cache;

import com.alibaba.qlexpress4.utils.BasicUtil;


/**
 * @Author TaoKan
 * @Date 2022/4/7 下午5:20
 */
public class MethodCacheElement implements ICacheElement {
    private ICache<String, Object> METHOD_CACHE = null;

    @Override
    public void initCache(int size, boolean enableUseCacheClear) {
        METHOD_CACHE = CacheFactory.cacheBuilder(size, enableUseCacheClear);
    }

    @Override
    public String buildCacheKey(Class<?> baseClass, String propertyName, Class<?>[] types) {
        StringBuilder builder = new StringBuilder();
        builder.append(baseClass.getName())
                .append("#")
                .append(propertyName)
                .append(";");

        if (types == null || types.length == 0) {
            return builder.toString();
        }

        for (Class clazz : types) {
            if (clazz == null) {
                builder.append(BasicUtil.NULL_SIGN);
            } else {
                builder.append(clazz.getName());
            }
            builder.append(",");
        }

        String result = builder.toString();
        return result.substring(0, result.length() - 1);
    }

    @Override
    public Object getCacheElement(String key) {
        return METHOD_CACHE.get(key);
    }

    @Override
    public void setCacheElement(String key, Object value) {
        METHOD_CACHE.put(key, value);
    }

}
