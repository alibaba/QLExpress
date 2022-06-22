package com.alibaba.qlexpress4.cache;

import com.alibaba.qlexpress4.utils.BasicUtil;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


/**
 * @Author TaoKan
 * @Date 2022/4/7 下午5:20
 */
public class MethodCacheElement implements ICacheElement<List<Method>> {
    private ICache<String, List<Method>> METHOD_CACHE = null;

    @Override
    public void initCache(int size, boolean enableUseCacheClear) {
        METHOD_CACHE = CacheFactory.cacheBuilder(size, enableUseCacheClear);
    }

    @Override
    public List<Method> getCacheElement(String key) {
        return METHOD_CACHE.get(key);
    }

    @Override
    public void setCacheElement(String key, List<Method> value) {
        METHOD_CACHE.put(key, value);
    }

}
