package com.alibaba.qlexpress4.utils;


import com.alibaba.qlexpress4.runtime.data.checker.*;

import java.lang.reflect.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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
    public static final String SPLIT_CLASS = "#";
    public static final String SPLIT_NAME = ";";
    public static final String SPLIT_COLLECTOR = ",";

    public static final int LEVEL_FACTOR = 10000;
    public static final int DEFAULT_MATCH_INDEX = -1;
    public static final int DEFAULT_WEIGHT = Integer.MAX_VALUE;

    private static final Map<Class<?>,Class<?>> primitiveMap;
    private static final Map<Class<?>,Integer> classMatchImplicit;
    private static final Map<Class<?>,Integer> classMatchImplicitExtend;


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

    static {
        classMatchImplicit = new HashMap<>(6);
        classMatchImplicit.put(double.class,5);
        classMatchImplicit.put(float.class,4);
        classMatchImplicit.put(long.class,3);
        classMatchImplicit.put(int.class,2);
        classMatchImplicit.put(short.class,1);
        classMatchImplicit.put(byte.class,0);
    }

    static {
        classMatchImplicitExtend = new HashMap<>(14);
        classMatchImplicitExtend.put(BigInteger.class,13);
        classMatchImplicitExtend.put(BigDecimal.class,12);
        classMatchImplicitExtend.put(Double.class,11);
        classMatchImplicitExtend.put(Float.class,10);
        classMatchImplicitExtend.put(Long.class,9);
        classMatchImplicitExtend.put(Integer.class,8);
        classMatchImplicitExtend.put(Short.class,7);
        classMatchImplicitExtend.put(Byte.class,6);
        classMatchImplicitExtend.put(double.class,5);
        classMatchImplicitExtend.put(float.class,4);
        classMatchImplicitExtend.put(long.class,3);
        classMatchImplicitExtend.put(int.class,2);
        classMatchImplicitExtend.put(short.class,1);
        classMatchImplicitExtend.put(byte.class,0);
    }



    public static Class<?> transToPrimitive(Class<?> clazz){
        return primitiveMap.get(clazz);
    }


    public static boolean classMatchImplicit(Class<?> target, Class<?> source){
        Integer targetOp = classMatchImplicit.get(target);
        Integer sourceOp = classMatchImplicit.get(source);
        if(targetOp != null && sourceOp != null && targetOp > sourceOp){
            return true;
        }else {
            return false;
        }
    }

    public static boolean classMatchImplicitExtend(Class<?> target, Class<?> source){
        Integer targetOp = classMatchImplicitExtend.get(target);
        Integer sourceOp = classMatchImplicitExtend.get(source);
        if(targetOp != null && sourceOp != null){
            return true;
        }else {
            return false;
        }
    }

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

}
