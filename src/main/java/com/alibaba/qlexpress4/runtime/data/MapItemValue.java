package com.alibaba.qlexpress4.runtime.data;

import com.alibaba.qlexpress4.runtime.LeftValue;

import java.util.Map;

/**
 * Author: DQinYuan
 */
@SuppressWarnings("all")
public class MapItemValue implements LeftValue {
    
    private final String symbolName;
    
    private final Map map;
    
    private final Object key;
    
    public MapItemValue(Map map, Object key) {
        this.symbolName = null;
        this.map = map;
        this.key = key;
    }
    
    public MapItemValue(String symbolName, Map map, Object key) {
        this.symbolName = symbolName;
        this.map = map;
        this.key = key;
    }
    
    @Override
    public void setInner(Object newValue) {
        map.put(key, newValue);
    }
    
    @Override
    public String getSymbolName() {
        return symbolName;
    }
    
    @Override
    public Object get() {
        return map.get(key);
    }
    
    @Override
    public Class<?> getDefinedType() {
        return null;
    }
}
