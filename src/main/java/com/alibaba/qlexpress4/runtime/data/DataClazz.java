package com.alibaba.qlexpress4.runtime.data;

import com.alibaba.qlexpress4.runtime.Value;

/**
 * @Author TaoKan
 * @Date 2022/4/30 下午8:43
 */
public class DataClazz implements Value {

    public DataClazz(Class<?> clazz){
        this.clazz = clazz;
    }

    private Class<?> clazz;

    @Override
    public Object get() {
        return clazz;
    }
}
