package com.alibaba.qlexpress4.runtime;

import com.alibaba.qlexpress4.runtime.operator.MockValue;

public class MockLeftValue extends MockValue implements LeftValue {
    public MockLeftValue(Object value, Class<?> declaredClass) {
        super(value, declaredClass);
    }

    @Override
    public void set(Object newValue) {
        this.value = newValue;
    }
}
