package com.alibaba.qlexpress4.runtime.data;

import com.alibaba.qlexpress4.runtime.LeftValue;

/**
 * Author: DQinYuan
 */
public class AssignableDataValue implements LeftValue {

    private Object value;

    private final Class<?> defineType;

    public AssignableDataValue(Object value) {
        this.value = value;
        this.defineType = Object.class;
    }

    public AssignableDataValue(Object value, Class<?> defineType) {
        this.value = value;
        this.defineType = defineType;
    }

    @Override
    public void set(Object newValue) {
        this.value = newValue;
    }

    @Override
    public Object get() {
        return value;
    }

    @Override
    public Class<?> getDefineType() {
        return defineType;
    }
}
