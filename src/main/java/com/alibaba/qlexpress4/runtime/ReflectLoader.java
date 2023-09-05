package com.alibaba.qlexpress4.runtime;

import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.exception.QLRuntimeException;
import com.alibaba.qlexpress4.member.ConstructorHandler;
import com.alibaba.qlexpress4.member.FieldHandler;
import com.alibaba.qlexpress4.member.MethodHandler;
import com.alibaba.qlexpress4.runtime.data.DataValue;
import com.alibaba.qlexpress4.runtime.data.FieldValue;
import com.alibaba.qlexpress4.runtime.data.MapItemValue;
import com.alibaba.qlexpress4.runtime.data.implicit.ConstructorReflect;
import com.alibaba.qlexpress4.runtime.data.implicit.MethodReflect;
import com.alibaba.qlexpress4.utils.BasicUtil;
import com.alibaba.qlexpress4.utils.PropertiesUtil;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * java reflect util with cache
 * Author: DQinYuan
 */
public class ReflectLoader {

    private final boolean allowPrivateAccess;

    private final Map<List<Class<?>>, ConstructorReflect> constructorCache = new ConcurrentHashMap<>();

    private final Map<List<?>, Optional<FieldReflectCache>> fieldCache = new ConcurrentHashMap<>();

    private final Map<Map.Entry<Class<?>, String>, Optional<PolyMethods>> staticMethodCache = new ConcurrentHashMap<>();

    private final Map<Map.Entry<Class<?>, String>, Optional<PolyMethods>> memberMethodCache = new ConcurrentHashMap<>();

    public ReflectLoader(boolean allowPrivateAccess) {
        this.allowPrivateAccess = allowPrivateAccess;
    }

