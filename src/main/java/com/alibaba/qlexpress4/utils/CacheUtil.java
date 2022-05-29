package com.alibaba.qlexpress4.utils;

import com.alibaba.qlexpress4.cache.*;
import com.alibaba.qlexpress4.member.MethodHandler;

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
    public static final FunctionCacheElement functionCacheElement = new FunctionCacheElement();
    public static final com.alibaba.qlexpress4.cache.ScriptCacheElement ScriptCacheElement = new ScriptCacheElement();


    public static void initCache(int size, boolean enableUseCacheClear) {
        fieldCacheElement.initCache(size, false);
        constructorCacheElement.initCache(size, false);
        functionCacheElement.initCache(size, false);
        methodCacheElement.initCache(size, false);
        ScriptCacheElement.initCache(size, enableUseCacheClear);
    }


    public static Object getFieldCacheElement(Class<?> bean, String name) {
        String key = fieldCacheElement.buildCacheKey(bean, name, null);
        return fieldCacheElement.getCacheElement(key);
    }


    public static void setFieldCacheElement(Class<?> bean, String name, Object value) {
        String key = (String) fieldCacheElement.buildCacheKey(bean, name, null);
        fieldCacheElement.setCacheElement(key, value);
    }


    public static Object getMethodCacheElement(Object bean, String methodName, Class<?>[] type) {
        String key = (String) methodCacheElement.buildCacheKey((Class<?>) bean, methodName, type);
        return methodCacheElement.getCacheElement(key);
    }

    public static Object getMethodCacheElement(Object bean, String methodName) {
        return getMethodCacheElement(bean, methodName, null);
    }

    public static void setMethodCacheElement(Object bean, String methodName, Object value) {
        setMethodCacheElement(bean, methodName, value, null);
    }

    public static void setMethodCacheElement(Object bean, String methodName, Object value, Class<?>[] type) {
        String key = methodCacheElement.buildCacheKey((Class<?>) bean, methodName, type);
        methodCacheElement.setCacheElement(key, value);
    }

    public static Constructor<?> getConstructorCacheElement(Class<?> baseClass, Class<?>[] types) {
        String key = constructorCacheElement.buildCacheKey(baseClass, BasicUtil.NEW, types);
        return (Constructor<?>) constructorCacheElement.getCacheElement(key);
    }

    public static void setConstructorCacheElement(Class<?> baseClass, Class<?>[] types, Object value) {
        String key = constructorCacheElement.buildCacheKey(baseClass, BasicUtil.NEW, types);
        constructorCacheElement.setCacheElement(key, value);
    }

    public static boolean isFunctionInterface(Class<?> clazz) {
        if (clazz == null) {
            return false;
        }
        return functionCacheElement.cacheFunctionInterface(clazz);
    }

    private static List<Method> getStaticCacheMethodFromProperty(Object bean, String methodName, Object[] args, Class<?>[] type, boolean allowAccessPrivate) throws InvocationTargetException, IllegalAccessException {
        return PropertiesUtil.getClzMethod((Class<?>) bean, methodName, allowAccessPrivate);
    }

    private static Object getStaticCacheMethodValueFromProperty(Object bean, String methodName, Object[] args, Class<?>[] type, boolean allowAccessPrivate) throws InvocationTargetException, IllegalAccessException {
        List<Method> methods = getStaticCacheMethodFromProperty(bean, methodName, args, type, allowAccessPrivate);
        Method method = MethodHandler.Preferred.findMostSpecificMethod(type, methods.toArray(new Method[0]));
        if (method == null) {
            return null;
        }
        return MethodHandler.Access.accessMethodValue(method, bean, args, allowAccessPrivate);
    }

    private static List<Method> getCacheMethodFromProperty(Object bean, String methodName, Object[] args, Class<?>[] type, boolean allowAccessPrivate) throws InvocationTargetException, IllegalAccessException {
        return PropertiesUtil.getMethod(bean, methodName, allowAccessPrivate);
    }

    private static Object getCacheMethodValueFromProperty(Object bean, String methodName, Object[] args, Class<?>[] type, boolean allowAccessPrivate) throws InvocationTargetException, IllegalAccessException {
        List<Method> methods = getCacheMethodFromProperty(bean, methodName, args, type, allowAccessPrivate);
        Method method = MethodHandler.Preferred.findMostSpecificMethod(type, methods.toArray(new Method[0]));
        if (method == null) {
            return null;
        }
        return MethodHandler.Access.accessMethodValue(method, bean, args, allowAccessPrivate);
    }

}
