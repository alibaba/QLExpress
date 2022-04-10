package com.alibaba.qlexpress4.utils;


import com.alibaba.qlexpress4.cache.*;
import com.ql.util.express.config.QLExpressRunStrategy;

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


    public static Object getProperty(Object bean, Object name) throws InvocationTargetException, IllegalAccessException, NoSuchFieldException {
        if (bean == null && QLExpressRunStrategy.isAvoidNullPointer()) {
            return null;
        }
        if (bean.getClass().isArray() && BasicUtils.LENGTH.equals(name)) {
            return Array.getLength(bean);
        } else if (bean instanceof Class) {
            if (BasicUtils.CLASS.equals(name)) {
                return bean;
            } else {
                Field f = ((Class<?>)bean).getDeclaredField(name.toString());
                return f.get(null);
            }
        } else if (bean instanceof Map) {
            return ((Map<?, ?>)bean).get(name);
        } else {
            return PropertiesUtils.getPropertyValue(bean, name.toString());
        }
    }

    public static Class<?> getPropertyClass(Object bean, Object name) throws NoSuchFieldException {
            if (bean.getClass().isArray() && BasicUtils.LENGTH.equals(name)) {
                return int.class;
            } else if (bean instanceof Class) {
                if (BasicUtils.CLASS.equals(name)) {
                    return Class.class;
                } else {
                    Field f = ((Class<?>)bean).getDeclaredField(name.toString());
                    return f.getType();
                }
            } else if (bean instanceof Map) {
                Object o = ((Map<?, ?>)bean).get(name);
                if (o == null) {
                    return null;
                } else {
                    return o.getClass();
                }
            } else {
                return PropertiesUtils.getPropertyType(bean, name.toString());
            }
    }

    public static void setProperty(Object bean, Object name, Object value) throws NoSuchFieldException, InvocationTargetException, IllegalAccessException {
        if (bean instanceof Class) {
            Field field = ((Class<?>)bean).getDeclaredField(name.toString());
            field.set(null, value);
        } else if (bean instanceof Map) {
            ((Map<Object, Object>)bean).put(name, value);
        } else {
            PropertiesUtils.setPropertyValue(bean, name.toString(), value);
        }
    }

}
