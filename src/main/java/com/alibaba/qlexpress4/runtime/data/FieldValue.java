package com.alibaba.qlexpress4.runtime.data;

import com.alibaba.qlexpress4.runtime.LeftValue;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Author: TaoKan
 */
public class FieldValue implements LeftValue {
    private final Supplier<Object> getOp;
    private final Consumer<Object> setOp;
    private final Class<?> defineType;


    public FieldValue(Supplier<Object> getOp, Consumer<Object> setOp, Class<?> defineType) {
        this.getOp = getOp;
        this.setOp = setOp;
        this.defineType = defineType;
    }


    @Override
    public void setInner(Object newValue) {
        setOp.accept(newValue);
    }

    @Override
    public String getSymbolName() {
        return null;
    }

    @Override
    public Object get() {
        return getOp.get();
    }

    @Override
    public Class<?> getDefinedType() {
        return this.defineType;
    }
}
