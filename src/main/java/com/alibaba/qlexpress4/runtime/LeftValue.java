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
            throw errorReporter.reportFormat("INCOMPATIBLE_TYPE_FOR_ASSIGNMENT",
                    "variable declared type %s, assigned with incompatible value type %s",
                    newValue == null? "null": newValue.getClass().getName(), defineType.getName());
        }
        setInner(result.getConverted());
    }

    void setInner(Object newValue);

    /**
     * @return Nullable
     */
    String getSymbolName();
}
