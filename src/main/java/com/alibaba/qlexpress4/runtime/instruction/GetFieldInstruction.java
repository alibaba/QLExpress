package com.alibaba.qlexpress4.runtime.instruction;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.exception.ErrorReporter;
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
    public void execute(QRuntime qRuntime, QLOptions qlOptions) {
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
            DataField dataField = getDataField(getMethod,setMethod,field,bean,qlOptions.enableAllowAccessPrivateMethod());
            qRuntime.push(dataField);
            CacheFieldValue cacheFieldValue = new CacheFieldValue(getMethod, setMethod, field);
            CacheUtil.setFieldCacheElement(clazz, this.fieldName, cacheFieldValue);
        } else {
            CacheFieldValue cacheFieldValue = cacheElement;
            DataField dataField = getDataField(cacheFieldValue.getGetMethod(),cacheFieldValue.getSetMethod(),
                    cacheFieldValue.getField(),bean,qlOptions.enableAllowAccessPrivateMethod());
            qRuntime.push(dataField);
            return;
        }
    }


    private DataField getDataField(Method getMethod, Method setMethod, Field field, Object bean, boolean enableAllowAccessPrivateMethod){
        Supplier<Object> supplier = null;
        Consumer<Object> consumer = null;
        if(getMethod != null){
            if(BasicUtil.isPublic(getMethod)){
                supplier = getMethodSupplierAccessible(getMethod, bean);
            }
            if(supplier == null && enableAllowAccessPrivateMethod){
                supplier = getMethodSupplierNotAccessible(getMethod, bean);
            }
        }
        if(setMethod != null){
            if(BasicUtil.isPublic(setMethod)){
                consumer = getMethodConsumerAccessible(setMethod, bean);
            }
            if(consumer == null && enableAllowAccessPrivateMethod){
                consumer = getMethodConsumerNotAccessible(setMethod, bean);
            }
        }
        if(supplier != null && consumer != null){
            return new DataField(supplier, consumer);
        }

        if(field != null){
            if(BasicUtil.isPublic(field)){
                if(supplier == null){
                    supplier = getFieldSupplierAccessible(field, bean);
                }
                if(consumer == null){
                    consumer = getFieldConsumerAccessible(field, bean);
                }
            }else {
                if(!enableAllowAccessPrivateMethod){
                    //either of getMethod or setMethod is null or both null && field cannot access
                    throw errorReporter.report("GET_FIELD_VALUE_ERROR", "can not get field accessible");
                }
                if(supplier == null){
                    supplier = getFieldSupplierNotAccessible(field, bean);
                }
                if(consumer == null){
                    consumer = getFieldConsumerNotAccessible(field, bean);
                }
            }
        }else {
            //either of getMethod or setMethod is null or both null && field is null
            throw errorReporter.report("GET_FIELD_VALUE_ERROR", "can not get field");
        }

        if(supplier == null || consumer == null){
            throw errorReporter.report("GET_FIELD_VALUE_ERROR", "can not get field");
        }else {
            return new DataField(supplier, consumer);
        }
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
