package com.alibaba.qlexpress4.spring;

import java.util.Map;

import org.springframework.context.ApplicationContext;

import com.alibaba.qlexpress4.runtime.Value;
import com.alibaba.qlexpress4.runtime.context.ExpressContext;
import com.alibaba.qlexpress4.runtime.data.DataValue;
import com.alibaba.qlexpress4.runtime.data.MapItemValue;

public class QLSpringContext implements ExpressContext {
    
    private final Map<String, Object> context;
    
    private final ApplicationContext applicationContext;
    
    public QLSpringContext(Map<String, Object> context, ApplicationContext applicationContext) {
        this.context = context;
        this.applicationContext = applicationContext;
    }
    
    @Override
    public Value get(Map<String, Object> attachments, String variableName) {
        Object value = context.get(variableName);
        if (value != null) {
            return new MapItemValue(context, variableName);
        }
        Object bean = applicationContext.getBean(variableName);
        if (bean != null) {
            return new DataValue(bean);
        }
        return new MapItemValue(context, value);
    }
}
