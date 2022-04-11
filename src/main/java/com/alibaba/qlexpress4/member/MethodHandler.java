package com.alibaba.qlexpress4.member;

import com.alibaba.qlexpress4.utils.BasicUtils;
import com.alibaba.qlexpress4.utils.QLAliasUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * @Author TaoKan
 * @Date 2022/4/7 下午6:05
 */
public class MethodHandler extends MemberHandler{

    public static class Preferred{
        /**
         *
         * @param idealMatch
         * @param methods
         * @return
         */
        public static Method findMostSpecificMethod(Class<?>[] idealMatch, Method[] methods) {
            Class<?>[][] candidates = new Class[methods.length][];
            for (int i = 0; i < methods.length; i++) {
                candidates[i] = methods[i].getParameterTypes();
            }
            int match = MemberHandler.Preferred.findMostSpecificSignature(idealMatch, candidates);
            return match == -1 ? null : methods[match];
        }



        public static List<Method> gatherMethodsRecursive(Class<?> baseClass, String methodName, List<Method> candidates) {
            return gatherMethodsRecursive(baseClass,methodName,false,null,false,false,candidates);
        }


        public static List<Method> gatherMethodsRecursive(Class<?> baseClass, String methodName, boolean argCheck, Integer numArgs,
                              boolean publicOnly, boolean isStatic, List<Method> candidates) {
            if (candidates == null) {
                candidates = new ArrayList();
            }
            addCandidatesMethod(baseClass.getDeclaredMethods(), methodName, argCheck, numArgs, publicOnly, isStatic, candidates);
            Class<?>[] interfaces = baseClass.getInterfaces();
            for (Class<?> anInterface : interfaces) {
                gatherMethodsRecursive(anInterface, methodName, argCheck, numArgs, publicOnly, isStatic, candidates);
            }
            Class<?> superclass = baseClass.getSuperclass();
            if (superclass != null) {
                gatherMethodsRecursive(superclass, methodName, argCheck, numArgs, publicOnly, isStatic, candidates);
            }
            return candidates;
        }


        private static boolean candidateCheck(Method method, boolean argCheck, Integer numArgs, boolean publicOnly, boolean isStatic){
            if(argCheck && method.getParameterTypes().length == numArgs){
                return (!publicOnly || BasicUtils.isPublic(method) && (!isStatic || BasicUtils.isStatic(method)));
            }else {
                return (!publicOnly || BasicUtils.isPublic(method) && (!isStatic || BasicUtils.isStatic(method)));
            }
        }


        public static List<Method> addCandidatesMethod(Method[] methods, String methodName, boolean argCheck,
                              Integer numArgs, boolean publicOnly, boolean isStatic, List<Method> candidates) {
            for (Method method : methods) {
                if(method.getName().equals(methodName) && candidateCheck(method,argCheck,numArgs,publicOnly,isStatic)){
                    candidates.add(method);
                }else if (QLAliasUtils.containsQLAliasForMethod(method)) {
                    for (String value : QLAliasUtils.getQLAliasValue(method)) {
                        if(value.equals(methodName) && candidateCheck(method,argCheck,numArgs,publicOnly,isStatic)){
                            candidates.add(method);
                        }
                    }
                }
            }
            return candidates;
        }
    }

    public static class Access{
        public static Class<?> accessMethodType(Member accessMember){
            Method accessMethod = ((Method) accessMember);
            return accessMethod.getReturnType();
        }
        public static Object accessMethodValue(Member accessMember,Object bean, Object[] params, boolean allowAccessPrivateMethod) throws
                IllegalArgumentException,InvocationTargetException,IllegalAccessException{
            Method accessMethod = ((Method) accessMember);
                if(allowAccessPrivateMethod || accessMethod.isAccessible()){
                    return accessMethod.invoke(bean,params);
                }else {
                    synchronized (accessMethod) {
                        try {
                            accessMethod.setAccessible(true);
                            return accessMethod.invoke(bean,params);
                        }finally {
                            accessMethod.setAccessible(false);
                        }
                    }
                }
        }

        public static void setAccessMethodValue(Member accessMember, Object bean, Object value, boolean allowAccessPrivateMethod)
                throws IllegalArgumentException,InvocationTargetException,IllegalAccessException{
            Method accessMethod = ((Method) accessMember);
            if(allowAccessPrivateMethod || accessMethod.isAccessible()){
                accessMethod.invoke(bean,value);
            }else {
                synchronized (accessMethod) {
                    try {
                        accessMethod.setAccessible(true);
                        accessMethod.invoke(bean,value);
                    }finally {
                        accessMethod.setAccessible(false);
                    }
                }
            }

        }
    }

    public static Method getGetter(Class<?> clazz, String property, boolean isStaticCheck) {
        String isGet = BasicUtils.getIsGetter(property);
        String getter = BasicUtils.getGetter(property);

        Map<String, Integer> map = new HashMap<>();
        map.put(isGet, 2);
        map.put(getter, 1);
        map.put(property, 0);

        Method mGetCandidate = null;

        for (Method method : clazz.getMethods()) {
            if (BasicUtils.isPublic(method)
                    && method.getParameterTypes().length == 0
                    && (getter.equals(method.getName())
                    || property.equals(method.getName())
                    || ((isGet.equals(method.getName())) && method.getReturnType() == boolean.class))) {
                if (mGetCandidate == null || BasicUtils.isPreferredGetter(mGetCandidate, method, map)) {
                    if( isStaticCheck && !BasicUtils.isStatic(method)){
                        mGetCandidate = method;
                    }else {
                        mGetCandidate = method;
                    }
                }
            }
        }
        return mGetCandidate;
    }


    public static Method getSetter(Class<?> clazz, String property) {
        property = BasicUtils.getSetter(property);

        for (Method method : clazz.getMethods()) {
            if (BasicUtils.isPublic(method) && method.getParameterTypes().length == 1
                    && property.equals(method.getName())) {
                return method;
            }
        }
        return null;
    }


    public static boolean hasOnlyOneAbstractMethod(Method[] methods) {
        int count = 0;
        for (Method method : methods) {
            if (Modifier.isAbstract(method.getModifiers())) {
                count++;
            }
        }
        return count == 1;
    }

}