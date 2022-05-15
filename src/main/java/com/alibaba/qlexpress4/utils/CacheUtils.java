package com.alibaba.qlexpress4.utils;

import com.alibaba.qlexpress4.cache.*;
import com.alibaba.qlexpress4.member.ConstructorHandler;
import com.alibaba.qlexpress4.member.MethodHandler;

import java.lang.reflect.*;
import java.util.List;

/**
 * @Author TaoKan
 * @Date 2022/4/17 下午12:28
 */
public class CacheUtils {

    public static final FieldCacheElement fieldCacheElement = new FieldCacheElement();
    public static final ConstructorCacheElement constructorCacheElement = new ConstructorCacheElement();
    public static final MethodCacheElement methodCacheElement = new MethodCacheElement();
    public static final FunctionCacheElement functionCacheElement = new FunctionCacheElement();
    public static final ScriptCacheElement ScriptCacheElement = new ScriptCacheElement();


    public static void initCache(int size,boolean isUseCacheClear){
        fieldCacheElement.initCache(size, false);
        constructorCacheElement.initCache(size, false);
        functionCacheElement.initCache(size, false);
        methodCacheElement.initCache(size, false);
        ScriptCacheElement.initCache(size,isUseCacheClear);
    }


    public static Object getFieldCacheElement(Class<?> bean, String name){
        String key = (String)CacheUtils.fieldCacheElement.buildCacheKey(bean ,name, null);
        return CacheUtils.fieldCacheElement.getCacheElement(key);
    }



    public static void setFieldCacheElement(Class<?> bean, String name, Object value){
        String key = (String)fieldCacheElement.buildCacheKey(bean ,name, null);
        fieldCacheElement.setCacheElement(key,value);
    }


    public static Object getMethodCacheElement(Object bean, String methodName, Class<?>[] type){
        String key = (String)methodCacheElement.buildCacheKey((Class<?>)bean ,methodName, type);
        return methodCacheElement.getCacheElement(key);
    }

    public static Object getMethodCacheElement(Object bean, String methodName){
        return getMethodCacheElement(bean, methodName, null);
    }

    public static void setMethodCacheElement(Object bean, String methodName, Object value){
        setMethodCacheElement(bean, methodName,value, null);
    }

    public static void setMethodCacheElement(Object bean, String methodName, Object value, Class<?>[] type){
        String key = (String)methodCacheElement.buildCacheKey((Class<?>)bean ,methodName, type);
        methodCacheElement.setCacheElement(key,value);
    }

    public static Constructor<?> getConstructorWithCache(Class<?> baseClass, Class<?>[] types) {
        String key = (String)constructorCacheElement.buildCacheKey(baseClass ,BasicUtils.NEW, types);
        Object cacheElement = constructorCacheElement.getCacheElement(key);
        if(cacheElement == null){
            cacheElement = ConstructorHandler.Preferred.findConstructorMostSpecificSignature(baseClass, null, null, false, false);
        }
        return (Constructor<?>)cacheElement;
    }

    public static boolean isFunctionInterface(Class<?> clazz){
        if (clazz == null) {
            return false;
        }
        return functionCacheElement.cacheFunctionInterface(clazz);
    }

    private static List<Method> getStaticCacheMethodFromProperty(Object bean, String methodName, Object[] args, Class<?>[] type, boolean allowAccessPrivate) throws InvocationTargetException, IllegalAccessException {
        return PropertiesUtils.getClzMethod((Class<?>)bean ,methodName, allowAccessPrivate);
    }

    private static Object getStaticCacheMethodValueFromProperty(Object bean, String methodName, Object[] args, Class<?>[] type, boolean allowAccessPrivate) throws InvocationTargetException, IllegalAccessException {
        List<Method> methods = getStaticCacheMethodFromProperty(bean, methodName, args, type, allowAccessPrivate);
        Method method = MethodHandler.Preferred.findMostSpecificMethod(type, methods.toArray(new Method[0]));
        if(method == null){
            return null;
        }
        return MethodHandler.Access.accessMethodValue(method,bean,args,allowAccessPrivate);
    }

    private static List<Method> getCacheMethodFromProperty(Object bean, String methodName, Object[] args, Class<?>[] type, boolean allowAccessPrivate) throws InvocationTargetException, IllegalAccessException {
        return PropertiesUtils.getMethod(bean, methodName,allowAccessPrivate);
    }

    private static Object getCacheMethodValueFromProperty(Object bean, String methodName, Object[] args, Class<?>[] type, boolean allowAccessPrivate) throws InvocationTargetException, IllegalAccessException {
        List<Method> methods = getCacheMethodFromProperty(bean, methodName, args, type, allowAccessPrivate);
        Method method = MethodHandler.Preferred.findMostSpecificMethod(type, methods.toArray(new Method[0]));
        if(method == null){
            return null;
        }
        return MethodHandler.Access.accessMethodValue(method,bean,args,allowAccessPrivate);
    }

}
