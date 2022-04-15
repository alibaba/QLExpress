package com.alibaba.qlexpress4.cache;

import java.lang.reflect.Member;

/**
 * @Author TaoKan
 * @Date 2022/4/7 下午5:18
 */
public interface ICacheElement {
    void initCache(int size, boolean isUseCacheClear);

    Object buildCacheKey(Class<?> baseClass, String name, Class<?>[] types);

    Member getCacheElement(String key, Class<?> baseClass, String name, Class<?>[] types, boolean publicOnly, boolean isStatic);

    Member getElement(Class<?> baseClass, String name, Class<?>[] types, boolean publicOnly, boolean isStatic);
}
