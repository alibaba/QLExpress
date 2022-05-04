package com.alibaba.qlexpress4.runtime.data;

import com.alibaba.qlexpress4.runtime.Value;

/**
 * @Author TaoKan
 * @Date 2022/4/30 下午8:37
 */
public class DataArray implements Value {

    public DataArray(Object[] array){
        this.array = array;
    }

    private Object[] array;

    @Override
    public Object get() {
        return array.length;
    }
}
