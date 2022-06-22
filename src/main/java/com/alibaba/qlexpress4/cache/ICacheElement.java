package com.alibaba.qlexpress4.cache;


import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * @Author TaoKan
 * @Date 2022/4/7 下午5:18
 */
public interface ICacheElement<V> {

    void initCache(int size, boolean enableUseCacheClear);

    V getCacheElement(String key);

    void setCacheElement(String key, V value);

    default String buildCacheKey(Class<?> baseClass, String name, Class<?>[] types){
        StringBuilder builder = new StringBuilder();
        builder.append(baseClass.getName()).append("#").append(name).append(";");
        if (types == null || types.length == 0) {
            return builder.toString();
        }
        return builder.append(Arrays.stream(types).map(v->v.toString()).collect(Collectors.joining(","))).toString();
    }
}
