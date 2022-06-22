package com.alibaba.qlexpress4.runtime.data;

import com.alibaba.qlexpress4.runtime.Value;

/**
 * @Author TaoKan
 * @Date 2022/5/6 下午6:29
 */
public class DataValue implements Value {

    private final Object value;
    private final Class<?> defineType;

    public DataValue(Object value) {
        this.value = value;
        this.defineType = Object.class;
    }

    public DataValue(Value value) {
        this.value = value.get();
        this.defineType = Object.class;
    }

    public DataValue(Object value, Class<?> defineType) {
        this.value = value;
        this.defineType = defineType;
    }

    @Override
    public Object get() {
        return this.value;
    }

    @Override
    public Class<?> getDefineType() {
        return defineType;
    }
}
