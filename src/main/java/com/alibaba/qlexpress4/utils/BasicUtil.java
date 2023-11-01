package com.alibaba.qlexpress4.utils;


import com.alibaba.qlexpress4.runtime.Parameters;

import java.lang.reflect.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import static java.lang.Character.toUpperCase;

/**
 * Author: TaoKan
 */
public class BasicUtil {
    public static final String NULL_SIGN = "null";
    public static final String LENGTH = "length";
    public static final String CLASS = "class";

    public static final int LEVEL_FACTOR = 10000;
    public static final int DEFAULT_MATCH_INDEX = -1;
    public static final int DEFAULT_WEIGHT = Integer.MAX_VALUE;

    private static final Map<Class<?>,Class<?>> primitiveMap;

    static{
        primitiveMap = new HashMap<>(8);
        primitiveMap.put(Boolean.class,boolean.class);
        primitiveMap.put(Character.class,char.class);
        primitiveMap.put(Double.class,double.class);
        primitiveMap.put(Float.class,float.class);
        primitiveMap.put(Integer.class,int.class);
        primitiveMap.put(Long.class,long.class);
        primitiveMap.put(Byte.class,byte.class);
        primitiveMap.put(Short.class,short.class);
    }

    public static Class<?> transToPrimitive(Class<?> clazz){
        return primitiveMap.get(clazz);
    }

    public static Integer numberPromoteLevel(Class<?> numCls) {
        if (numCls == byte.class || numCls == Byte.class) {
            return 0;
        }
        if (numCls == short.class || numCls == Short.class) {
            return 1;
        }
        if (numCls == int.class || numCls == Integer.class) {
            return 2;
        }
        if (numCls == long.class || numCls == Long.class) {
            return 3;
        }
        if (numCls == BigInteger.class) {
            return 4;
        }
        if (numCls == float.class || numCls == Float.class) {
            return 5;
        }
        if (numCls == double.class || numCls == Double.class) {
            return 6;
        }
        if (numCls == BigDecimal.class) {
            return 7;
        }
        return null;
    }

    public static boolean isAccess(Member member) {
        return Modifier.isPublic(member.getDeclaringClass().getModifiers()) && Modifier.isPublic(member.getModifiers());
    }

    public static boolean isPublic(Member member) {
        return Modifier.isPublic(member.getModifiers());
    }

    public static boolean isStatic(Member member) {
        return Modifier.isStatic(member.getModifiers());
    }

    public static String getGetter(String s) {
        return "get" + toUpperCase(s.charAt(0)) + s.substring(1);
    }

    public static String getSetter(String s) {
        return "set" + toUpperCase(s.charAt(0)) + s.substring(1);
    }

    public static String getIsGetter(String s) {
        return "is" + toUpperCase(s.charAt(0)) + s.substring(1);
    }

    public static Class<?>[] getTypeOfObject(Object[] objects) {
        Class<?>[] classes = new Class<?>[objects.length];
        for (int i = 0; i < objects.length; i++) {
            if (objects[i] == null) {
                classes[i] = null;
            } else {
                classes[i] = objects[i].getClass();
            }
        }
        return classes;
    }

    public static Object[] argumentsArr(Parameters parameters) {
        Object[] arr = new Object[parameters.size()];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = parameters.getValue(i);
        }
        return arr;
    }
}
