package com.alibaba.qlexpress4.utils;

import com.alibaba.qlexpress4.cache.QLFieldCache;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.exception.QLRuntimeException;
import com.alibaba.qlexpress4.member.FieldHandler;
import com.alibaba.qlexpress4.member.MethodHandler;
import com.alibaba.qlexpress4.runtime.MetaClass;
import com.alibaba.qlexpress4.runtime.Value;
import com.alibaba.qlexpress4.runtime.data.DataValue;
import com.alibaba.qlexpress4.runtime.data.FieldValue;
import com.alibaba.qlexpress4.runtime.data.MapItemValue;
import com.alibaba.qlexpress4.runtime.data.cache.CacheFieldValue;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @Author TaoKan
 * @Date 2022/5/28 下午5:23
 */
public class PropertiesUtil {

    public static Value getField(Object bean, String fieldName, QLFieldCache qlFieldCache,
                                 ErrorReporter errorReporter, boolean allowAccessPrivateMethod) {
        if (bean.getClass().isArray() && BasicUtil.LENGTH.equals(fieldName)) {
           return new DataValue(((Object[]) bean).length);
        } else if (bean instanceof List && BasicUtil.LENGTH.equals(fieldName)) {
            return new DataValue(((List<?>) bean).size());
        } else if (bean instanceof MetaClass) {
            MetaClass metaClass = (MetaClass) bean;
            if (BasicUtil.CLASS.equals(fieldName)) {
                return new DataValue(metaClass.getClz());
            } else {
                return getCacheFieldValue(metaClass.getClz(), null,
                        fieldName, qlFieldCache, errorReporter, allowAccessPrivateMethod);
            }
        } else if (bean instanceof Map) {
            return new MapItemValue((Map<?, ?>) bean, fieldName);
        } else {
            return getCacheFieldValue(bean.getClass(), bean,
                    fieldName, qlFieldCache, errorReporter, allowAccessPrivateMethod);
        }
    }

    /**
     * getValue from cache, if value not exist getInstance
     *
     * @param clazz
     * @param bean
     * @param qlFieldCache
     * @param errorReporter
     * @param allowAccessPrivateMethod
     * @return value
     */
    private static Value getCacheFieldValue(Class<?> clazz, Object bean,
                                            String fieldName, QLFieldCache qlFieldCache,
                                            ErrorReporter errorReporter, boolean allowAccessPrivateMethod) {
        CacheFieldValue cacheElement = CacheUtil.getFieldCacheElement(qlFieldCache, clazz, fieldName);
        if (cacheElement == null) {
            Method getMethod = MethodHandler.getGetter(clazz, fieldName);
            Method setMethod = MethodHandler.getSetter(clazz, fieldName);
            Field field = FieldHandler.Preferred.gatherFieldRecursive(clazz, fieldName);
            Value dataField = getFieldValue(fieldName, getMethod, setMethod, field,
                    bean, allowAccessPrivateMethod, errorReporter);
            CacheFieldValue cacheFieldValue = new CacheFieldValue(getMethod, setMethod, field);
            CacheUtil.setFieldCacheElement(qlFieldCache, clazz, fieldName, cacheFieldValue);
            return dataField;
        } else {
            return getFieldValue(fieldName, cacheElement.getGetMethod(), cacheElement.getSetMethod(),
                    cacheElement.getField(), bean, allowAccessPrivateMethod, errorReporter);
        }
    }

    private static Value getFieldValue(String fieldName, Method getMethod, Method setMethod, Field field, Object bean,
                                       boolean enableAllowAccessPrivateMethod, ErrorReporter errorReporter) {
        Supplier<Object> getterOp = operatorSelect(
                getMethodSupplierAccessible(getMethod, bean, errorReporter),
                getMethodSupplierNotAccessible(getMethod, bean, errorReporter),
                getFieldSupplierAccessible(field, bean, errorReporter),
                getFieldSupplierNotAccessible(field, bean, errorReporter),
                getMethod, field, enableAllowAccessPrivateMethod);

        Consumer<Object> setterOp = operatorSelect(
                getMethodConsumerAccessible(setMethod, bean, errorReporter),
                getMethodConsumerNotAccessible(setMethod, bean, errorReporter),
                getFieldConsumerAccessible(field, bean, errorReporter),
                getFieldConsumerNotAccessible(field, bean, errorReporter),
                setMethod, field, enableAllowAccessPrivateMethod);

        if (getterOp == null) {
            throw errorReporter.report("FIELD_INACCESSIBLE", "field '" + fieldName + "' inaccessible");
        }
        if (setterOp == null) {
            return new DataValue(getterOp.get());
        }
        return new FieldValue(getterOp, setterOp);
    }

    private static  <T> T operatorSelect(T methodAccess, T methodNotAccess,
                                         T fieldAccess, T fieldNotAccess,
                                         Method method, Field field, boolean enableAllowAccessPrivateMethod){
        if(method != null){
            if(BasicUtil.isPublic(method)){
                return methodAccess;
            }
            if(enableAllowAccessPrivateMethod){
                return methodNotAccess;
            }
        }
        if (field != null) {
            if(BasicUtil.isPublic(field)){
                return fieldAccess;
            }
            if(enableAllowAccessPrivateMethod) {
                return fieldNotAccess;
            }
        }
        return null;
    }


    private static Supplier<Object> getFieldSupplierAccessible(Field field, Object bean, ErrorReporter errorReporter) {
        return () -> {
            try {
                return field.get(bean);
            } catch (Exception e) {
                throw errorReporter.report(e, "FIELD_GET_UNKNOWN_ERROR",
                        "'" + field.getName() + "' field get unknown error");
            }
        };
    }

