package com.alibaba.qlexpress4.runtime.data;

import com.alibaba.qlexpress4.runtime.LeftValue;

import java.util.List;

/**
 * Author: DQinYuan
 */
public class ListItemValue implements LeftValue {

    private final List<? super Object> list;

    private final int index;

    public ListItemValue(List<? super Object> list, int index) {
        this.list = list;
        this.index = index;
    }

    @Override
    public void set(Object newValue) {
        list.set(index, newValue);
    }

    @Override
    public Object get() {
        return list.get(index);
    }

    @Override
    public Class<?> getDefinedType() {
        return Object.class;
    }
}
