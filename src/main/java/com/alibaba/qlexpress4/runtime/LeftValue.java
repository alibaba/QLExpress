package com.alibaba.qlexpress4.runtime;

import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.runtime.data.convert.ObjTypeConvertor;

/**
 * assignable value
 * <p>
 * Author: DQinYuan
 */
public interface LeftValue extends Value {

    Class<?> getDefinedType();

    @Override
    default Class<?> getType() {
        return getDefinedType();
    }

    default void set(Object newValue, ErrorReporter errorReporter) {
        Class<?> defineType = getDefinedType();
        ObjTypeConvertor.QConverted result = ObjTypeConvertor.cast(newValue, defineType);
        if (!result.isConvertible()) {
            throw errorReporter.report("TRANS_OBJECT_ERROR", "can not trans to:" + defineType.getName());
        }
        setInner(result.getConverted());
    }

    void setInner(Object newValue);

    /**
     * @return Nullable
     */
    String getSymbolName();
}
