package com.alibaba.qlexpress4.member;

import com.alibaba.qlexpress4.utils.BasicUtil;
import com.alibaba.qlexpress4.utils.QLAliasUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Member;

/**
 * @Author TaoKan
 * @Date 2022/4/7 下午6:05
 */
public class FieldHandler extends MemberHandler {
    public static class Access {
        public static void setAccessFieldValue(Member accessMember, Object bean, Object value, boolean allowAccessPrivate) throws IllegalAccessException {
            Field accessField = ((Field) accessMember);
            if (BasicUtil.isPublic(accessField)) {
                accessField.set(bean, value);
            } else {
                if(!allowAccessPrivate){
                    throw new IllegalAccessException("can not allow access");
                }else {
                    synchronized (accessField) {
                        try {
                            accessField.setAccessible(true);
                            accessField.set(bean, value);
                        } finally {
                            accessField.setAccessible(false);
                        }
                    }
                }
            }
        }


        public static Class<?> accessFieldType(Member accessMember) {
            Field accessField = ((Field) accessMember);
            return accessField.getType();
        }

        public static Object accessFieldValue(Member accessMember, Object bean, boolean allowAccessPrivate) throws IllegalAccessException {
            Field accessField = ((Field) accessMember);
            if (BasicUtil.isPublic(accessField)) {
                return accessField.get(bean);
            } else {
                if(!allowAccessPrivate){
                    throw new IllegalAccessException("can not allow access");
                }else {
                    synchronized (accessField) {
                        try {
                            accessField.setAccessible(true);
                            return accessField.get(bean);
                        } finally {
                            accessField.setAccessible(false);
                        }
                    }
                }
            }
        }
    }

    public static class Preferred {

        public static Field gatherFieldRecursive(Class<?> baseClass, String propertyName) {
            Field[] fields = baseClass.getDeclaredFields();
            for (Field field : fields) {
                //优先使用本身的定义
                if (propertyName.equals(field.getName())) {
                    return field;
                }
                if (QLAliasUtil.findQLAliasFields(field, propertyName)) {
                    return field;
                }
            }
            Class<?> superclass = baseClass.getSuperclass();
            if (superclass != null) {
                return gatherFieldRecursive(superclass, propertyName);
            }
            return null;

        }

    }
}
