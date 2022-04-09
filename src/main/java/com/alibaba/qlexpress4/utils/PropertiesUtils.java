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
     * object.name || object.getName()
     * @param bean
     * @param name
     * @return class.type
     */
    public static Class<?> getPropertyType(Object bean, String name){
        Member accessMember = MemberHandler.Access.getAccessMember(bean.getClass(),name,AccessMode.READ);
        if(Objects.isNull(accessMember)){
            throw new QLRuntimeException("No Access Member From getPropertyType:"+ bean.getClass().getCanonicalName()+ " key:"+name);
        }
        if (accessMember instanceof Method) {
            return MethodHandler.Access.accessMethodType(accessMember);
        }else if(accessMember instanceof Field){
            return FieldHandler.Access.accessFieldType(accessMember);
        }
        throw new QLRuntimeException("IllegalException From getPropertyType:"+ bean.getClass().getCanonicalName()+ " key:"+name);
    }

    /**
     * object.name || object.getName()
     * @param bean
     * @param name
     * @return Object
     */
    public static Object getPropertyValue(Object bean, String name) {
        Member accessMember = MemberHandler.Access.getAccessMember(bean.getClass(),name,AccessMode.READ);
        if(Objects.isNull(accessMember)){
            throw new QLRuntimeException("No Access Member From getPropertyValue:"+ bean.getClass().getCanonicalName()+ " key:"+name);
        }
        if (accessMember instanceof Method) {
            return MethodHandler.Access.accessMethodValue(accessMember,bean,name,null);
        }else if(accessMember instanceof Field){
            return FieldHandler.Access.accessFieldValue(accessMember,bean,name);
        }
        throw new QLRuntimeException("IllegalException From getPropertyValue:"+ bean.getClass().getCanonicalName()+ " key:"+name);
    }

    /**
     * object.name = "ex"
     * @param bean
     * @param name
     * @param value
     */
    public static void setPropertyValue(Object bean, String name, Object value){
        Member accessMember = MemberHandler.Access.getAccessMember(bean.getClass(),name,AccessMode.WRITE);
        if(Objects.isNull(accessMember)){
            throw new QLRuntimeException("No Access Member From setPropertyValue:"+ bean.getClass().getCanonicalName()+ " key:"+name);
        }
        if (accessMember instanceof Method) {
            MethodHandler.Access.setAccessMethodValue(accessMember,bean,name,value);
            return;
        }else if(accessMember instanceof Field){
            FieldHandler.Access.setAccessFieldValue(accessMember,bean,name,value);
            return;
        }
        throw new QLRuntimeException("IllegalException From setPropertyValue:"+ bean.getClass().getCanonicalName()+ " key:"+name);
    }


    /**
     *
     * @param clazz
     * @param name
     * @return
     */
    public static Field getClzField(Class<?> clazz, String name){
        Field f = null;
        try {
            f = clazz.getField(name);
            return f;
        } catch (NoSuchFieldException e) {
            throw new QLRuntimeException("IllegalException From getClzProperty:"+ clazz.getCanonicalName()+ " key:"+name);
        }
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
        return MethodHandler.Preferred.gatherMethodsRecursive(clazz, name, null);
    }
}
