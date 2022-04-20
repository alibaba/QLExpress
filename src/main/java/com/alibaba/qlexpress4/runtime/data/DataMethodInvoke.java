package com.alibaba.qlexpress4.runtime.data;

import com.alibaba.qlexpress4.runtime.LeftValue;

/**
 * @Author TaoKan
 * @Date 2022/4/19 下午10:05
 */
public class DataMethodInvoke implements LeftValue {
    private Object methodValue;

    @Override
    public void set(Object newValue) {
        this.methodValue = newValue;
    }

    @Override
    public Object get() {
        return this.methodValue;
    }
}
