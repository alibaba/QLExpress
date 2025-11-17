package com.alibaba.qlexpress4.runtime;

import com.alibaba.qlexpress4.annotation.QLAlias;
import com.alibaba.qlexpress4.utils.BasicUtil;
import com.alibaba.qlexpress4.utils.CacheUtil;
import com.alibaba.qlexpress4.utils.QLAliasUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Author: DQinYuan
 */
public class MemberResolver {
    
    public enum MatchPriority {
        MISMATCH(-1),
        // e.g. Integer -> Number
        EXTEND(0),
        // e.g. BigDecimal -> int
        NUMBER_DEMOTION(9),
        // e.g. int -> long
        // 1 -> 8
        NUMBER_PROMOTION(8),
        // e.g. Integer -> int
        UNBOX(9),
        // e.g. QLambda -> Function, Runnable, ...
        LAMBDA(10),
        // e.g. Integer -> Integer
        EQUAL(11);
        
        public final int priority;
        
        MatchPriority(int priority) {
            this.priority = priority;
        }
    }
    
    public static Constructor<?> resolveConstructor(Class<?> cls, Class<?>[] argTypes) {
        Constructor<?>[] constructors = cls.getConstructors();
        
        // simple match
        Class<?>[][] candidates = new Class<?>[constructors.length][];
        for (int i = 0; i < constructors.length; i++) {
            candidates[i] = constructors[i].getParameterTypes();
        }
        Integer bestIndex = resolveBestMatch(candidates, argTypes);
        if (bestIndex != null) {
            return constructors[bestIndex];
        }
        
        // var args match
        List<Class<?>[]> varArgsCandidates = new ArrayList<>(constructors.length);
        List<Integer> varArgsConstructorI = new ArrayList<>(constructors.length);
        for (int i = 0; i < constructors.length; i++) {
            Constructor<?> constructor = constructors[i];
            if (!constructor.isVarArgs()) {
                continue;
            }
            varArgsCandidates.add(adapt2VarArgTypes(constructor.getParameterTypes(), argTypes.length));
            varArgsConstructorI.add(i);
        }
        Integer varArgBestIndex = resolveBestMatch(varArgsCandidates.toArray(new Class[0][]), argTypes);
        if (varArgBestIndex == null) {
            return null;
        }
        return constructors[varArgsConstructorI.get(varArgBestIndex)];
    }
    
    public static IMethod resolveMethod(Class<?> cls, String methodName, Class<?>[] argTypes, boolean isStatic,
        boolean allowPrivate) {
        Class<?> curCls = cls;
        List<Class<?>> inters = new ArrayList<>();
        while (curCls != null) {
            IMethod method = resolveDeclaredMethod(curCls, methodName, argTypes, isStatic, allowPrivate);
            if (method != null) {
                return method;
            }
            // collect interfaces implemented by current class to search default methods later
            Class<?>[] curInters = curCls.getInterfaces();
            inters.addAll(Arrays.asList(curInters));
            
            curCls = curCls.getSuperclass();
        }
        // interface method
        return resolveIntersMethod(inters.toArray(new Class[0]), methodName, argTypes, isStatic);
    }
    
    private static IMethod resolveIntersMethod(Class<?>[] inters, String methodName, Class<?>[] argTypes,
        boolean isStatic) {
        for (Class<?> inter : inters) {
            IMethod method = resolveInterMethod(inter, methodName, argTypes, isStatic);
            if (method != null) {
                return method;
            }
        }
        return null;
    }
    
    private static IMethod resolveInterMethod(Class<?> inter, String methodName, Class<?>[] argTypes,
        boolean isStatic) {
        // no private method in interface, so pass false to 'allowPrivate'
        IMethod method = resolveDeclaredMethod(inter, methodName, argTypes, isStatic, false);
        if (method != null) {
            return method;
        }
        return resolveIntersMethod(inter.getInterfaces(), methodName, argTypes, isStatic);
    }
    
    public static int resolvePriority(Class<?>[] paramTypes, Class<?>[] argTypes) {
        if (paramTypes.length != argTypes.length) {
            return MatchPriority.MISMATCH.priority;
        }
        int methodPriority = MatchPriority.EQUAL.priority;
        for (int i = 0; i < paramTypes.length; i++) {
            Class<?> paramType = paramTypes[i];
            Class<?> argType = argTypes[i];
            int paramPriority = resolveArgPriority(paramType, argType);
            if (paramPriority == MatchPriority.MISMATCH.priority) {
                return paramPriority;
            }
            if (paramPriority < methodPriority) {
                // methodPriority is min(paramPriority)
                methodPriority = paramPriority;
            }
        }
        return methodPriority;
    }
    
    private static IMethod resolveDeclaredMethod(Class<?> cls, String methodName, Class<?>[] argTypes, boolean isStatic,
        boolean allowPrivate) {
        return resolveMethod(getDeclaredMethod(cls, methodName, isStatic, allowPrivate), argTypes);
    }
    
