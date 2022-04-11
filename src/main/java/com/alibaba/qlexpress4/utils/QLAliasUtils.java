package com.alibaba.qlexpress4.utils;

import com.alibaba.qlexpress4.annotation.QLAlias;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @Author TaoKan
 * @Date 2022/4/7 下午3:12
 */
public class QLAliasUtils {
    private QLAliasUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static String[] getQLAliasValue(Method method){
        return method.getAnnotation(QLAlias.class).value();
    }

    public static boolean containsQLAliasForMethod(Method method){
        return method.isAnnotationPresent(QLAlias.class);
    }

    public static boolean findQLAliasFields(Field field ,String propertyName){
        for (QLAlias alias : field.getAnnotationsByType(QLAlias.class)) {
            for (int i = 0; i < alias.value().length; i++) {
                if (propertyName.equals(alias.value()[i])) {
                    return true;
                }
            }
        }
        return false;
    }

}