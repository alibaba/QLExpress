package com.alibaba.qlexpress4.runtime.data;

import com.alibaba.qlexpress4.runtime.LeftValue;

import java.lang.reflect.Array;

/**
 * Author: DQinYuan
 */
public class ArrayValue implements LeftValue {

    private final Object array;

    private final int index;

    public ArrayValue(Object array, int index) {
        this.array = array;
        this.index = index;
    }

    @Override
    public void set(Object newValue) {
        Array.set(array, index, newValue);
    }

    @Override
    public Object get() {
        return Array.get(array, index);
    }

    @Override
    public Class<?> getDefineType() {
        return array.getClass().getComponentType();
    }
}
