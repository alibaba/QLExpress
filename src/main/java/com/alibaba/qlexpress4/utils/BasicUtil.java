package com.alibaba.qlexpress4.utils;

import com.alibaba.qlexpress4.proxy.QLambdaInvocationHandler;
import com.alibaba.qlexpress4.runtime.QLambda;

import java.lang.reflect.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;

import static java.lang.Character.toUpperCase;

/**
 * @Author TaoKan
 * @Date 2022/5/28 下午5:18
 */
public class BasicUtil {
    public static final String NULL_SIGN = "null";
    public static final String LENGTH = "length";
    public static final String CLASS = "class";
    public static final String NEW = "new";
    public static final int LEVEL_FACTOR = 10;
    public static final int DEFAULT_MATCH_INDEX = -1;
    public static final int DEFAULT_WEIGHT = Integer.MAX_VALUE;

    public static final Class<?>[][] CLASS_MATCHES_IMPLICIT = new Class[][]{
            {double.class, float.class}, {double.class, long.class}, {double.class, int.class},
            {double.class, short.class},{double.class, byte.class},  {float.class, long.class},
            {float.class, int.class},{float.class, short.class}, {float.class, byte.class},
            {long.class, int.class}, {long.class, short.class}, {long.class, byte.class},
            {int.class, short.class}, {int.class, byte.class}, {short.class, byte.class}
    };

    public static final Class<?>[][] CLASS_MATCHES_EXTEND = new Class[][]{
            {BigDecimal.class, double.class}, {BigDecimal.class, float.class}, {BigDecimal.class, long.class},
            {BigDecimal.class, int.class}, {BigDecimal.class, short.class}, {BigDecimal.class, byte.class},
            {double.class, BigDecimal.class}, {float.class, BigDecimal.class}, {byte.class, BigDecimal.class},
            {short.class, BigDecimal.class}, {int.class, BigDecimal.class}, {long.class, BigDecimal.class},
            {BigInteger.class, double.class}, {BigInteger.class, float.class}, {BigInteger.class, long.class},
            {BigInteger.class, int.class}, {BigInteger.class, short.class}, {BigInteger.class, byte.class},
            {double.class, BigInteger.class}, {float.class, BigInteger.class},{byte.class, BigInteger.class},
            {short.class, BigInteger.class}, {int.class, BigInteger.class}, {long.class, BigInteger.class}
    };


    public static boolean isPublic(Member member) {
        return Modifier.isPublic(member.getModifiers());
    }

    public static boolean isStatic(Member member) {
        return Modifier.isStatic(member.getModifiers());
    }

    public static String getGetter(String s) {
        return new StringBuilder().append("get").append(toUpperCase(s.charAt(0))).append(s, 1, s.length()).toString();
    }

    public static String getSetter(String s) {
        return new StringBuilder().append("set").append(toUpperCase(s.charAt(0))).append(s, 1, s.length()).toString();
    }

    public static String getIsGetter(String s) {
        return new StringBuilder().append("is").append(toUpperCase(s.charAt(0))).append(s, 1, s.length()).toString();
    }

    public static Class<?>[] getTypeOfObject(Object[] objects) {
        if (objects == null) {
            return null;
        }
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



    public static int hashAlgorithmWithNoForNumber(int h){
        h += (h << 15) ^ 0xffffcd7d;
        h ^= (h >>> 10);
        h += (h << 3);
        h ^= (h >>> 6);
        h += (h << 2) + (h << 14);
        return h ^ (h >>> 16);
    }


    public static Class<?> transToPrimitive(Class<?> clazz){
        if (!clazz.isPrimitive()) {
            if(clazz == Boolean.class){
                clazz = boolean.class;
            } else if (clazz == Byte.class) {
                clazz = byte.class;
            } else if (clazz == Short.class) {
                clazz = short.class;
            } else if (clazz == Integer.class) {
                clazz = int.class;
            } else if (clazz == Long.class) {
                clazz = long.class;
            } else if (clazz == Float.class) {
                clazz = float.class;
            } else if (clazz == Double.class) {
                clazz = double.class;
            } else if (clazz == Character.class) {
                clazz = char.class;
            }
        }
        return clazz;
    }
}
