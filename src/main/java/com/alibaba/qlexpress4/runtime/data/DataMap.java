package com.alibaba.qlexpress4.runtime.data;

import com.alibaba.qlexpress4.runtime.LeftValue;
import com.alibaba.qlexpress4.runtime.data.convert.InstanceConversion;

import java.util.Map;

/**
 * @Author TaoKan
 * @Date 2022/4/30 下午7:42
 */
public class DataMap implements LeftValue {

    private final String fieldName;
    private final Map map;

    public DataMap(Map map, String fieldName) {
        this.map = map;
        this.fieldName = fieldName;
    }

    @Override
    public void setInner(Object newValue) {
        this.map.put(this.fieldName, newValue);
    }

    @Override
    public Object get() {
        return this.map.get(this.fieldName);
    }

    @Override
    public Class<?> getDefinedType() {
        return Map.class;
    }

}
