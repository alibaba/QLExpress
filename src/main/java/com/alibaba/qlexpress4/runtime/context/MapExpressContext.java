package com.alibaba.qlexpress4.runtime.context;

import com.alibaba.qlexpress4.runtime.Value;
import com.alibaba.qlexpress4.runtime.data.MapItemValue;

import java.util.Map;

/**
 * Author: DQinYuan
 */
public class MapExpressContext implements ExpressContext {
    
    private final Map<String, Object> source;
    
    public MapExpressContext(Map<String, Object> source) {
        this.source = source;
    }
    
    @Override
    public Value get(Map<String, Object> attachments, String variableName) {
        return new MapItemValue(source, variableName);
    }
}
