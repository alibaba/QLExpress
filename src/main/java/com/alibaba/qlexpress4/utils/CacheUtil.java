package com.alibaba.qlexpress4.utils;

import com.alibaba.qlexpress4.cache.*;
import com.alibaba.qlexpress4.member.MethodHandler;
import com.alibaba.qlexpress4.runtime.data.DataValue;
import com.alibaba.qlexpress4.runtime.data.cache.CacheFieldValue;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * @Author TaoKan
 * @Date 2022/5/28 下午5:21
 */
public class CacheUtil {

    public static final FieldCacheElement fieldCacheElement = new FieldCacheElement();
    public static final ConstructorCacheElement constructorCacheElement = new ConstructorCacheElement();
    public static final MethodCacheElement methodCacheElement = new MethodCacheElement();
    public static final MethodInvokeCacheElement methodInvokeCacheElement = new MethodInvokeCacheElement();
    public static final FunctionCacheElement functionCacheElement = new FunctionCacheElement();
    public static final com.alibaba.qlexpress4.cache.ScriptCacheElement ScriptCacheElement = new ScriptCacheElement();


    public static void initCache(int size, boolean enableUseCacheClear) {
        fieldCacheElement.initCache(size, false);
        constructorCacheElement.initCache(size, false);
        functionCacheElement.initCache(size, false);
        methodInvokeCacheElement.initCache(size, false);
        methodCacheElement.initCache(size, false);
        ScriptCacheElement.initCache(size, enableUseCacheClear);
    }


    public static CacheFieldValue getFieldCacheElement(Class<?> bean, String name) {
        String key = fieldCacheElement.buildCacheKey(bean, name, null);
        return fieldCacheElement.getCacheElement(key);
    }


    public static void setFieldCacheElement(Class<?> bean, String name, CacheFieldValue value) {
        String key = fieldCacheElement.buildCacheKey(bean, name, null);
        fieldCacheElement.setCacheElement(key, value);
    }


    public static List<Method> getMethodCacheElement(Class<?> bean, String methodName, Class<?>[] type) {
        String key = methodCacheElement.buildCacheKey(bean, methodName, type);
        return methodCacheElement.getCacheElement(key);
    }

    public static Method getMethodInvokeCacheElement(Class<?> bean, String methodName, Class<?>[] type) {
        String key = methodInvokeCacheElement.buildCacheKey(bean, methodName, type);
        return methodInvokeCacheElement.getCacheElement(key);
    }

    public static List<Method> getMethodCacheElement(Class<?> bean, String methodName) {
        return getMethodCacheElement(bean, methodName, null);
    }

    public static void setMethodCacheElement(Class<?> bean, String methodName, List<Method> value) {
        setMethodCacheElement(bean, methodName, value, null);
    }

    public static void setMethodCacheElement(Class<?> bean, String methodName, List<Method> value, Class<?>[] type) {
        String key = methodCacheElement.buildCacheKey(bean, methodName, type);
        methodCacheElement.setCacheElement(key, value);
    }

    public static void setMethodInvokeCacheElement(Class<?> bean, String methodName, Method value, Class<?>[] type) {
        String key = methodInvokeCacheElement.buildCacheKey( bean, methodName, type);
        methodInvokeCacheElement.setCacheElement(key, value);
    }

    public static Constructor<?> getConstructorCacheElement(Class<?> baseClass, Class<?>[] types) {
        String key = constructorCacheElement.buildCacheKey(baseClass, BasicUtil.NEW, types);
        return constructorCacheElement.getCacheElement(key);
    }

    public static void setConstructorCacheElement(Class<?> baseClass, Class<?>[] types, Constructor<?> value) {
        String key = constructorCacheElement.buildCacheKey(baseClass, BasicUtil.NEW, types);
        constructorCacheElement.setCacheElement(key, value);
    }

    public static boolean isFunctionInterface(Class<?> clazz) {
        if (clazz == null) {
            return false;
        }
        return functionCacheElement.cacheFunctionInterface(clazz);
    }
}
