package com.alibaba.qlexpress4.member;

import com.alibaba.qlexpress4.enums.AccessMode;
import com.alibaba.qlexpress4.utils.BasicUtils;
import com.alibaba.qlexpress4.utils.ExpressUtil;
import com.ql.util.express.QLambda;

import java.lang.reflect.Field;
import java.lang.reflect.Member;

/**
 * @Author TaoKan
 * @Date 2022/4/7 下午6:05
 */
public class MemberHandler {
    public static final String IS_EMPTY = "isEmpty";
    public static final String IS_SIGN = "is";
    public static final String GET_SIGN = "get";

    public static class Access{
        public static Member getAccessMember(Class clazz, String property, AccessMode propertyMode, boolean isStaticCheck) {
            Field f = null;
            try {
                f = clazz.getField(property);
                return f;
            } catch (NoSuchFieldException e) {

            }
            //from Method
            if(AccessMode.READ.equals(propertyMode)){
                return MethodHandler.getGetter(clazz, property, isStaticCheck);
            }else {
                return MethodHandler.getSetter(clazz, property);
            }
        }

    }

    public static class Preferred{


        public static int findMostSpecificSignature(Class<?>[] idealMatch,Class<?>[][] candidates) {
            Class<?>[] bestMatch = null;
            int bestMatchIndex = -1;

            for (int i = candidates.length - 1; i >= 0; i--) {
                Class<?>[] targetMatch = candidates[i];
                if (isSignatureAssignable(idealMatch, targetMatch) && ((bestMatch == null)
                        || isSignatureAssignable(targetMatch, bestMatch))) {
                    bestMatch = targetMatch;
                    bestMatchIndex = i;
                }
            }

            if (bestMatch != null) {
                return bestMatchIndex;
            } else {
                return -1;
            }
        }

        private static boolean isSignatureAssignable(Class<?>[] from, Class<?>[] to) {
            for (int i = 0; i < from.length; i++) {
                if (!isAssignable(to[i], from[i])) {
                    return false;
                }
            }
            return true;
        }

        private static boolean isAssignable(Class<?> target, Class<?> source) {
            if (target == source) {
                return true;
            }
            if (target.isArray() && source.isArray()) {
                return isAssignable(target.getComponentType(), source.getComponentType());
            }
            return isAssignablePrivate(target, source);
        }

        private static boolean isAssignablePrivate(Class<?> target, Class<?> source) {
            if (target == source) {
                return true;
            }

            if (target == null) {
                return false;
            }

            if (source == null) {
                return !target.isPrimitive();
            }

            if (target.isAssignableFrom(source)) {
                return true;
            }
            if (source.isPrimitive() && target == Object.class) {
                return true;
            }

            if (!target.isPrimitive()) {
                if (target == Byte.class) {
                    target = byte.class;
                } else if (target == Short.class) {
                    target = short.class;
                } else if (target == Integer.class) {
                    target = int.class;
                } else if (target == Long.class) {
                    target = long.class;
                } else if (target == Float.class) {
                    target = float.class;
                } else if (target == Double.class) {
                    target = double.class;
                }
            }
            if (!source.isPrimitive()) {
                if (source == Byte.class) {
                    source = byte.class;
                } else if (source == Short.class) {
                    source = short.class;
                } else if (source == Integer.class) {
                    source = int.class;
                } else if (source == Long.class) {
                    source = long.class;
                } else if (source == Float.class) {
                    source = float.class;
                } else if (source == Double.class) {
                    source = double.class;
                }
            }

            if (target == source) {
                return true;
            }

            if (source == QLambda.class && ExpressUtil.isFunctionInterface(target)) {
                return true;
            }

            for (Class<?>[] classMatch : BasicUtils.CLASS_MATCHES) {
                if (target == classMatch[0] && source == classMatch[1]) {
                    return true;
                }
            }

            return false;
        }

    }

}
