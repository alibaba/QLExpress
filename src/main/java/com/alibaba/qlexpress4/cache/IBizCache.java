package com.alibaba.qlexpress4.cache;

import com.alibaba.qlexpress4.utils.BasicUtil;

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
        builder.append(baseClass.getName()).append(BasicUtil.SPLIT_CLASS).append(name).append(BasicUtil.SPLIT_NAME);
        if (types == null || types.length == 0) {
            return builder.toString();
        }
        return builder.append(Arrays.stream(types).map(v->v==null? BasicUtil.NULL_SIGN :v.toString()).collect(Collectors.joining(BasicUtil.SPLIT_COLLECTOR))).toString();
    }
}
