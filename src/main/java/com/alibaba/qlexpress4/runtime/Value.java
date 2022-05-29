package com.alibaba.qlexpress4.runtime;

/**
 * Author: DQinYuan
 */
public interface Value {
    Value NULL_VALUE = () -> null;

    Object get();

    default Class getType() {
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
