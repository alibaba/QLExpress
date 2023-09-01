package com.alibaba.qlexpress4.utils;

import com.alibaba.qlexpress4.member.MethodHandler;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author TaoKan
 * @Date 2022/5/28 下午5:23
 */
public class PropertiesUtil {

    private final Map<Class<?>, Boolean> FUNCTION_INTERFACE_CACHE = new ConcurrentHashMap<>();



    /**
     * getMethod List
     * return null means notFound
     *
     * @param bean
     * @param name
     * @return
     */
    public static List<Method> getMethod(Object bean, String name, boolean isAllowAccessPrivate) {
        return getMethod(bean, name, null, isAllowAccessPrivate);
    }

    public static List<Method> getMethod(Object bean, String name, Object[] params, boolean isAllowAccessPrivate) {
        if (params == null) {
            return MethodHandler.Preferred.gatherMethodsRecursive(bean.getClass(), name, isAllowAccessPrivate);
        }
        return MethodHandler.Preferred.gatherMethodsRecursive(bean.getClass(), name, isAllowAccessPrivate, params);
    }


    /**
     * getClzMethod
     * return null means notFound
     *
     * @param clazz
     * @param name
     * @return
     */
    public static List<Method> getClzMethod(Class<?> clazz, String name, boolean isAllowAccessPrivate) {
        return getClzMethod(clazz, name, null, isAllowAccessPrivate);
    }


    public static List<Method> getClzMethod(Class<?> clazz, String name, Object[] params, boolean isAllowAccessPrivate) {
        if (params == null) {
            return MethodHandler.Preferred.gatherMethodsRecursive(clazz, name, false, null, !isAllowAccessPrivate, true, null);
        }
        return MethodHandler.Preferred.gatherMethodsRecursive(clazz, name, true, params.length, !isAllowAccessPrivate, true, null);
    }
}
