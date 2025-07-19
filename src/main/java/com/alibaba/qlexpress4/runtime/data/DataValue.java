package com.alibaba.qlexpress4.runtime.data;

import com.alibaba.qlexpress4.runtime.Value;

/**
 * Author: TaoKan
 */
public class DataValue implements Value {
    
    private final Object value;
    
    public DataValue(Object value) {
        this.value = value;
    }
    
    public DataValue(Value value) {
        this.value = value.get();
    }
    
    @Override
    public Object get() {
        return this.value;
    }
}
