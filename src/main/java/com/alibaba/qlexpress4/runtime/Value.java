package com.alibaba.qlexpress4.runtime;

import com.alibaba.qlexpress4.runtime.data.ReadonlyDataValue;

/**
 * Author: DQinYuan
 */
public interface Value {
    Value NULL_VALUE = new ReadonlyDataValue(null);

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
