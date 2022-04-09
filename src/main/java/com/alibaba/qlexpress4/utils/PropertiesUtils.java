package com.alibaba.qlexpress4.utils;

import com.alibaba.qlexpress4.enums.AccessMode;
import com.alibaba.qlexpress4.exception.QLRuntimeException;
import com.alibaba.qlexpress4.member.FieldHandler;
import com.alibaba.qlexpress4.member.MemberHandler;
import com.alibaba.qlexpress4.member.MethodHandler;

import java.lang.reflect.*;
import java.util.*;

/**
 * @Author TaoKan
 * @Date 2022/4/7 下午3:12
 */
public class PropertiesUtils {


    /**
     * get instance field classType (accessField and getMethod)
     * @param bean
     * @param name
     * @return class.type
     */
    public static Class<?> getPropertyType(Object bean, String name){
        Member accessMember = MemberHandler.Access.getAccessMember(bean.getClass(),name,AccessMode.READ,true);
        if(Objects.nonNull(accessMember)){
            if (accessMember instanceof Method) {
                return MethodHandler.Access.accessMethodType(accessMember);
            }else if(accessMember instanceof Field){
                return FieldHandler.Access.accessFieldType(accessMember);
            }
        }
        return null;
    }

    /**
     * get instance field Value (accessField and getMethod)
     * @param bean
     * @param name
     * @return Object
     */
    public static Object getPropertyValue(Object bean, String name) throws InvocationTargetException, IllegalAccessException {
        Member accessMember = MemberHandler.Access.getAccessMember(bean.getClass(),name,AccessMode.READ,true);
        if(Objects.nonNull(accessMember)){
            if (accessMember instanceof Method) {
                return MethodHandler.Access.accessMethodValue(accessMember,bean,null);
            }else if(accessMember instanceof Field){
                return FieldHandler.Access.accessFieldValue(accessMember,bean);
            }
        }
        return null;
    }

    /**
     * set instance field (accessField and getMethod)
     * @param bean
     * @param name
     * @param value
     */
    public static void setPropertyValue(Object bean, String name, Object value) throws InvocationTargetException, IllegalAccessException {
        Member accessMember = MemberHandler.Access.getAccessMember(bean.getClass(),name,AccessMode.WRITE,true);
        if (accessMember instanceof Method) {
            MethodHandler.Access.setAccessMethodValue(accessMember,bean,value);
            return;
        }else if(accessMember instanceof Field){
            FieldHandler.Access.setAccessFieldValue(accessMember,bean,value);
            return;
        }
    }


    /**
     * Static.field
     * @param clazz
     * @param name
     * @return
     */
    public static Object getClzField(Class<?> clazz, String name) throws InvocationTargetException, IllegalAccessException {
        Member accessMember = MemberHandler.Access.getAccessMember(clazz,name,AccessMode.READ,false);
        if(Objects.nonNull(accessMember)){
            if (accessMember instanceof Method) {
                return MethodHandler.Access.accessMethodValue(accessMember,null,null);
            }else if(accessMember instanceof Field){
                return FieldHandler.Access.accessFieldValue(accessMember,null);
            }
        }
        return null;
    }
    /**
     * getMethod List
     * @param bean
     * @param name
     * @return
     */
    public static List<Method> getMethod(Object bean, String name){
        return MethodHandler.Preferred.gatherMethodsRecursive(bean.getClass(), name, null);
    }


    /**
     * getClzMethod
     * @param clazz
     * @param name
     * @return
     */
    public static List<Method> getClzMethod(Class<?> clazz, String name){
        return MethodHandler.Preferred.gatherMethodsRecursive(clazz, name, false, null,
        true, true,null);
    }
}
