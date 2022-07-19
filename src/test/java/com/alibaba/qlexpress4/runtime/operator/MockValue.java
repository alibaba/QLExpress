package com.alibaba.qlexpress4.runtime.operator;

import com.alibaba.qlexpress4.runtime.Value;

public class MockValue implements Value {
    protected Object value;
    private Class<?> declaredClass;

    public MockValue(Object value, Class<?> declaredClass) {
        this.value = value;
        this.declaredClass = declaredClass;
    }

    @Override
    public Object get() {
        return value;
    }

    @Override
    public Class<?> getDefinedType() {
        return declaredClass;
    }
}
