package com.alibaba.qlexpress4.cache;

import com.alibaba.qlexpress4.member.MethodHandler;

/**
 * @Author TaoKan
 * @Date 2022/6/26 下午4:46
 */
public class QLFunctionCache implements IBizCache<Object,QLFunctionCache> {
    private ICache<Object, Boolean> FUNCTION_CACHE = null;

    @Override
    public QLFunctionCache initCache(int size, boolean enableUseCacheClear) {
        FUNCTION_CACHE = CacheFactory.cacheBuilder(size, enableUseCacheClear);
        return this;
    }

    @Override
    public Object getCacheElement(String key) {
        return null;
    }

    @Override
    public void setCacheElement(String key, Object value) {
    }

    public boolean cacheFunctionInterface(Class<?> clazz) {
        Boolean cacheRes = FUNCTION_CACHE.get(clazz);
        if (cacheRes != null) {
            return cacheRes;
        }
        boolean res = clazz.isInterface() && MethodHandler.hasOnlyOneAbstractMethod(clazz.getMethods());
        FUNCTION_CACHE.put(clazz, res);
        return res;

    }
}
