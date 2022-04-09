package com.alibaba.qlexpress4.cache;

import com.alibaba.qlexpress4.member.FieldHandler;
import com.alibaba.qlexpress4.utils.QLAliasUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Member;

/**
 * @Author TaoKan
 * @Date 2022/4/7 下午5:20
 */
public class FieldCacheElement implements ICacheElement{
    private static final ICache<String, Object> FIELD_CACHE = CacheFactory.cacheBuilder(128);

    @Override
    public Object buildCacheKey(Class<?> baseClass, String propertyName, Class<?>[] types) {
        StringBuilder builder = new StringBuilder();
        builder.append(baseClass.getName())
                .append("#")
                .append(propertyName)
                .append(".");
        return builder.toString();
    }

    @Override
    public Member getCacheElement(String key, Class<?> baseClass, String propertyName, Class<?>[] types, boolean publicOnly, boolean isStatic) {
        Object result = FIELD_CACHE.get(key);
        if (result == null) {
            result = getElement(baseClass, propertyName, types, publicOnly , isStatic);
            if (result == null) {
                FIELD_CACHE.put(key, void.class);
            } else {
                FIELD_CACHE.put(key, result);
            }
        } else if (result == void.class) {
            result = null;
        }
        return (Field)result;
    }

    @Override
    public Member getElement(Class<?> baseClass, String propertyName, Class<?>[] types, boolean publicOnly, boolean isStatic) {
        return FieldHandler.Preferred.gatherFieldRecursive(baseClass, propertyName, types, publicOnly, isStatic);
    }
}
