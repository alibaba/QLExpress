package com.alibaba.qlexpress4.utils;

import com.alibaba.qlexpress4.cache.ConstructorCacheElement;
import com.alibaba.qlexpress4.cache.FieldCacheElement;
import com.alibaba.qlexpress4.cache.FunctionCacheElement;
import com.alibaba.qlexpress4.cache.MethodCacheElement;
import com.alibaba.qlexpress4.enums.AccessMode;
import com.alibaba.qlexpress4.member.ConstructorHandler;
import com.alibaba.qlexpress4.member.FieldHandler;
import com.alibaba.qlexpress4.member.MemberHandler;
import com.alibaba.qlexpress4.member.MethodHandler;

import java.lang.reflect.*;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @Author TaoKan
 * @Date 2022/4/17 下午12:28
 */
public class CacheUtils {

    public static final FieldCacheElement fieldCacheElement = new FieldCacheElement();
    public static final ConstructorCacheElement constructorCacheElement = new ConstructorCacheElement();
    public static final MethodCacheElement methodCacheElement = new MethodCacheElement();
    public static final FunctionCacheElement functionCacheElement = new FunctionCacheElement();


    public static void initCache(int size,boolean isUseCacheClear){
        fieldCacheElement.initCache(size, isUseCacheClear);
        constructorCacheElement.initCache(size, isUseCacheClear);
        functionCacheElement.initCache(size, isUseCacheClear);
        methodCacheElement.initCache(size, isUseCacheClear);
    }


    public static Object getFieldCacheElement(Object bean, String name, boolean allowAccessPrivate) throws InvocationTargetException, IllegalAccessException {
        if (bean.getClass().isArray() && BasicUtils.LENGTH.equals(name)) {
            return Array.getLength(bean);
        } else if (bean instanceof Class) {
            if (BasicUtils.CLASS.equals(name)) {
                return bean;
            } else {
                String key = (String)fieldCacheElement.buildCacheKey((Class<?>)bean ,name, null);
                Object cacheElement = fieldCacheElement.getCacheElement(key);
                if(cacheElement == null){
                    cacheElement = PropertiesUtils.getClzField((Class<?>)bean,name,allowAccessPrivate);
                }
                return cacheElement;
            }
        } else if (bean instanceof Map) {
            return ((Map<?, ?>)bean).get(name);
        } else {
            String key = (String)fieldCacheElement.buildCacheKey(bean.getClass() ,name, null);
            Object cacheElement = fieldCacheElement.getCacheElement(key);
            if(cacheElement == null){
                cacheElement = PropertiesUtils.getPropertyValue(bean.getClass(), name, allowAccessPrivate);
            }
            return cacheElement;
        }
    }


    public static void setFieldCacheElement(Object bean, String name, Object value, boolean allowAccessPrivate) throws IllegalAccessException, InvocationTargetException {
        if (bean instanceof Class) {
            PropertiesUtils.setClzPropertyValue(bean, name, value, allowAccessPrivate);
            String key = (String)fieldCacheElement.buildCacheKey((Class<?>)bean ,name, null);
            fieldCacheElement.setCacheElement(key,value);
        } else if (bean instanceof Map) {
            ((Map<Object, Object>)bean).put(name, value);
        } else {
            PropertiesUtils.setPropertyValue(bean, name, value, allowAccessPrivate);
            String key = (String)fieldCacheElement.buildCacheKey(bean.getClass() ,name, null);
            fieldCacheElement.setCacheElement(key,value);
        }
    }


    public static List<Method> getMethodWithCache(Object bean, String methodName, Object[] args, boolean isAllowAccessPrivate) throws InvocationTargetException, IllegalAccessException {
        Object cacheElement = null;
        Class<?>[] type = BasicUtils.getTypeOfObject(args);
        if (bean instanceof Class) {
            String key = (String)methodCacheElement.buildCacheKey((Class<?>)bean, methodName, type);
            cacheElement = methodCacheElement.getCacheElement(key);
            if(cacheElement == null){
                cacheElement = getStaticCacheMethodFromProperty(bean,methodName,args,type, isAllowAccessPrivate);
                if(cacheElement!=null){
                    methodCacheElement.setCacheElement(key, cacheElement);
                }
            }
        }else {
            String key = (String)methodCacheElement.buildCacheKey(bean.getClass(), methodName, type);
            cacheElement = methodCacheElement.getCacheElement(key);
            if(cacheElement == null){
                cacheElement = getCacheMethodFromProperty(bean,methodName,args,type, isAllowAccessPrivate);
                if(cacheElement!=null){
                    methodCacheElement.setCacheElement(key, cacheElement);
                }
            }
        }
        return (List<Method>)cacheElement;
    }

    public static Object getMethodCacheElement(Object bean, String methodName, Object[] args, boolean allowAccessPrivate) throws InvocationTargetException, IllegalAccessException {
        Object cacheElement = null;
        Class<?>[] type = BasicUtils.getTypeOfObject(args);
        if (bean instanceof Class) {
            String key = (String)methodCacheElement.buildCacheKey((Class<?>)bean ,methodName, type);
            cacheElement = methodCacheElement.getCacheElement(key);
            if(cacheElement == null){
                cacheElement = getStaticCacheMethodValueFromProperty(bean,methodName,args,type,allowAccessPrivate);
                if(cacheElement!=null){
                    methodCacheElement.setCacheElement(key, cacheElement);
                }
            }
        }else {
            String key = (String)methodCacheElement.buildCacheKey(bean.getClass() ,methodName, type);
            cacheElement = methodCacheElement.getCacheElement(key);
            if(cacheElement == null){
                cacheElement = getCacheMethodValueFromProperty(bean,methodName,args,type,allowAccessPrivate);
                if(cacheElement!=null){
                    methodCacheElement.setCacheElement(key, cacheElement);
                }
            }
        }
        return cacheElement;
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
