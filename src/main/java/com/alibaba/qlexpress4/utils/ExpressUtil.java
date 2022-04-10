package com.alibaba.qlexpress4.utils;

import com.alibaba.qlexpress4.cache.*;

import java.lang.reflect.*;
import java.util.Map;

/**
 * @Author TaoKan
 * @Date 2022/4/7 下午3:12
 */
@SuppressWarnings("unchecked")
public class ExpressUtil {

    public static final FieldCacheElement fieldCacheElement = new FieldCacheElement();
    public static final ConstructorCacheElement constructorCacheElement = new ConstructorCacheElement();
    public static final MethodCacheElement methodCacheElement = new MethodCacheElement();
    public static final FunctionCacheElement functionCacheElement = new FunctionCacheElement();


    public static Field getFieldCacheElement(Class<?> baseClass, String propertyName){
        String key = (String)fieldCacheElement.buildCacheKey(baseClass ,propertyName, null);
        return (Field) fieldCacheElement.getCacheElement(key, baseClass, propertyName, null, false, false);
    }


    public static Method getMethodWithCache(Class<?> baseClass, String methodName,
                                             Class<?>[] types, boolean publicOnly, boolean isStatic) {
        String key = (String)methodCacheElement.buildCacheKey(baseClass ,methodName, types);
        return (Method) methodCacheElement.getCacheElement(key, baseClass, methodName, types, publicOnly, isStatic);
    }


    public static Constructor<?> getConstructorWithCache(Class<?> baseClass, Class<?>[] types) {
        String key = (String)constructorCacheElement.buildCacheKey(baseClass ,BasicUtils.NEW, types);
        return (Constructor<?>) constructorCacheElement.getCacheElement(key, baseClass, null, null, false, false);
    }


    public static boolean isFunctionInterface(Class<?> clazz){
        if (clazz == null) {
            return false;
        }
        return functionCacheElement.cacheFunctionInterface(clazz);
    }

}
