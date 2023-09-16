package com.alibaba.qlexpress4.utils;

import com.alibaba.qlexpress4.annotation.QLFunction;

import java.lang.reflect.Method;

/**
 * Author: TaoKan
 */
public class QLFunctionUtil {

    public static String[] getQLFunctionValue(Method method) {
        return method.getAnnotation(QLFunction.class).value();
    }

    public static boolean containsQLFunctionForMethod(Method method) {
        return method.isAnnotationPresent(QLFunction.class);
    }
}
