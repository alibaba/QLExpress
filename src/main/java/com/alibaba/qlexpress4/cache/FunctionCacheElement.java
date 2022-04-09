package com.alibaba.qlexpress4.cache;

import com.alibaba.qlexpress4.member.MethodHandler;

import java.lang.reflect.Member;

/**
 * @Author TaoKan
 * @Date 2022/4/7 下午7:58
 */
public class FunctionCacheElement implements ICacheElement {
    private static final ICache<Object, Boolean> FUNCTION_CACHE = CacheFactory.cacheBuilder(128);

    @Override
    public Object buildCacheKey(Class<?> baseClass, String name, Class<?>[] types) {
        return null;
    }

    @Override
    public Member getCacheElement(String key, Class<?> baseClass, String name, Class<?>[] types, boolean publicOnly, boolean isStatic) {
        return null;
    }

    @Override
    public Member getElement(Class<?> baseClass, String name, Class<?>[] types, boolean publicOnly, boolean isStatic) {
        return null;
    }


    public boolean cacheFunctionInterface(Class<?> clazz){
        Boolean cacheRes = FUNCTION_CACHE.get(clazz);
        if (cacheRes != null) {
            return cacheRes;
        }
        boolean res = clazz.isInterface() && MethodHandler.hasOnlyOneAbstractMethod(clazz.getMethods());
        FUNCTION_CACHE.put(clazz, res);
        return res;

    }
}
