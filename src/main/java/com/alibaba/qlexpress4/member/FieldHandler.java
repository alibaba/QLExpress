package com.alibaba.qlexpress4.member;

import com.alibaba.qlexpress4.annotation.QLAlias;
import com.alibaba.qlexpress4.utils.QLAliasUtils;

import java.lang.reflect.Field;

/**
 * Author: TaoKan
 */
public class FieldHandler {
    public static class Preferred {

        public static String preHandleAlias(Class<?> baseClass, String propertyName) {
            Field[] fields = baseClass.getDeclaredFields();
            for (Field field : fields) {
                if (QLAliasUtils.matchQLAlias(propertyName, field.getAnnotationsByType(QLAlias.class))) {
                    return field.getName();
                }
            }
            Class<?> superclass = baseClass.getSuperclass();
            if (superclass != null) {
                return preHandleAlias(superclass, propertyName);
            }
            return propertyName;
        }

        public static Field gatherFieldRecursive(Class<?> baseClass, String propertyName) {
            Field[] fields = baseClass.getDeclaredFields();
            for (Field field : fields) {
                if (propertyName.equals(field.getName())) {
                    return field;
                }
                if (QLAliasUtils.matchQLAlias(propertyName, field.getAnnotationsByType(QLAlias.class))) {
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
