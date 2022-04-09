package com.alibaba.qlexpress4.member;

import com.alibaba.qlexpress4.exception.QLRuntimeException;

import java.lang.reflect.Field;
import java.lang.reflect.Member;

/**
 * @Author TaoKan
 * @Date 2022/4/7 下午6:05
 */
public class FieldHandler extends MemberHandler{
    public static class Access{
        public static void setAccessFieldValue(Member accessMember, Object bean, String name, Object value){
            Field accessField = ((Field) accessMember);
            if (value != null && !accessField.getType().isAssignableFrom(value.getClass())) {
                throw new QLRuntimeException("cannot setPropertyValue: " + value.getClass() + " to " + accessField.getType()+ " about:"+name);
            }
            else {
                try {
                    if(accessField.isAccessible()){
                        accessField.set(bean,value);
                    }else {
                        synchronized (accessField) {
                            try {
                                accessField.setAccessible(true);
                                accessField.set(bean,value);
                            }finally {
                                accessField.setAccessible(false);
                            }
                        }
                    }
                } catch (IllegalAccessException e) {
                    throw new QLRuntimeException("setPropertyValue accessFieldException key："+
                            name + " (" + accessMember.toString() + ") with:" + e.getMessage());
                }
            }
        }


        public static Class<?> accessFieldType(Member accessMember){
            Field accessField = ((Field) accessMember);
            return accessField.getType();
        }

        public static Object accessFieldValue(Member accessMember,Object bean, String name){
            Field accessField = ((Field) accessMember);
            try {
                if(accessField.isAccessible()){
                    return accessField.get(bean);
                }else {
                    synchronized (accessField) {
                        try {
                            accessField.setAccessible(true);
                            return accessField.get(bean);
                        }finally {
                            accessField.setAccessible(false);
                        }
                    }
                }
            } catch (IllegalAccessException e) {
                throw new QLRuntimeException("getPropertyValue accessFieldException key："+
                        name + " (" + accessMember.toString() + ") with:" + e.getMessage());
            }
        }
    }
}
