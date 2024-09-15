package com.alibaba.qlexpress4.runtime;

import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.exception.QLErrorCodes;
import com.alibaba.qlexpress4.exception.QLRuntimeException;
import com.alibaba.qlexpress4.member.FieldHandler;
import com.alibaba.qlexpress4.member.MethodHandler;
import com.alibaba.qlexpress4.runtime.data.DataValue;
import com.alibaba.qlexpress4.runtime.data.FieldValue;
import com.alibaba.qlexpress4.runtime.data.MapItemValue;
import com.alibaba.qlexpress4.runtime.function.ExtensionFunction;
import com.alibaba.qlexpress4.security.QLSecurityStrategy;
import com.alibaba.qlexpress4.utils.BasicUtil;

import java.io.Serializable;
import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * java reflect util with cache
 * Author: DQinYuan
 */
public class ReflectLoader {

    private final QLSecurityStrategy securityStrategy;

    private final boolean allowPrivateAccess;

    private final Map<List<Class<?>>, Constructor<?>> constructorCache = new ConcurrentHashMap<>();

    private final Map<List<?>, Optional<FieldReflectCache>> fieldCache = new ConcurrentHashMap<>();

    private final Map<MethodCacheKey, Method> staticMethodCache = new ConcurrentHashMap<>();

    private final Map<MethodCacheKey, Method> memberMethodCache = new ConcurrentHashMap<>();

    private final List<ExtensionFunction> extensionFunctions;

    public ReflectLoader(QLSecurityStrategy securityStrategy,
                         List<ExtensionFunction> extensionFunctions, boolean allowPrivateAccess) {
        this.securityStrategy = securityStrategy;
        this.allowPrivateAccess = allowPrivateAccess;
        this.extensionFunctions = extensionFunctions;
    }

    public Constructor<?> loadConstructor(Class<?> cls, Class<?>[] paramTypes) {
        List<Class<?>> cacheKey = new ArrayList<>(paramTypes.length + 1);
        cacheKey.add(cls);
        cacheKey.addAll(Arrays.asList(paramTypes));

        Constructor<?> cachedConstructor = constructorCache.get(cacheKey);
        if (cachedConstructor != null) {
            return cachedConstructor;
        }

        Constructor<?> constructor = securityFilter(MemberResolver.resolveConstructor(cls, paramTypes));
        if (constructor == null) {
            return null;
        }
        constructorCache.put(cacheKey, constructor);
        return constructor;
    }

    public Value loadField(Object bean, String fieldName, ErrorReporter errorReporter) {
        if (bean.getClass().isArray() && BasicUtil.LENGTH.equals(fieldName)) {
            return new DataValue(((Object[]) bean).length);
        } else if (bean instanceof List && BasicUtil.LENGTH.equals(fieldName)) {
            return new DataValue(((List<?>) bean).size());
        } else if (bean instanceof MetaClass) {
            MetaClass metaClass = (MetaClass) bean;
            if (BasicUtil.CLASS.equals(fieldName)) {
                return new DataValue(metaClass.getClz());
            }
            return loadJavaField(metaClass.getClz(), null, fieldName, errorReporter);
        } else if (bean instanceof Map) {
            return new MapItemValue((Map<?, ?>) bean, fieldName);
        } else {
            return loadJavaField(bean.getClass(), bean, fieldName, errorReporter);
        }
    }

    public IMethod loadMethod(Object bean, String methodName, Class<?>[] argTypes) {
        boolean isStaticMethod = bean instanceof MetaClass;
        Class<?> clz = isStaticMethod ? ((MetaClass) bean).getClz(): bean.getClass();
        MethodCacheKey cacheKey = new MethodCacheKey(clz, methodName, argTypes);
        Map<MethodCacheKey, Method> methodCache = isStaticMethod ? staticMethodCache: memberMethodCache;
        Method cachedMethod = methodCache.get(cacheKey);
        if (cachedMethod != null) {
            return new JvmIMethod(cachedMethod);
        }

        // only support member extension method
        if (!isStaticMethod) {
            IMethod extendFunction = loadExtendFunction(clz, methodName, argTypes);
            if (extendFunction != null) {
                return extendFunction;
            }
        }

        Method method = securityFilter(
                MemberResolver.resolveMethod(clz, methodName, argTypes, isStaticMethod, allowPrivateAccess)
        );
        if (method == null) {
            return null;
        }

        methodCache.put(cacheKey, method);
        return new JvmIMethod(method);
    }

