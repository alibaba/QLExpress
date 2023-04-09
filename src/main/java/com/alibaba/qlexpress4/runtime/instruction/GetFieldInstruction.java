package com.alibaba.qlexpress4.runtime.instruction;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.cache.QLFieldCache;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.member.FieldHandler;
import com.alibaba.qlexpress4.member.IField;
import com.alibaba.qlexpress4.member.IMethod;
import com.alibaba.qlexpress4.member.MethodHandler;
import com.alibaba.qlexpress4.runtime.*;
import com.alibaba.qlexpress4.runtime.data.DataValue;
import com.alibaba.qlexpress4.runtime.data.FieldValue;
import com.alibaba.qlexpress4.runtime.data.MapItemValue;
import com.alibaba.qlexpress4.runtime.data.cache.CacheFieldValue;
import com.alibaba.qlexpress4.runtime.util.OptionUtils;
import com.alibaba.qlexpress4.utils.BasicUtil;
import com.alibaba.qlexpress4.utils.CacheUtil;
import com.alibaba.qlexpress4.utils.PrintlnUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
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
                return QResult.NEXT_INSTRUCTION;
            }
            throw errorReporter.report(new NullPointerException(),
                    "GET_FIELD_FROM_NULL", "can not get field from null");
        }
        if (bean.getClass().isArray() && BasicUtil.LENGTH.equals(this.fieldName)) {
            Value dataArray = new DataValue(((Object[]) bean).length);
            qContext.push(dataArray);
        } else if (bean instanceof List) {
            qContext.push(new DataValue(((List<?>) bean).size()));
        } else if (bean instanceof MetaClass) {
            MetaClass metaClass = (MetaClass) bean;
            if (BasicUtil.CLASS.equals(this.fieldName)) {
                Value dataClazz = new DataValue(metaClass.getClz());
                qContext.push(dataClazz);
            } else if (metaClass.getClz().isEnum()) {
                Object[] enums = metaClass.getClz().getEnumConstants();
                for (Object enumObj : enums) {
                    if (this.fieldName.equals(enumObj.toString())) {
                        Value dataEnum = new DataValue(enumObj);
                        qContext.push(dataEnum);
                        return QResult.NEXT_INSTRUCTION;
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
        return QResult.NEXT_INSTRUCTION;
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
            Value dataField = getDataField(getMethod, setMethod, field, bean, clazz, qlOptions);
            qContext.push(dataField);
            CacheFieldValue cacheFieldValue = new CacheFieldValue(getMethod, setMethod, field);
            CacheUtil.setFieldCacheElement(qlFieldCache, clazz, this.fieldName, cacheFieldValue);
        } else {
            Value dataField = getDataField(cacheElement.getGetMethod(), cacheElement.getSetMethod(),
                    cacheElement.getField(), bean, clazz, qlOptions);
            qContext.push(dataField);
        }
    }


    private Value getDataField(Method getMethod, Method setMethod, Field field, Object bean, Class<?> clazz, QLOptions qlOptions) {
        Supplier<Object> getterOp = operator(
                getMethodSupplierAccessible(OptionUtils.getMethodFromQLOption(
                        qlOptions, clazz, getMethod.getName(), getMethod), bean),
                getMethodSupplierNotAccessible(OptionUtils.getMethodFromQLOption(
                        qlOptions, clazz, getMethod.getName(), getMethod), bean),
                getFieldSupplierAccessible(OptionUtils.getFieldFromQLOption(
                        qlOptions, clazz, field.getName(), field), bean),
                getFieldSupplierNotAccessible(OptionUtils.getFieldFromQLOption(
                        qlOptions, clazz, field.getName(), field), bean),
                getMethod, field, qlOptions.enableAllowAccessPrivateMethod());

        Consumer<Object> setterOp = operator(
                getMethodConsumerAccessible(OptionUtils.getMethodFromQLOption(
                        qlOptions, clazz, getMethod.getName(), setMethod), bean),
                getMethodConsumerNotAccessible(OptionUtils.getMethodFromQLOption(
                        qlOptions, clazz, getMethod.getName(), setMethod), bean),
                getFieldConsumerAccessible(OptionUtils.getFieldFromQLOption(
                        qlOptions, clazz, field.getName(), field), bean),
                getFieldConsumerNotAccessible(OptionUtils.getFieldFromQLOption(
                        qlOptions, clazz, field.getName(), field), bean),
                setMethod, field, qlOptions.enableAllowAccessPrivateMethod());

        if (getterOp == null) {
            throw errorReporter.report("GET_FIELD_VALUE_CAN_NOT_ACCESS", "can not get field accessible");
        }
        if (setterOp == null) {
            return new DataValue(getterOp.get());
        }
        return new FieldValue(getterOp, setterOp);
    }

    private <T> T process(T functional) {
        return functional;
    }

    private <T> T operator(T methodAccess, T methodNotAccess, T fieldAccess, T fieldNotAccess, Method method, Field field,
                           boolean enableAllowAccessPrivateMethod) {
        if (method != null) {
            if (BasicUtil.isPublic(method)) {
                return process(methodAccess);
            }
            if (enableAllowAccessPrivateMethod) {
                return process(methodNotAccess);
            }
        }
        if (field != null) {
            if (BasicUtil.isPublic(field)) {
                return process(fieldAccess);
            }
            if (enableAllowAccessPrivateMethod) {
                return process(fieldNotAccess);
            }
        }
        return null;
    }


    private Supplier<Object> getFieldSupplierAccessible(IField field, Object bean) {
        return () -> {
            try {
                return field.get(bean);
            } catch (Exception e) {
                return null;
            }
        };
    }

    private Supplier<Object> getFieldSupplierNotAccessible(IField field, Object bean) {
        return () -> {
            try {
                synchronized (field) {
                    try {
                        field.seVisitWithOutPermission(true);
                        return field.get(bean);
                    } finally {
                        field.seVisitWithOutPermission(false);
                    }
                }
            } catch (Exception e) {
                return null;
            }
        };
    }

    private Consumer<Object> getFieldConsumerAccessible(IField field, Object bean) {
        return (newValue) -> {
            try {
                field.set(bean, newValue);
            } catch (Exception e) {
            }
        };
    }

    private Consumer<Object> getFieldConsumerNotAccessible(IField field, Object bean) {
        return (newValue) -> {
            try {
                synchronized (field) {
                    try {
                        field.seVisitWithOutPermission(true);
                        field.set(bean, newValue);
                    } finally {
                        field.seVisitWithOutPermission(false);
                    }
                }
            } catch (Exception e) {
            }
        };
    }

    private Supplier<Object> getMethodSupplierAccessible(IMethod method, Object bean) {
        return () -> {
            try {
                return method.invoke(bean);
            } catch (Exception e) {
                return null;
            }
        };
    }

    private Supplier<Object> getMethodSupplierNotAccessible(IMethod method, Object bean) {
        return () -> {
            try {
                synchronized (method) {
                    try {
                        method.seVisitWithOutPermission(true);
                        return method.invoke(bean);
                    } finally {
                        method.seVisitWithOutPermission(false);
                    }
                }
            } catch (Exception e) {
                return null;
            }
        };
    }

    private Consumer<Object> getMethodConsumerAccessible(IMethod method, Object bean) {
        return (newValue) -> {
            try {
                method.invoke(bean, newValue);
            } catch (Exception e) {
            }
        };
    }

    private Consumer<Object> getMethodConsumerNotAccessible(IMethod method, Object bean) {
        return (newValue) -> {
            try {
                synchronized (method) {
                    try {
                        method.seVisitWithOutPermission(true);
                        method.invoke(bean, newValue);
                    } finally {
                        method.seVisitWithOutPermission(false);
                    }
                }
            } catch (Exception e) {
            }
        };
    }


}
