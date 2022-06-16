package com.alibaba.qlexpress4.runtime.data;

import com.alibaba.qlexpress4.runtime.LeftValue;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @Author TaoKan
 * @Date 2022/6/16 上午10:17
 */
public class DataField implements LeftValue {
    private final Supplier<Object> getOp;
    private final Consumer<Object> setOp;
    private Class<?> defineType;


    public DataField(Supplier<Object> getOp, Consumer<Object> setOp) {
        this.getOp = getOp;
        this.setOp = setOp;
        this.defineType = Object.class;
    }


    @Override
    public void set(Object newValue) {
        setOp.accept(newValue);
    }

    @Override
    public Object get() {
        return getOp.get();
    }

    @Override
    public Class<?> getDefineType() {
        return this.defineType;
    }
}
