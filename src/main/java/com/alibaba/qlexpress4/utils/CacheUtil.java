package com.alibaba.qlexpress4.utils;

import com.alibaba.qlexpress4.member.MethodHandler;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Author: TaoKan
 */
public class CacheUtil {
    private static final Map<Object, Boolean> FUNCTION_INTERFACE_CACHE = new ConcurrentHashMap<>();

    public static boolean isFunctionInterface(Class<?> clazz) {
        return FUNCTION_INTERFACE_CACHE.computeIfAbsent(clazz,
                ignore -> clazz.isInterface() && MethodHandler.hasOnlyOneAbstractMethod(clazz.getMethods()));
    }
}
