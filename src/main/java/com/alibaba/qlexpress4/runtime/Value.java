package com.alibaba.qlexpress4.runtime;

/**
 * Author: DQinYuan
 */
public interface Value {
    Value NULL_VALUE = () -> null;

    Object get();

    default String getValueType() {
        Object value = get();
        if (value == null) {
            return null;
        }
        return value.getClass().getName();
    }
}