    public ConstructorReflect loadConstructor(Class<?> cls, Class<?>[] paramTypes) {
        List<Class<?>> cacheKey = new ArrayList<>(paramTypes.length + 1);
        cacheKey.add(cls);
        cacheKey.addAll(Arrays.asList(paramTypes));

        return constructorCache.computeIfAbsent(cacheKey, ignore ->
                ConstructorHandler.Preferred.findConstructorMostSpecificSignature(cls, paramTypes, allowPrivateAccess));
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

    public Optional<PolyMethods> loadMethod(Object bean, String methodName) {
        return bean instanceof MetaClass?
                loadStaticMethod(((MetaClass) bean).getClz(), methodName):
                loadMemberMethod(bean, methodName);
    }

    public Optional<PolyMethods> loadStaticMethod(Class<?> cls, String methodName) {
        return staticMethodCache.computeIfAbsent(new AbstractMap.SimpleEntry<>(cls, methodName), ignore -> {
            List<Method> clzMethods = PropertiesUtil.getClzMethod(cls, methodName, allowPrivateAccess);
            if (clzMethods.isEmpty()) {
                return Optional.empty();
            }
            return Optional.of(new PolyMethods(clzMethods));
        });
    }

    public Optional<PolyMethods> loadMemberMethod(Object bean, String methodName) {
        return memberMethodCache.computeIfAbsent(new AbstractMap.SimpleEntry<>(bean.getClass(), methodName),
                ignore -> {
                    List<Method> memberMethods = PropertiesUtil.getMethod(bean, methodName, allowPrivateAccess);
                    if (memberMethods.isEmpty()) {
                        return Optional.empty();
                    }
                    return Optional.of(new PolyMethods(memberMethods));
        });
    }

    private MethodReflect loadStaticMethodInner(Class<?> cls, String methodName, Class<?>[] paramTypes) {
        List<Method> methods = PropertiesUtil.getClzMethod(cls, methodName, allowPrivateAccess);
        return MethodHandler.Preferred.findMostSpecificMethod(paramTypes, methods.toArray(new Method[0]));
    }

    private Value loadJavaField(Class<?> cls, Object bean, String fieldName, ErrorReporter errorReporter) {
        Optional<FieldReflectCache> fieldReflectCacheOp = fieldCache.computeIfAbsent(
                Arrays.asList(cls, fieldName),
                ignore -> loadFieldInner(cls, fieldName)
        );
        if (!fieldReflectCacheOp.isPresent()) {
            return null;
        }
        FieldReflectCache fieldReflectCache = fieldReflectCacheOp.get();
        Supplier<Object> getterOp = fieldReflectCache.getterSupplier.apply(errorReporter, bean);
        if (fieldReflectCache.setterSupplier == null) {
            return new DataValue(getterOp.get());
        }
        Consumer<Object> setterOp = fieldReflectCache.setterSupplier.apply(errorReporter, bean);
        return new FieldValue(getterOp, setterOp, fieldReflectCache.defType);
    }

    private Optional<FieldReflectCache> loadFieldInner(Class<?> cls, String fieldName) {
        Method getMethod = MethodHandler.getGetter(cls, fieldName);
        Field field = FieldHandler.Preferred.gatherFieldRecursive(cls, fieldName);
        BiFunction<ErrorReporter, Object, Supplier<Object>> getterSupplier = fieldGetter(getMethod, field);
        if (getterSupplier == null) {
            return Optional.empty();
        }
        Method setMethod = MethodHandler.getSetter(cls, fieldName);
        BiFunction<ErrorReporter, Object, Consumer<Object>> setterSupplier = fieldSetter(setMethod, field);
        return Optional.of(new FieldReflectCache(getterSupplier, setterSupplier, fieldDefCls(setMethod, field)));
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
                throw errorReporter.report(e, "FIELD_SET_UNKNOWN_ERROR",
                        "'" + field.getName() + "' field set unknown error");
            }
        };
    }

    private BiFunction<ErrorReporter, Object, Consumer<Object>> setFieldUnAccessible(Field field) {
        return (errorReporter, bean) -> newValue -> {
            try {
                field.setAccessible(true);
                field.set(bean, newValue);
            } catch (Exception e) {
                throw errorReporter.report(e, "FIELD_SET_UNKNOWN_ERROR",
                        "'" + field.getName() + "' field set unknown error");
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
                throw errorReporter.report(e, "FIELD_GET_UNKNOWN_ERROR",
                        "'" + field.getName() + "' field get unknown error");
            }
        };
    }

    private BiFunction<ErrorReporter, Object, Supplier<Object>> getFieldUnAccessible(Field field) {
        return (errorReporter, bean) -> () -> {
            try {
                field.setAccessible(true);
                return field.get(bean);
            } catch (Exception e) {
                throw errorReporter.report(e, "FIELD_GET_UNKNOWN_ERROR",
                        "'" + field.getName() + "' field get unknown error");
            }
        };
    }

    public static QLRuntimeException unwrapMethodInvokeEx(ErrorReporter errorReporter, String methodName, Exception ex) {
        if (ex instanceof IllegalArgumentException) {
            return errorReporter.reportFormat("METHOD_INVOKE_WITH_WRONG_ARGUMENT",
                    "'" + methodName + "' method invoke with wrong argument");
        } else if (ex instanceof InvocationTargetException) {
            return errorReporter.report(((InvocationTargetException) ex).getTargetException(),
                    "METHOD_INNER_EXCEPTION", "'" + methodName + "' method invoke inner exception");
        } else {
            return errorReporter.report(ex, "METHOD_INVOKE_UNKNOWN_EXCEPTION",
                    "'" + methodName + "' method invoke with unknown error");
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

    public static class PolyMethods {

        private final List<Method> methods;

        private final Map<List<?>, Optional<MethodReflect>> typesMethod = new ConcurrentHashMap<>();

        public PolyMethods(List<Method> methods) {
            this.methods = methods;
        }

        public Optional<MethodReflect> getMethod(Class<?>[] paramTypes) {
            return typesMethod.computeIfAbsent(Arrays.asList(paramTypes), ignore ->
                    Optional.ofNullable(MethodHandler.Preferred
                            .findMostSpecificMethod(paramTypes, methods.toArray(new Method[0]))));
        }
    }
}
