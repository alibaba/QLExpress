package com.alibaba.qlexpress4.utils;

import com.alibaba.qlexpress4.annotation.QLField;
import java.lang.reflect.Method;

/**
 * Author: TaoKan
 */
public class QLFieldUtil {

    public static String[] getQLFieldValue(Method method) {
        return method.getAnnotation(QLField.class).value();
    }

    public static boolean containsQLFieldForMethod(Method method) {
        return method.isAnnotationPresent(QLField.class);
    }
}