    private IMethod loadExtendFunction(Class<?> clz, String methodName, Class<?>[] argTypes) {
        List<ExtensionFunction> assignableExtensionFunctions = extensionFunctions.stream()
                .filter(extensionFunction -> extensionFunction.getDeclaringClass().isAssignableFrom(clz) &&
                        methodName.equals(extensionFunction.getName()))
                .collect(Collectors.toList());
        if (assignableExtensionFunctions.isEmpty()) {
            return null;
        }

        Class<?>[][] candidates = new Class<?>[assignableExtensionFunctions.size()][];
        for (int i = 0; i < assignableExtensionFunctions.size(); i++) {
            candidates[i] = assignableExtensionFunctions.get(i).getParameterTypes();
        }
        Integer bestIndex = MemberResolver.resolveBestMatch(candidates, argTypes);
        if (bestIndex == null) {
            return null;
        }
        return assignableExtensionFunctions.get(bestIndex);
    }

    private Value loadJavaField(Class<?> cls, Object bean, String fieldName, ErrorReporter errorReporter) {
        FieldReflectCache fieldReflectCache = loadFieldReflectCache(cls, fieldName);
        if (fieldReflectCache == null) {
            return null;
        }
        Supplier<Object> getterOp = fieldReflectCache.getterSupplier.apply(errorReporter, bean);
        if (fieldReflectCache.setterSupplier == null) {
            return new DataValue(getterOp.get());
        }
        Consumer<Object> setterOp = fieldReflectCache.setterSupplier.apply(errorReporter, bean);
        return new FieldValue(getterOp, setterOp, fieldReflectCache.defType);
    }

    private FieldReflectCache loadFieldReflectCache(Class<?> cls, String fieldName) {
        List<Serializable> cacheKey = Arrays.asList(cls, fieldName);
        Optional<FieldReflectCache> cachedFieldOp = fieldCache.get(cacheKey);
        if (cachedFieldOp != null) {
            return cachedFieldOp.orElse(null);
        }

        Optional<FieldReflectCache> fieldReflectOp = loadJavaFieldInner(cls, fieldName);
        fieldCache.put(cacheKey, fieldReflectOp);
        return fieldReflectOp.orElse(null);
    }

    private Optional<FieldReflectCache> loadJavaFieldInner(Class<?> cls, String fieldName) {
        Method getMethod = securityFilter(MethodHandler.getGetter(cls, fieldName));
        Field field = securityFilter(FieldHandler.Preferred.gatherFieldRecursive(cls, fieldName));
        BiFunction<ErrorReporter, Object, Supplier<Object>> getterSupplier = fieldGetter(getMethod, field);
        if (getterSupplier == null) {
            return Optional.empty();
        }
        Method setMethod = securityFilter(MethodHandler.getSetter(cls, fieldName));
        BiFunction<ErrorReporter, Object, Consumer<Object>> setterSupplier = fieldSetter(setMethod, field);
        return Optional.of(new FieldReflectCache(getterSupplier, setterSupplier, fieldDefCls(setMethod, field)));
    }

    private <T extends Member> T securityFilter(T member) {
        return member == null? null: (securityStrategy.check(member)? member: null);
    }

    private Class<?> fieldDefCls(Method setMethod, Field field) {
        return setMethod != null? setMethod.getParameterTypes()[0]:
                field != null? field.getType(): Object.class;
    }