    public static IMethod resolveMethod(List<? extends IMethod> methods, Class<?>[] argTypes) {
        // simple match
        Class<?>[][] candidates = new Class<?>[methods.size()][];
        for (int i = 0; i < methods.size(); i++) {
            IMethod declaredMethod = methods.get(i);
            candidates[i] = declaredMethod.getParameterTypes();
        }
        Integer bestIndex = resolveBestMatch(candidates, argTypes);
        if (bestIndex != null) {
            return methods.get(bestIndex);
        }
        
        // var args match
        List<Class<?>[]> varArgsCandidates = new ArrayList<>(methods.size());
        List<Integer> varArgsMethodI = new ArrayList<>(methods.size());
        for (int i = 0; i < methods.size(); i++) {
            IMethod declaredMethod = methods.get(i);
            if (!declaredMethod.isVarArgs()) {
                continue;
            }
            varArgsCandidates.add(adapt2VarArgTypes(declaredMethod.getParameterTypes(), argTypes.length));
            varArgsMethodI.add(i);
        }
        Integer varArgBestIndex = resolveBestMatch(varArgsCandidates.toArray(new Class[0][]), argTypes);
        if (varArgBestIndex == null) {
            return null;
        }
        return methods.get(varArgsMethodI.get(varArgBestIndex));
    }
    
    private static List<IMethod> getDeclaredMethod(Class<?> cls, String methodName, boolean isStatic,
        boolean allowPrivate) {
        Method[] declaredMethods = cls.getDeclaredMethods();
        List<IMethod> result = new ArrayList<>(declaredMethods.length);
        for (Method declaredMethod : declaredMethods) {
            if (!methodName.equals(declaredMethod.getName())
                && !QLAliasUtils.matchQLAlias(methodName, declaredMethod.getAnnotationsByType(QLAlias.class))) {
                continue;
            }
            if ((!isStatic || BasicUtil.isStatic(declaredMethod))
                && (allowPrivate || BasicUtil.isPublic(declaredMethod))) {
                result.add(new JvmIMethod(declaredMethod));
            }
        }
        return result;
    }
    
    private static Class<?>[] adapt2VarArgTypes(Class<?>[] parameterTypes, int argLength) {
        Class<?> varItemType = parameterTypes[parameterTypes.length - 1].getComponentType();
        
        Class<?>[] varParamTypes = new Class<?>[argLength];
        System.arraycopy(parameterTypes, 0, varParamTypes, 0, parameterTypes.length - 1);
        for (int i = parameterTypes.length - 1; i < argLength; i++) {
            varParamTypes[i] = varItemType;
        }
        return varParamTypes;
    }
    
    public static Integer resolveBestMatch(Class<?>[][] candidates, Class<?>[] argTypes) {
        Integer bestMatchIndex = null;
        int bestPriority = MatchPriority.MISMATCH.priority;
        for (int i = 0; i < candidates.length; i++) {
            Class<?>[] candidate = candidates[i];
            int priority = resolvePriority(candidate, argTypes);
            if (priority > bestPriority) {
                bestPriority = priority;
                bestMatchIndex = i;
            }
        }
        return bestMatchIndex;
    }
    
    private static int resolveArgPriority(Class<?> paramType, Class<?> argType) {
        if (paramType == argType) {
            return MatchPriority.EQUAL.priority;
        }
        if (CacheUtil.isFunctionInterface(paramType) && QLambda.class.isAssignableFrom(argType)) {
            return MatchPriority.LAMBDA.priority;
        }
        
        Class<?> primitiveArgCls = argType.isPrimitive() ? argType : BasicUtil.transToPrimitive(argType);
        Class<?> primitiveParamCls = paramType.isPrimitive() ? paramType : BasicUtil.transToPrimitive(paramType);
        if (primitiveArgCls != null && primitiveArgCls == primitiveParamCls) {
            return MatchPriority.UNBOX.priority;
        }
        
        Integer paramNumLevel = BasicUtil.numberPromoteLevel(paramType);
        Integer argNumLevel = BasicUtil.numberPromoteLevel(argType);
        if (paramNumLevel != null && argNumLevel != null) {
            return paramNumLevel >= argNumLevel ? MatchPriority.NUMBER_PROMOTION.priority + argNumLevel - paramNumLevel
                : MatchPriority.NUMBER_DEMOTION.priority;
        }
        
        // Handle primitive to Object boxing conversion
        if (argType.isPrimitive() && paramType == Object.class) {
            return MatchPriority.EXTEND.priority;
        }
        
        if (argType == Nothing.class || paramType.isAssignableFrom(argType)) {
            return MatchPriority.EXTEND.priority;
        }
        return MatchPriority.MISMATCH.priority;
    }
}
