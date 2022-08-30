package com.alibaba.qlexpress4.runtime.instruction;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.cache.QLFieldCache;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.runtime.*;
import com.alibaba.qlexpress4.member.FieldHandler;
import com.alibaba.qlexpress4.member.MethodHandler;
import com.alibaba.qlexpress4.runtime.data.FieldValue;
import com.alibaba.qlexpress4.runtime.data.DataValue;
import com.alibaba.qlexpress4.runtime.data.MapItemValue;
import com.alibaba.qlexpress4.runtime.data.cache.CacheFieldValue;
import com.alibaba.qlexpress4.utils.BasicUtil;
import com.alibaba.qlexpress4.utils.CacheUtil;
import com.alibaba.qlexpress4.utils.PrintlnUtils;

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

    @SuppressWarnings("unchecked")
    @Override
    public QResult execute(QContext qContext, QLOptions qlOptions) {
        Object bean = qContext.pop().get();
        if (bean == null) {
            if (qlOptions.isAvoidNullPointer()) {
                qContext.push(DataValue.NULL_VALUE);
                return QResult.CONTINUE_RESULT;
            }
            throw errorReporter.report("GET_FIELD_FROM_NULL", "can not get field from null");
        }
        if (bean.getClass().isArray() && BasicUtil.LENGTH.equals(this.fieldName)) {
            Value dataArray = new DataValue(((Object[]) bean).length);
            qContext.push(dataArray);
        } else if (bean instanceof MetaClass) {
            MetaClass metaClass = (MetaClass) bean;
            if (BasicUtil.CLASS.equals(this.fieldName)) {
                Value dataClazz = new DataValue(metaClass.getClz());
                qContext.push(dataClazz);
            } else if(metaClass.getClz().isEnum()){
                Object[] enums = metaClass.getClz().getEnumConstants();
                for(Object enumObj: enums){
                    if(this.fieldName.equals(enumObj.toString())){
                        Value dataEnum = new DataValue(enumObj);
                        qContext.push(dataEnum);
                        return QResult.CONTINUE_RESULT;
                    }
                }
                throw errorReporter.report("ENUM_NOT_EXIST", "enum not exist");
            } else {
                getCacheFieldValue(qlOptions, metaClass.getClz(), bean, qContext);
            }
        } else if (bean instanceof Map) {
            LeftValue dataMap = new MapItemValue((Map<?, ?>) bean, this.fieldName);
            qContext.push(dataMap);
        } else {
            getCacheFieldValue(qlOptions, bean.getClass(), bean, qContext);
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

    @Override
    public void println(int depth, Consumer<String> debug) {
        PrintlnUtils.printlnByCurDepth(depth, "GetField " + fieldName, debug);
    }

    /**
     * getValue from cache, if value not exist getInstance
     *
     * @param qlOptions
     * @param clazz
     * @param bean
     * @param qContext
     */
    private void getCacheFieldValue(QLOptions qlOptions, Class<?> clazz, Object bean, QContext qContext) {
        QLFieldCache qlFieldCache = qContext.getQLCaches().getQlFieldCache();
        CacheFieldValue cacheElement = CacheUtil.getFieldCacheElement(qlFieldCache, clazz, this.fieldName);
        if (cacheElement == null) {
            Method getMethod = MethodHandler.getGetter(clazz, this.fieldName);
            Method setMethod = MethodHandler.getSetter(clazz, this.fieldName);
            Field field = FieldHandler.Preferred.gatherFieldRecursive(clazz, this.fieldName);
            Value dataField = getDataField(getMethod,setMethod,field,bean,qlOptions.enableAllowAccessPrivateMethod());
            qContext.push(dataField);
            CacheFieldValue cacheFieldValue = new CacheFieldValue(getMethod, setMethod, field);
            CacheUtil.setFieldCacheElement(qlFieldCache, clazz, this.fieldName, cacheFieldValue);
        } else {
            Value dataField = getDataField(cacheElement.getGetMethod(), cacheElement.getSetMethod(),
                    cacheElement.getField(),bean,qlOptions.enableAllowAccessPrivateMethod());
            qContext.push(dataField);
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
            return new DataValue(getterOp.get());
        }
        return new FieldValue(getterOp, setterOp);
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
