package com.alibaba.qlexpress4.runtime;

import com.alibaba.qlexpress4.runtime.data.DataValue;

/**
 * Author: DQinYuan
 */
public interface Value {
    Value NULL_VALUE = new DataValue(null);

    Object get();

    Class<?> getDefineType();

    default Class<?> getType() {
        Object value = get();
        if (value == null) {
            return null;
        }

        return value.getClass();
    }

    default String getTypeName() {
        Object value = get();
        if (value == null) {
            return null;
        }

        return value.getClass().getName();
    }
}
