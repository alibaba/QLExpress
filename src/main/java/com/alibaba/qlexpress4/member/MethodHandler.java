package com.alibaba.qlexpress4.member;

import com.alibaba.qlexpress4.runtime.data.implicit.QLCandidateMethodAttr;
import com.alibaba.qlexpress4.runtime.data.implicit.QLImplicitMatcher;
import com.alibaba.qlexpress4.runtime.data.implicit.MethodReflect;
import com.alibaba.qlexpress4.utils.BasicUtil;
import com.alibaba.qlexpress4.utils.QLAliasUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: TaoKan
 */
public class MethodHandler extends MemberHandler {

    public static Method getGetter(Class<?> clazz, String property) {
        String isGet = BasicUtil.getIsGetter(property);
        String getter = BasicUtil.getGetter(property);
        GetterCandidateMethod mGetCandidate = null;
        for (Method method : clazz.getMethods()) {
            if ((isGet.equals(method.getName())) && method.getReturnType() == boolean.class
                            && BasicUtil.isPublic(method) && method.getParameterTypes().length == 0) {
                GetterCandidateMethod isGetMethod = new GetterCandidateMethod(method, 2);
                if (isPreferredGetter(mGetCandidate, isGetMethod)) {
                    mGetCandidate = isGetMethod;
                }
            } else if (getter.equals(method.getName()) && BasicUtil.isPublic(method) && method.getParameterTypes().length == 0) {
                GetterCandidateMethod getterMethod = new GetterCandidateMethod(method, 1);
                if (isPreferredGetter(mGetCandidate, getterMethod)) {
                    mGetCandidate = getterMethod;
                }
            }
        }
        return mGetCandidate == null ? null : mGetCandidate.getMethod();
    }

    public static boolean isPreferredGetter(GetterCandidateMethod before, GetterCandidateMethod after) {
        if (before == null) {
            return true;
        }
        return after.getPriority() >= before.getPriority();
    }


    public static Method getSetter(Class<?> clazz, String property) {
        property = BasicUtil.getSetter(property);

        for (Method method : clazz.getMethods()) {
            if (BasicUtil.isPublic(method) && method.getParameterTypes().length == 1
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

    public static class Preferred {
        /**
         * @param idealMatch idealMatch
         * @param methods methods
         * @return result
         */
        public static MethodReflect findMostSpecificMethod(Class<?>[] idealMatch, Method[] methods) {
            QLCandidateMethodAttr[] candidates = new QLCandidateMethodAttr[methods.length];
            Method compareMethod = null;
            int level = 1;
            for (int i = 0; i < methods.length; i++) {
                level += compareLevelOfMethod(compareMethod,methods[i]);
                compareMethod = methods[i];
                candidates[i] = new QLCandidateMethodAttr(methods[i].getParameterTypes(),level);
            }
            QLImplicitMatcher matcher = MemberHandler.Preferred.findMostSpecificSignature(idealMatch, candidates);
            return matcher.getMatchIndex() == BasicUtil.DEFAULT_MATCH_INDEX ? null :
                    new MethodReflect(methods[matcher.getMatchIndex()],matcher.needImplicitTrans(), matcher.getVars());
        }


        public static int compareLevelOfMethod(Method before, Method after){
            if(before == null){
                return 0;
            }
            if(before.getDeclaringClass() != after.getDeclaringClass() && after.getDeclaringClass().isAssignableFrom(before.getDeclaringClass())){
                return 1;
            }
            return 0;
        }

        public static List<Method> gatherMethodsRecursive(Class<?> baseClass, String methodName, boolean isAllowAccessPrivate) {
            return gatherMethodsRecursive(baseClass, methodName, false, null, !isAllowAccessPrivate, false, null);
        }

        public static List<Method> gatherMethodsRecursive(Class<?> baseClass, String methodName, boolean isAllowAccessPrivate, List<Method> candidates) {
            return gatherMethodsRecursive(baseClass, methodName, false, null, !isAllowAccessPrivate, false, candidates);
        }

        public static List<Method> gatherMethodsRecursive(Class<?> baseClass, String methodName, boolean isAllowAccessPrivate, Object[] args) {
            return gatherMethodsRecursive(baseClass, methodName, true, args.length, !isAllowAccessPrivate, false, null);
        }

        public static List<Method> gatherMethodsRecursive(Class<?> baseClass, String methodName, boolean isAllowAccessPrivate, Object[] args, List<Method> candidates) {
            return gatherMethodsRecursive(baseClass, methodName, true, args.length, !isAllowAccessPrivate, false, candidates);
        }


        public static List<Method> gatherMethodsRecursive(Class<?> baseClass, String methodName, boolean argCheck, Integer numArgs,
                                                          boolean publicOnly, boolean isStatic, List<Method> candidates) {
            if (candidates == null) {
                candidates = new ArrayList<>();
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


        private static boolean candidateCheck(Method method, boolean argCheck, Integer numArgs, boolean publicOnly, boolean isStatic) {
            if (argCheck) {
                if (method.getParameterTypes().length == numArgs) {
                    return (!publicOnly || BasicUtil.isPublic(method) && (!isStatic || BasicUtil.isStatic(method)));
                } else {
                    return false;
                }
            } else {
                return (!publicOnly || BasicUtil.isPublic(method) && (!isStatic || BasicUtil.isStatic(method)));
            }
        }

        public static List<Method> addCandidatesMethod(Method[] methods, String methodName, boolean argCheck,
                                                       Integer numArgs, boolean publicOnly, boolean isStatic, List<Method> candidates) {
            for (Method method : methods) {
                if (method.getName().equals(methodName) && candidateCheck(method, argCheck, numArgs, publicOnly, isStatic)) {
                    candidates.add(method);
                } else if (QLAliasUtil.containsQLAliasForMethod(method)) {
                    for (String value : QLAliasUtil.getQLAliasValue(method)) {
                        if (value.equals(methodName) && candidateCheck(method, argCheck, numArgs, publicOnly, isStatic)) {
                            candidates.add(method);
                        }
                    }
                }
            }
            return candidates;
        }
    }

    public static class Access {
        public static Class<?> accessMethodType(Member accessMember) {
            Method accessMethod = ((Method) accessMember);
            return accessMethod.getReturnType();
        }

        public static Object accessMethodValue(Method method, Object bean, Object[] params) throws
                IllegalArgumentException, InvocationTargetException, IllegalAccessException {
            if (!BasicUtil.isPublic(method)) {
                method.setAccessible(true);
            }
            return method.invoke(bean, params);
        }

        public static void setAccessMethodValue(Member accessMember, Object bean, Object value, boolean allowAccessPrivateMethod)
                throws IllegalArgumentException, InvocationTargetException, IllegalAccessException {
            Method accessMethod = ((Method) accessMember);
            if (BasicUtil.isPublic(accessMethod)) {
                accessMethod.invoke(bean, value);
            } else {
                if(!allowAccessPrivateMethod){
                    throw new IllegalAccessException("can not allow access");
                }else {
                    synchronized (accessMethod) {
                        try {
                            accessMethod.setAccessible(true);
                            accessMethod.invoke(bean, value);
                        } finally {
                            accessMethod.setAccessible(false);
                        }
                    }
                }
            }

        }
    }


    static class GetterCandidateMethod {
        public Method getMethod() {
            return method;
        }

        public void setMethod(Method method) {
            this.method = method;
        }

        private Method method;

        public int getPriority() {
            return priority;
        }

        public void setPriority(int priority) {
            this.priority = priority;
        }

        private int priority;

        public GetterCandidateMethod(Method method, int priority) {
            this.method = method;
            this.priority = priority;
        }
    }
}
