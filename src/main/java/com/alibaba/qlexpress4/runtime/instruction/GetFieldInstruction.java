package com.alibaba.qlexpress4.runtime.instruction;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.runtime.QResult;
import com.alibaba.qlexpress4.member.FieldHandler;
import com.alibaba.qlexpress4.member.MethodHandler;
import com.alibaba.qlexpress4.runtime.LeftValue;
import com.alibaba.qlexpress4.runtime.QRuntime;
import com.alibaba.qlexpress4.runtime.Value;
import com.alibaba.qlexpress4.runtime.data.DataField;
import com.alibaba.qlexpress4.runtime.data.DataMap;
import com.alibaba.qlexpress4.runtime.data.DataValue;
import com.alibaba.qlexpress4.runtime.data.cache.CacheFieldValue;
import com.alibaba.qlexpress4.utils.BasicUtil;
import com.alibaba.qlexpress4.utils.CacheUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @Operation: get specified field of object on the top of stack
 * @Input: 1
 * @Output: 1
 * <p>
 * Author: DQinYuan
 */
public class GetFieldInstruction extends QLInstruction {

    private final String fieldName;

    public GetFieldInstruction(ErrorReporter errorReporter, String fieldName) {
        super(errorReporter);
        this.fieldName = fieldName;
    }

    @Override
    public QResult execute(QRuntime qRuntime, QLOptions qlOptions) {
        Object bean = qRuntime.pop().get();
        if (bean == null) {
            throw errorReporter.report("GET_FIELD_VALUE_ERROR", "can not get field from null");
        }
        if (bean.getClass().isArray() && BasicUtil.LENGTH.equals(this.fieldName)) {
            Value dataArray = new DataValue(((Object[]) bean).length);
            qRuntime.push(dataArray);
        } else if (bean instanceof Class) {
            if (BasicUtil.CLASS.equals(this.fieldName)) {
                Value dataClazz = new DataValue(bean);
                qRuntime.push(dataClazz);
            } else {
                getCacheFieldValue(qlOptions, (Class<?>) bean, bean, qRuntime);
            }
        } else if (bean instanceof Map) {
            LeftValue dataMap = new DataMap((Map<?, ?>) bean, this.fieldName);
            qRuntime.push(dataMap);
        } else {
            getCacheFieldValue(qlOptions, bean.getClass(), bean, qRuntime);
        }
        return QResult.CONTINUE_RESULT;
    }

    @Override
    public int stackInput() {
        return 1;
    }

    @Override
    public int stackOutput() {
        return 1;
    }

    /**
     * getValue from cache, if value not exist getInstance
     *
     * @param qlOptions
     * @param clazz
     * @param bean
     * @param qRuntime
     */
    private void getCacheFieldValue(QLOptions qlOptions, Class<?> clazz, Object bean, QRuntime qRuntime) {
        CacheFieldValue cacheElement = CacheUtil.getFieldCacheElement(clazz, this.fieldName);
        if (cacheElement == null) {
            Method getMethod = MethodHandler.getGetter(clazz, this.fieldName);
            Method setMethod = MethodHandler.getSetter(clazz, this.fieldName);
            Field field = FieldHandler.Preferred.gatherFieldRecursive(clazz, this.fieldName);
            Value dataField = getDataField(getMethod,setMethod,field,bean,qlOptions.enableAllowAccessPrivateMethod());
            qRuntime.push(dataField);
            CacheFieldValue cacheFieldValue = new CacheFieldValue(getMethod, setMethod, field);
            CacheUtil.setFieldCacheElement(clazz, this.fieldName, cacheFieldValue);
        } else {
            CacheFieldValue cacheFieldValue = cacheElement;
            Value dataField = getDataField(cacheFieldValue.getGetMethod(),cacheFieldValue.getSetMethod(),
                    cacheFieldValue.getField(),bean,qlOptions.enableAllowAccessPrivateMethod());
            qRuntime.push(dataField);
            return;
        }
    }