    private BiFunction<ErrorReporter, Object, Consumer<Object>> fieldSetter(Method setMethod, Field field) {
        if (setMethod != null) {
            if (BasicUtil.isPublic(setMethod)) {
                return setMethodAccessible(setMethod);
            }
            if (allowPrivateAccess) {
                return setMethodUnAccessible(setMethod);
            }
        }
        if (field != null) {
            if (BasicUtil.isPublic(field)) {
                return setFieldAccessible(field);
            }
            if (allowPrivateAccess) {
                return setFieldUnAccessible(field);
            }
        }
        return null;
    }

    private BiFunction<ErrorReporter, Object, Supplier<Object>> fieldGetter(Method getMethod, Field field) {
        if(getMethod != null){
            if(BasicUtil.isPublic(getMethod)){
                return getMethodAccessible(getMethod);
            }
            if(allowPrivateAccess){
                return getMethodUnAccessible(getMethod);
            }
        }
        if (field != null) {
            if(BasicUtil.isPublic(field)){
                return getFieldAccessible(field);
            }
            if(allowPrivateAccess) {
                return getFieldUnAccessible(field);
            }
        }
        return null;
    }

    private BiFunction<ErrorReporter, Object, Consumer<Object>> setMethodAccessible(Method setMethod) {
        return (errorReporter, bean) -> newValue -> {
            try {
                setMethod.invoke(bean, newValue);
            } catch (Exception e) {
                throw unwrapMethodInvokeEx(errorReporter, setMethod.getName(), e);
            }
        };
    }

    private BiFunction<ErrorReporter, Object, Consumer<Object>> setMethodUnAccessible(Method setMethod) {
        return (errorReporter, bean) -> newValue -> {
            try {
                setMethod.setAccessible(true);
                setMethod.invoke(bean, newValue);
            } catch (Exception e) {
                throw unwrapMethodInvokeEx(errorReporter, setMethod.getName(), e);
            }
        };
    }

    private BiFunction<ErrorReporter, Object, Consumer<Object>> setFieldAccessible(Field field) {
        return (errorReporter, bean) -> newValue -> {
            try {
                field.set(bean, newValue);
            } catch (Exception e) {
                throw errorReporter.report(e, QLErrorCodes.SET_FIELD_UNKNOWN_ERROR.name(),
                        String.format(QLErrorCodes.SET_FIELD_UNKNOWN_ERROR.getErrorMsg(), field.getName()));
            }
        };
    }

    private BiFunction<ErrorReporter, Object, Consumer<Object>> setFieldUnAccessible(Field field) {
        return (errorReporter, bean) -> newValue -> {
            try {
                field.setAccessible(true);
                field.set(bean, newValue);
            } catch (Exception e) {
                throw errorReporter.report(e, QLErrorCodes.SET_FIELD_UNKNOWN_ERROR.name(),
                        String.format(QLErrorCodes.SET_FIELD_UNKNOWN_ERROR.getErrorMsg(), field.getName()));
            }
        };
    }

    private BiFunction<ErrorReporter, Object, Supplier<Object>> getMethodAccessible(Method getMethod) {
        return (errorReporter, bean) -> () -> {
            try {
                return getMethod.invoke(bean);
            } catch (Exception e) {
                throw unwrapMethodInvokeEx(errorReporter, getMethod.getName(), e);
            }
        };
    }

    private BiFunction<ErrorReporter, Object, Supplier<Object>> getMethodUnAccessible(Method getMethod) {
        return (errorReporter, bean) -> () -> {
            try {
                getMethod.setAccessible(true);
                return getMethod.invoke(bean);
            } catch (Exception e) {
                throw unwrapMethodInvokeEx(errorReporter, getMethod.getName(), e);
            }
        };
    }

