package com.alibaba.qlexpress4.utils;

import com.alibaba.qlexpress4.proxy.QLambdaInvocationHandler;
import com.alibaba.qlexpress4.runtime.QLambda;

import java.lang.reflect.*;
import java.math.BigDecimal;
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

    public static final Class<?>[][] CLASS_MATCHES = new Class[][]{
            //原始数据类型
            {BigDecimal.class, double.class}, {BigDecimal.class, float.class}, {BigDecimal.class, long.class},
            {BigDecimal.class, int.class}, {BigDecimal.class, short.class}, {BigDecimal.class, byte.class},
            {double.class, float.class}, {double.class, long.class}, {double.class, int.class}, {double.class, short.class},
            {double.class, byte.class}, {double.class, BigDecimal.class},
            {float.class, long.class}, {float.class, int.class}, {float.class, short.class}, {float.class, byte.class},
            {float.class, BigDecimal.class},
            {long.class, int.class}, {long.class, short.class}, {long.class, byte.class},
            {int.class, short.class}, {int.class, byte.class},
            {short.class, byte.class},
            //---------
            {char.class, Character.class}, {Character.class, char.class},
            {boolean.class, Boolean.class}, {Boolean.class, boolean.class}
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


    public static Number transfer(Number value, Class<?> type, boolean isForce) {
        if (isForce || !(value instanceof BigDecimal)) {
            if (type.equals(byte.class) || type.equals(Byte.class)) {
                return value.byteValue();
            } else if (type.equals(short.class) || type.equals(Short.class)) {
                return value.shortValue();
            } else if (type.equals(int.class) || type.equals(Integer.class)) {
                return value.intValue();
            } else if (type.equals(long.class) || type.equals(Long.class)) {
                return value.longValue();
            } else if (type.equals(float.class) || type.equals(Float.class)) {
                return value.floatValue();
            } else if (type.equals(double.class) || type.equals(Double.class)) {
                return value.doubleValue();
            } else if (type.equals(BigDecimal.class)) {
                return new BigDecimal(value.toString());
            } else {
                throw new RuntimeException("没有处理的数据类型：" + type.getName());
            }
        } else {
            if (type.equals(byte.class) || type.equals(Byte.class)) {
                if (((BigDecimal) value).scale() > 0) {
                    throw new RuntimeException("有小数位，不能转化为：" + type.getName());
                }
                return value.byteValue();
            } else if (type.equals(short.class) || type.equals(Short.class)) {
                if (((BigDecimal) value).scale() > 0) {
                    throw new RuntimeException("有小数位，不能转化为：" + type.getName());
                }
                return value.shortValue();
            } else if (type.equals(int.class) || type.equals(Integer.class)) {
                if (((BigDecimal) value).scale() > 0) {
                    throw new RuntimeException("有小数位，不能转化为：" + type.getName());
                }
                return value.intValue();
            } else if (type.equals(long.class) || type.equals(Long.class)) {
                if (((BigDecimal) value).scale() > 0) {
                    throw new RuntimeException("有小数位，不能转化为：" + type.getName());
                }
                return value.longValue();
            } else if (type.equals(float.class) || type.equals(Float.class)) {
                return value.floatValue();
            } else if (type.equals(double.class) || type.equals(Double.class)) {
                return value.doubleValue();
            } else {
                throw new RuntimeException("没有处理的数据类型：" + type.getName());
            }
        }
    }


    public static Object castObject(Object value, Class<?> type, boolean isForce) {
        if (value.getClass() == type || type.isAssignableFrom(value.getClass())) {
            return value;
        }
        if (value instanceof Number && (type.isPrimitive() || Number.class.isAssignableFrom(type))) {
            return BasicUtil.transfer((Number) value, type, isForce);
        } else if (type.isArray() && value.getClass().isArray()) {
            Class<?> valueType = value.getClass().getComponentType();
            Class<?> declareType = type.getComponentType();
            if (declareType != valueType) {
                Object[] values = (Object[]) value;
                boolean allBlank = true;
                for (Object o : values) {
                    if (o != null) {
                        allBlank = false;
                        break;
                    }
                }
                if (allBlank) {
                    return Array.newInstance(declareType, values.length);
                }
            }
            return value;

        } else if (QLambda.class.isAssignableFrom(value.getClass()) && CacheUtil.isFunctionInterface(type)) {
            return Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[]{type}, new QLambdaInvocationHandler((QLambda) value));
        } else {
            return value;
        }
    }

    public static int hashAlgorithmWithNoForNumber(int h){
        h += (h << 15) ^ 0xffffcd7d;
        h ^= (h >>> 10);
        h += (h << 3);
        h ^= (h >>> 6);
        h += (h << 2) + (h << 14);
        return h ^ (h >>> 16);
    }
}
