package com.alibaba.qlexpress4.runtime.data;

import com.alibaba.qlexpress4.runtime.LeftValue;

/**
 * Author: DQinYuan
 */
public class AssignableDataValue implements LeftValue {

    private String symbolName;

    private Object value;

    private final Class<?> defineType;

    public AssignableDataValue(String symbolName, Object value) {
        this.symbolName = symbolName;
        this.value = value;
        this.defineType = Object.class;
    }

    public AssignableDataValue(String symbolName, Object value, Class<?> defineType) {
        this.symbolName = symbolName;
        this.value = value;
        this.defineType = defineType;
    }

    @Override
    public void setInner(Object newValue) {
        this.value = newValue;
    }

    @Override
    public Object get() {
        return value;
    }

    @Override
    public Class<?> getDefinedType() {
        return defineType;
    }

    @Override
    public String getSymbolName() {
        return symbolName;
    }
}