    private BiFunction<ErrorReporter, Object, Supplier<Object>> getFieldAccessible(Field field) {
        return (errorReporter, bean) -> () -> {
            try {
                return field.get(bean);
            } catch (Exception e) {
                throw errorReporter.report(e, QLErrorCodes.GET_FIELD_UNKNOWN_ERROR.name(),
                        String.format(QLErrorCodes.GET_FIELD_UNKNOWN_ERROR.getErrorMsg(), field.getName()));
            }
        };
    }

    private BiFunction<ErrorReporter, Object, Supplier<Object>> getFieldUnAccessible(Field field) {
        return (errorReporter, bean) -> () -> {
            try {
                field.setAccessible(true);
                return field.get(bean);
            } catch (Exception e) {
                throw errorReporter.report(e, QLErrorCodes.GET_FIELD_UNKNOWN_ERROR.name(),
                        String.format(QLErrorCodes.GET_FIELD_UNKNOWN_ERROR.getErrorMsg(), field.getName()));
            }
        };
    }

    public static QLRuntimeException unwrapMethodInvokeEx(ErrorReporter errorReporter, String methodName, Exception ex) {
        if (ex instanceof IllegalArgumentException) {
            return errorReporter.reportFormat(QLErrorCodes.INVOKE_METHOD_WITH_WRONG_ARGUMENTS.name(),
                    String.format(QLErrorCodes.INVOKE_METHOD_WITH_WRONG_ARGUMENTS.getErrorMsg(), methodName));
        } else if (ex instanceof InvocationTargetException) {
            return errorReporter.report(((InvocationTargetException) ex).getTargetException(),
                    QLErrorCodes.INVOKE_METHOD_INNER_ERROR.name(),
                    String.format(QLErrorCodes.INVOKE_METHOD_INNER_ERROR.getErrorMsg(), methodName));
        } else {
            return errorReporter.report(ex, QLErrorCodes.INVOKE_METHOD_UNKNOWN_ERROR.name(),
                    String.format(QLErrorCodes.INVOKE_METHOD_UNKNOWN_ERROR.getErrorMsg(), methodName));
        }
    }

    private static class FieldReflectCache {
        private final BiFunction<ErrorReporter, Object, Supplier<Object>> getterSupplier;
        private final BiFunction<ErrorReporter, Object, Consumer<Object>> setterSupplier;
        private final Class<?> defType;

        private FieldReflectCache(BiFunction<ErrorReporter, Object, Supplier<Object>> getterSupplier,
                                  BiFunction<ErrorReporter, Object, Consumer<Object>> setterSupplier, Class<?> defType) {
            this.getterSupplier = getterSupplier;
            this.setterSupplier = setterSupplier;
            this.defType = defType;
        }
    }

    private static class ExtensionMapKey {
        private final Class<?> cls;
        private final String methodName;

        public ExtensionMapKey(Class<?> cls, String methodName) {
            this.cls = cls;
            this.methodName = methodName;
        }

        public Class<?> getCls() {
            return cls;
        }

        public String getMethodName() {
            return methodName;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ExtensionMapKey that = (ExtensionMapKey) o;
            return Objects.equals(cls, that.cls) && Objects.equals(methodName, that.methodName);
        }

        @Override
        public int hashCode() {
            return Objects.hash(cls, methodName);
        }
    }

    private static class MethodCacheKey {
        private final Class<?> cls;
        private final String methodName;
        private final Class<?>[] argTypes;

        public MethodCacheKey(Class<?> cls, String methodName, Class<?>[] argTypes) {
            this.cls = cls;
            this.methodName = methodName;
            this.argTypes = argTypes;
        }

        public Class<?> getCls() {
            return cls;
        }

        public String getMethodName() {
            return methodName;
        }

        public Class<?>[] getArgTypes() {
            return argTypes;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            MethodCacheKey that = (MethodCacheKey) o;
            return cls.equals(that.cls) && methodName.equals(that.methodName) && Arrays.equals(argTypes, that.argTypes);
        }

        @Override
        public int hashCode() {
            int result = Objects.hash(cls, methodName);
            result = 31 * result + Arrays.hashCode(argTypes);
            return result;
        }
    }
}
