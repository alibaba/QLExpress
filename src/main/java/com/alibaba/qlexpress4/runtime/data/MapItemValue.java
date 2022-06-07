package com.alibaba.qlexpress4.runtime.data;

import com.alibaba.qlexpress4.runtime.LeftValue;

import java.util.Map;

/**
 * Author: DQinYuan
 */
public class MapItemValue implements LeftValue {

    private final Map<? super Object, ? super Object> map;

    private final Object key;

    public MapItemValue(Map<? super Object, ? super Object> map, Object key) {
        this.map = map;
        this.key = key;
    }

    @Override
    public void set(Object newValue) {
        map.put(key, newValue);
    }

    @Override
    public Object get() {
        return map.get(key);
    }

    @Override
    public Class<?> getDefineType() {
        return Object.class;
    }
}
