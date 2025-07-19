package com.alibaba.qlexpress4.runtime.data;

import com.alibaba.qlexpress4.runtime.LeftValue;

import java.lang.reflect.Array;

/**
 * Author: DQinYuan
 */
public class ArrayItemValue implements LeftValue {
    
    private final Object array;
    
    private final int index;
    
    public ArrayItemValue(Object array, int index) {
        this.array = array;
        this.index = index;
    }
    
    @Override
    public void setInner(Object newValue) {
        Array.set(array, index, newValue);
    }
    
    @Override
    public String getSymbolName() {
        return null;
    }
    
    @Override
    public Object get() {
        return Array.get(array, index);
    }
    
    @Override
    public Class<?> getDefinedType() {
        return array.getClass().getComponentType();
    }
}
