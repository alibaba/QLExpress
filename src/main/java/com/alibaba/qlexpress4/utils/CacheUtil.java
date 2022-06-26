package com.alibaba.qlexpress4.utils;

import com.alibaba.qlexpress4.cache.*;
import com.alibaba.qlexpress4.runtime.data.cache.CacheFieldValue;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;

/**
 * @Author TaoKan
 * @Date 2022/5/28 下午5:21
 */
public class CacheUtil {
    public static QLFieldCache initFieldCache(int size, boolean enableUseCacheClear) {
        return new QLFieldCache().initCache(size, enableUseCacheClear);
    }

    public static QLConstructorCache initConstructorCache(int size, boolean enableUseCacheClear) {
        return new QLConstructorCache().initCache(size, enableUseCacheClear);
    }

    public static QLMethodCache initMethodCache(int size, boolean enableUseCacheClear) {
        return new QLMethodCache().initCache(size, enableUseCacheClear);
    }

    public static QLMethodInvokeCache initMethodInvokeCache(int size, boolean enableUseCacheClear) {
        return new QLMethodInvokeCache().initCache(size, enableUseCacheClear);
    }

    public static QLFunctionCache initFunctionCache(int size, boolean enableUseCacheClear) {
        return new QLFunctionCache().initCache(size, enableUseCacheClear);
    }

    public static QLScriptCache initScriptCache(int size, boolean enableUseCacheClear) {
        return new QLScriptCache().initCache(size, enableUseCacheClear);
    }

    public static CacheFieldValue getFieldCacheElement(QLFieldCache qlFieldCache, Class<?> bean, String name) {
        String key = qlFieldCache.buildCacheKey(bean, name, null);
        return qlFieldCache.getCacheElement(key);
    }

    public static void setFieldCacheElement(QLFieldCache qlFieldCache, Class<?> bean, String name, CacheFieldValue value) {
        String key = qlFieldCache.buildCacheKey(bean, name, null);
        qlFieldCache.setCacheElement(key, value);
    }


    public static List<Method> getMethodCacheElement(QLMethodCache qlMethodCache, Class<?> bean, String methodName, Class<?>[] type) {
        String key = qlMethodCache.buildCacheKey(bean, methodName, type);
        return qlMethodCache.getCacheElement(key);
    }

    public static Method getMethodInvokeCacheElement(QLMethodInvokeCache qlMethodInvokeCache, Class<?> bean, String methodName, Class<?>[] type) {
        String key = qlMethodInvokeCache.buildCacheKey(bean, methodName, type);
        return qlMethodInvokeCache.getCacheElement(key);
    }

    public static List<Method> getMethodCacheElement(QLMethodCache qlMethodCache, Class<?> bean, String methodName) {
        return getMethodCacheElement(qlMethodCache, bean, methodName, null);
    }

    public static void setMethodCacheElement(QLMethodCache qlMethodCache,
                                             Class<?> bean, String methodName, List<Method> value) {
        setMethodCacheElement(qlMethodCache, bean, methodName, value, null);
    }

    public static void setMethodCacheElement(QLMethodCache qlMethodCache,
                                             Class<?> bean, String methodName, List<Method> value, Class<?>[] type) {
        String key = qlMethodCache.buildCacheKey(bean, methodName, type);
        qlMethodCache.setCacheElement(key, value);
    }

    public static void setMethodInvokeCacheElement(QLMethodInvokeCache qlMethodInvokeCache,
                                                   Class<?> bean, String methodName, Method value, Class<?>[] type) {
        String key = qlMethodInvokeCache.buildCacheKey(bean, methodName, type);
        qlMethodInvokeCache.setCacheElement(key, value);
    }

    public static Constructor<?> getConstructorCacheElement(QLConstructorCache qlConstructorCache,
                                                            Class<?> baseClass, Class<?>[] types) {
        String key = qlConstructorCache.buildCacheKey(baseClass, BasicUtil.NEW, types);
        return qlConstructorCache.getCacheElement(key);
    }

    public static void setConstructorCacheElement(QLConstructorCache qlConstructorCache,
                                                  Class<?> baseClass, Class<?>[] types, Constructor<?> value) {
        String key = qlConstructorCache.buildCacheKey(baseClass, BasicUtil.NEW, types);
        qlConstructorCache.setCacheElement(key, value);
    }

    public static boolean isFunctionInterface(QLFunctionCache qlFunctionCache, Class<?> clazz) {
        if (clazz == null) {
            return false;
        }
        return qlFunctionCache.cacheFunctionInterface(clazz);
    }
}
