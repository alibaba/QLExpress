package com.alibaba.qlexpress4.cache;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * @Author TaoKan
 * @Date 2022/6/26 下午4:37
 */
public interface IBizCache<E,P> {
    P initCache(int size, boolean enableUseCacheClear);

    E getCacheElement(String key);

    void setCacheElement(String key, E value);

    default String buildCacheKey(Class<?> baseClass, String name, Class<?>[] types){
        StringBuilder builder = new StringBuilder();
        builder.append(baseClass.getName()).append("#").append(name).append(";");
        if (types == null || types.length == 0) {
            return builder.toString();
        }
        return builder.append(Arrays.stream(types).map(v->v==null?"null":v.toString()).collect(Collectors.joining(","))).toString();
    }
}
