package com.alibaba.qlexpress4.runtime;

import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.exception.QLErrorCodes;
import com.alibaba.qlexpress4.runtime.data.convert.ObjTypeConvertor;

/**
 * assignable value
 * <p>
 * Author: DQinYuan
 */
public interface LeftValue extends Value {
    
    Class<?> getDefinedType();
    
    default void set(Object newValue, ErrorReporter errorReporter) {
        Class<?> defineType = getDefinedType();
        ObjTypeConvertor.QConverted result = ObjTypeConvertor.cast(newValue, defineType);
        if (!result.isConvertible()) {
            throw errorReporter.reportFormat(QLErrorCodes.INCOMPATIBLE_ASSIGNMENT_TYPE.name(),
                QLErrorCodes.INCOMPATIBLE_ASSIGNMENT_TYPE.getErrorMsg(),
                newValue == null ? "null" : newValue.getClass().getName(),
                defineType.getName());
        }
        setInner(result.getConverted());
    }
    
    void setInner(Object newValue);
    
    /**
     * @return Nullable
     */
    String getSymbolName();
}
