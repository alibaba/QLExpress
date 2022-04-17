package com.alibaba.qlexpress4.runtime.data;

import com.alibaba.qlexpress4.runtime.LeftValue;

/**
 * @Author TaoKan
 * @Date 2022/4/18 上午7:47
 */
public class DataField implements LeftValue {
    private Object fieldValue;

    @Override
    public void set(Object newValue) {
        this.fieldValue = newValue;
    }

    @Override
    public Object get() {
        return this.fieldValue;
    }
}
