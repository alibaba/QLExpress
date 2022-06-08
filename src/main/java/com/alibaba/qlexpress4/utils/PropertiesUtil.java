package com.alibaba.qlexpress4.utils;

import com.alibaba.qlexpress4.enums.AccessMode;
import com.alibaba.qlexpress4.member.FieldHandler;
import com.alibaba.qlexpress4.member.MemberHandler;
import com.alibaba.qlexpress4.member.MethodHandler;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * @Author TaoKan
 * @Date 2022/5/28 下午5:23
 */
public class PropertiesUtil {


    /**
     * get instance field classType (accessField and getMethod)
     * return null means notFound
     *
     * @param bean
     * @param name
     * @return class.type
     */
    public static Class<?> getPropertyType(Object bean, String name) throws NoSuchFieldException {
        if (bean.getClass().isArray() && BasicUtil.LENGTH.equals(name)) {
            return int.class;
        } else if (bean instanceof Class) {
            if (BasicUtil.CLASS.equals(name)) {
                return Class.class;
            } else {
                return ((Class<?>) bean).getField(name).getType();
            }
        } else if (bean instanceof Map) {
            Object o = ((Map<?, ?>) bean).get(name);
            if (o == null) {
                return null;
            } else {
                return o.getClass();
            }
        } else {
            Member accessMember = MemberHandler.Access.getAccessMember(bean.getClass(), name, AccessMode.READ);
            if (accessMember != null) {
                if (accessMember instanceof Method) {
                    return MethodHandler.Access.accessMethodType(accessMember);
                } else if (accessMember instanceof Field) {
                    return FieldHandler.Access.accessFieldType(accessMember);
                }
            }
        }
        return null;
    }

    /**
     * get instance field Value (accessField and getMethod)
     * return null means notFound
     *
     * @param bean
     * @param name
     * @return Object
     */
    public static Object getPropertyValue(Object bean, String name, boolean allowAccessPrivate) throws InvocationTargetException, IllegalAccessException {
        Member accessMember = MemberHandler.Access.getAccessMember(bean.getClass(), name, AccessMode.READ);
        if (accessMember != null) {
            if (accessMember instanceof Method) {
                return MethodHandler.Access.accessMethodValue(accessMember, bean, null, allowAccessPrivate);
            } else if (accessMember instanceof Field) {
                return FieldHandler.Access.accessFieldValue(accessMember, bean, allowAccessPrivate);
            }
        }
        return null;
    }

    /**
     * set instance field (accessField and getMethod)
     *
     * @param bean
     * @param name
     * @param value
     */
    public static void setPropertyValue(Object bean, String name, Object value, boolean allowAccessPrivate) throws InvocationTargetException, IllegalAccessException {
        Member accessMember = MemberHandler.Access.getAccessMember(bean.getClass(), name, AccessMode.WRITE);
        if (accessMember instanceof Method) {
            MethodHandler.Access.setAccessMethodValue(accessMember, bean, value, allowAccessPrivate);
            return;
        } else if (accessMember instanceof Field) {
            FieldHandler.Access.setAccessFieldValue(accessMember, bean, value, allowAccessPrivate);
            return;
        }
    }

    /**
     * set static field (accessField and getMethod)
     *
     * @param bean
     * @param name
     * @param value
     */
    public static void setClzPropertyValue(Object bean, String name, Object value, boolean allowAccessPrivate) throws IllegalAccessException {
        Member accessMember = MemberHandler.Access.getAccessMember((Class<?>) bean, name, AccessMode.WRITE);
        FieldHandler.Access.setAccessFieldValue(accessMember, null, value, allowAccessPrivate);
    }


    /**
     * Static.field
     * return null means notFound
     *
     * @param clazz
     * @param name
     * @return
     */
    public static Object getClzField(Class<?> clazz, String name, boolean allowAccessPrivate) throws InvocationTargetException, IllegalAccessException {
        Member accessMember = MemberHandler.Access.getAccessMember(clazz, name, AccessMode.READ);
        if (accessMember != null) {
            if (accessMember instanceof Method) {
                return MethodHandler.Access.accessMethodValue(accessMember, null, null, allowAccessPrivate);
            } else if (accessMember instanceof Field) {
                return FieldHandler.Access.accessFieldValue(accessMember, null, allowAccessPrivate);
            }
        }
        return null;
    }

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
    public static List<Method> getClzMethod(Class<?> clazz, String name) {
        return getClzMethod(clazz, name, null, false);
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
