package com.alibaba.qlexpress4.member;

import com.alibaba.qlexpress4.utils.QLAliasUtil;

import java.lang.reflect.Field;

/**
 * Author: TaoKan
 */
public class FieldHandler {
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
