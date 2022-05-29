package com.alibaba.qlexpress4.runtime.data;

import com.alibaba.qlexpress4.runtime.LeftValue;

import java.util.Map;

/**
 * @Author TaoKan
 * @Date 2022/4/30 下午7:42
 */
public class DataMap implements LeftValue {

    private String fieldName;
    private Map map;

    public DataMap(Map map, String fieldName) {
        this.map = map;
        this.fieldName = fieldName;
    }

    @Override
    public void set(Object newValue) {
        this.map.put(this.fieldName, newValue);
    }

    @Override
    public Object get() {
        return this.map.get(this.fieldName);
    }
}