    private Value getDataField(Method getMethod, Method setMethod, Field field, Object bean, boolean enableAllowAccessPrivateMethod) {
        Supplier<Object> getterOp = operator(
                getMethodSupplierAccessible(getMethod, bean),
                getMethodSupplierNotAccessible(getMethod, bean),
                getFieldSupplierAccessible(field, bean),
                getFieldSupplierNotAccessible(field, bean),
                getMethod, field, enableAllowAccessPrivateMethod);

        Consumer<Object> setterOp = operator(
                getMethodConsumerAccessible(setMethod, bean),
                getMethodConsumerNotAccessible(setMethod, bean),
                getFieldConsumerAccessible(field, bean),
                getFieldConsumerNotAccessible(field, bean),
                setMethod, field, enableAllowAccessPrivateMethod);

        if (getterOp == null) {
            throw errorReporter.report("GET_FIELD_VALUE_ERROR", "can not get field accessible");
        }
        if (setterOp == null) {
            return new DataValue(getterOp);
        }
        return new DataField(getterOp, setterOp);
    }

    private <T> T process(T functional){
        return functional;
    }

    private <T> T operator(T methodAccess, T methodNotAccess, T fieldAccess, T fieldNotAccess, Method method, Field field,
                           boolean enableAllowAccessPrivateMethod){
        if(method != null){
            if(BasicUtil.isPublic(method)){
                return process(methodAccess);
            }
            if(enableAllowAccessPrivateMethod){
                return process(methodNotAccess);
            }
        }
        if (field != null) {
            if(BasicUtil.isPublic(field)){
                return process(fieldAccess);
            }
            if(enableAllowAccessPrivateMethod) {
                return process(fieldNotAccess);
            }
        }
        return null;
    }


    private Supplier<Object> getFieldSupplierAccessible(Field field, Object bean) {
        return () -> {
            try {
                return field.get(bean);
            } catch (Exception e) {
                return null;
            }
        };
    }

    private Supplier<Object> getFieldSupplierNotAccessible(Field field, Object bean) {
        return () -> {
            try {
                synchronized (field) {
                    try {
                        field.setAccessible(true);
                        return field.get(bean);
                    } finally {
                        field.setAccessible(false);
                    }
                }
            } catch (Exception e) {
                return null;
            }
        };
    }

    private Consumer<Object> getFieldConsumerAccessible(Field field, Object bean) {
        return (newValue) -> {
            try {
                field.set(bean, newValue);
            } catch (Exception e) {
            }
        };
    }

    private Consumer<Object> getFieldConsumerNotAccessible(Field field, Object bean) {
        return (newValue) -> {
            try {
                synchronized (field) {
                    try {
                        field.setAccessible(true);
                        field.set(bean, newValue);
                    } finally {
                        field.setAccessible(false);
                    }
                }
            } catch (Exception e) {
            }
        };
    }

    private Supplier<Object> getMethodSupplierAccessible(Method method, Object bean) {
        return () -> {
            try {
                return method.invoke(bean);
            } catch (Exception e) {
                return null;
            }
        };
    }

    private Supplier<Object> getMethodSupplierNotAccessible(Method method, Object bean) {
        return () -> {
            try {
                synchronized (method) {
                    try {
                        method.setAccessible(true);
                        return method.invoke(bean);
                    } finally {
                        method.setAccessible(false);
                    }
                }
            } catch (Exception e) {
                return null;
            }
        };
    }

    private Consumer<Object> getMethodConsumerAccessible(Method method, Object bean) {
        return (newValue) -> {
            try {
                method.invoke(bean, newValue);
            } catch (Exception e) {
            }
        };
    }

    private Consumer<Object> getMethodConsumerNotAccessible(Method method, Object bean) {
        return (newValue) -> {
            try {
                synchronized (method) {
                    try {
                        method.setAccessible(true);
                        method.invoke(bean, newValue);
                    } finally {
                        method.setAccessible(false);
                    }
                }
            } catch (Exception e) {
            }
        };
    }


}
