package com.alibaba.qlexpress4.runtime.data;

import com.alibaba.qlexpress4.runtime.LeftValue;
import com.alibaba.qlexpress4.runtime.QLambda;

/**
 * @Author TaoKan
 * @Date 2022/4/19 下午10:03
 */
public class DataMethod implements LeftValue {
    private QLambda qLambda;

    @Override
    public void set(Object newValue) {
        this.qLambda = (QLambda) newValue;
    }

    @Override
    public Object get() {
        return this.qLambda;
    }
}
