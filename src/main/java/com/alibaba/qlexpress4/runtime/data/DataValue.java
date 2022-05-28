package com.alibaba.qlexpress4.runtime.data;

import com.alibaba.qlexpress4.runtime.Value;

/**
 * @Author TaoKan
 * @Date 2022/5/6 下午6:29
 */
public class DataValue implements Value {

    private Object value;

    public DataValue(Object value){
        this.value = value;
    }

    @Override
    public Object get() {
        return this.value;
    }
}
