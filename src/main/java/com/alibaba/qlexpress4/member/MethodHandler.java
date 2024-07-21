package com.alibaba.qlexpress4.member;

import com.alibaba.qlexpress4.runtime.IMethod;
import com.alibaba.qlexpress4.utils.BasicUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * Author: TaoKan
 */
public class MethodHandler {

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
                if (count > 1) {
                    return false;
                }
            }
        }
        return count == 1;
    }

    public static class Access {
        public static Object accessMethodValue(IMethod method, Object bean, Object[] args) throws
                IllegalArgumentException, InvocationTargetException, IllegalAccessException {
            if (!method.isAccess()) {
                method.setAccessible(true);
            }
            return method.invoke(bean, args);
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
