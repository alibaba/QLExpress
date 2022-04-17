package com.alibaba.qlexpress4.cache;


import com.alibaba.qlexpress4.runtime.Value;

/**
 * @Author TaoKan
 * @Date 2022/4/7 下午5:18
 */
public interface ICacheElement {

    void initCache(int size, boolean isUseCacheClear);

    Object buildCacheKey(Class<?> baseClass, String name, Class<?>[] types);

    Object getCacheElement(String key);

    void setCacheElement(String key, Object value);
}
