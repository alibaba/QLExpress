package com.alibaba.qlexpress4.runtime;

/**
 * Author: DQinYuan
 */
public class MetaClass {

    private final Class<?> clz;

    public MetaClass(Class<?> clz) {
        this.clz = clz;
    }

    public Class<?> getClz() {
        return clz;
    }
}
