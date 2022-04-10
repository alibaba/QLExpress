package com.alibaba.qlexpress4.utils;

import com.ql.util.express.exception.QLException;

import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Character.toUpperCase;

/**
 * @Author TaoKan
 * @Date 2022/4/7 下午3:12
 */
public class BasicUtils {
    public static final String NULL_SIGN = "null";
    public static final String LENGTH = "length";
    public static final String CLASS = "class";
    public static final String NEW = "new";

    public static final Class<?>[][] CLASS_MATCHES = new Class[][] {
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


    public static boolean isPreferredGetter(Method before, Method after, Map<String, Integer> map) {
        Class<?> beforeReturnType = before.getReturnType();
        Class<?> afterReturnType = after.getReturnType();
        if (beforeReturnType.equals(afterReturnType)) {
            return map.get(after.getName()) > map.get(before.getName());
        } else if (beforeReturnType.isAssignableFrom(afterReturnType)) {
            return true;
        } else {
            return false;
        }
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

}