    private static Supplier<Object> getFieldSupplierNotAccessible(Field field, Object bean, ErrorReporter errorReporter) {
        return () -> {
            synchronized (field) {
                try {
                    field.setAccessible(true);
                    return field.get(bean);
                } catch (IllegalAccessException e) {
                    throw errorReporter.report(e, "FIELD_GET_UNKNOWN_ERROR",
                            "'" + field.getName() + "' field get unknown error");
                } finally {
                    field.setAccessible(false);
                }
            }
        };
    }

    private static Consumer<Object> getFieldConsumerAccessible(Field field, Object bean, ErrorReporter errorReporter) {
        return (newValue) -> {
            try {
                field.set(bean, newValue);
            } catch (Exception e) {
                throw errorReporter.report(e, "FIELD_SET_UNKNOWN_ERROR",
                        "'" + field.getName() + "' field set unknown error");
            }
        };
    }

    private static Consumer<Object> getFieldConsumerNotAccessible(Field field, Object bean, ErrorReporter errorReporter) {
        return (newValue) -> {
            synchronized (field) {
                try {
                    field.setAccessible(true);
                    field.set(bean, newValue);
                } catch (Exception e) {
                    throw errorReporter.report(e, "FIELD_SET_UNKNOWN_ERROR",
                            "'" + field.getName() + "' field set unknown error");
                } finally {
                    field.setAccessible(false);
                }
            }
        };
    }

    private static Supplier<Object> getMethodSupplierAccessible(Method method, Object bean, ErrorReporter errorReporter) {
        return () -> {
            try {
                return method.invoke(bean);
            } catch (Exception e) {
                throw unwrapMethodInvokeEx(errorReporter, method.getName(), e);
            }
        };
    }

    private static Supplier<Object> getMethodSupplierNotAccessible(Method method, Object bean, ErrorReporter errorReporter) {
        return () -> {
            synchronized (method) {
                try {
                    method.setAccessible(true);
                    return method.invoke(bean);
                } catch (Exception e) {
                    throw unwrapMethodInvokeEx(errorReporter, method.getName(), e);
                } finally {
                    method.setAccessible(false);
                }
            }
        };
    }

    private static Consumer<Object> getMethodConsumerAccessible(Method method, Object bean, ErrorReporter errorReporter) {
        return (newValue) -> {
            try {
                method.invoke(bean, newValue);
            } catch (Exception e) {
                throw unwrapMethodInvokeEx(errorReporter, method.getName(), e);
            }
        };
    }

    private static Consumer<Object> getMethodConsumerNotAccessible(Method method, Object bean, ErrorReporter errorReporter) {
        // TODO errorReporter 抛出方法的执行错误
        return (newValue) -> {
            synchronized (method) {
                try {
                    method.setAccessible(true);
                    method.invoke(bean, newValue);
                } catch (Exception e) {
                    throw unwrapMethodInvokeEx(errorReporter, method.getName(), e);
                } finally {
                    method.setAccessible(false);
                }
            }
        };
    }

    private static QLRuntimeException unwrapMethodInvokeEx(ErrorReporter errorReporter, String methodName, Exception ex) {
        if (ex instanceof IllegalArgumentException) {
            return errorReporter.reportFormat("METHOD_INVOKE_WITH_WRONG_ARGUMENT",
                    "'" + methodName + "' method invoke with wrong argument");
        } else if (ex instanceof InvocationTargetException) {
            return errorReporter.report(((InvocationTargetException) ex).getTargetException(),
                    "METHOD_INNER_EXCEPTION", "'" + methodName + "' method invoke");
        } else {
            return errorReporter.report(ex, "METHOD_INVOKE_UNKNOWN_ERROR",
                    "'" + methodName + "' method invoke with unknown error");
        }
    }

    /**
     * getMethod List
     * return null means notFound
     *
     * @param bean
     * @param name
     * @return
     */
    public static List<Method> getMethod(Object bean, String name, boolean isAllowAccessPrivate) {
        return getMethod(bean, name, null, isAllowAccessPrivate);
    }


    public static List<Method> getMethod(Object bean, String name, Object[] params, boolean isAllowAccessPrivate) {
        if (params == null) {
            return MethodHandler.Preferred.gatherMethodsRecursive(bean.getClass(), name, isAllowAccessPrivate);
        }
        return MethodHandler.Preferred.gatherMethodsRecursive(bean.getClass(), name, isAllowAccessPrivate, params);
    }


    /**
     * getClzMethod
     * return null means notFound
     *
     * @param clazz
     * @param name
     * @return
     */
    public static List<Method> getClzMethod(Class<?> clazz, String name) {
        return getClzMethod(clazz, name, null, false);
    }

    /**
     * getClzMethod
     * return null means notFound
     *
     * @param clazz
     * @param name
     * @return
     */
    public static List<Method> getClzMethod(Class<?> clazz, String name, boolean isAllowAccessPrivate) {
        return getClzMethod(clazz, name, null, isAllowAccessPrivate);
    }


    public static List<Method> getClzMethod(Class<?> clazz, String name, Object[] params, boolean isAllowAccessPrivate) {
        if (params == null) {
            return MethodHandler.Preferred.gatherMethodsRecursive(clazz, name, false, null, !isAllowAccessPrivate, true, null);
        }
        return MethodHandler.Preferred.gatherMethodsRecursive(clazz, name, true, params.length, !isAllowAccessPrivate, true, null);
    }
}
