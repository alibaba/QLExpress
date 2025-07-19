package com.alibaba.qlexpress4.runtime.context;

import com.alibaba.qlexpress4.Express4Runner;
import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.QLResult;
import com.alibaba.qlexpress4.runtime.Value;
import com.alibaba.qlexpress4.runtime.data.DataValue;
import com.alibaba.qlexpress4.runtime.data.MapItemValue;

import java.util.HashMap;
import java.util.Map;

public class DynamicVariableContext implements ExpressContext {
    
    private final Express4Runner runner;
    
    private final Map<String, Object> staticContext;
    
    private final QLOptions qlOptions;
    
    private final Map<String, String> dynamicContext;
    
    public DynamicVariableContext(Express4Runner runner, Map<String, Object> staticContext, QLOptions qlOptions,
        Map<String, String> dynamicContext) {
        this.runner = runner;
        this.staticContext = staticContext;
        this.qlOptions = qlOptions;
        this.dynamicContext = dynamicContext;
    }
    
    public DynamicVariableContext(Express4Runner runner, Map<String, Object> staticContext, QLOptions qlOptions) {
        this.runner = runner;
        this.staticContext = staticContext;
        this.qlOptions = qlOptions;
        this.dynamicContext = new HashMap<>();
    }
    
    public void put(String name, String valueExpression) {
        dynamicContext.put(name, valueExpression);
    }
    
    @Override
    public Value get(Map<String, Object> attachments, String variableName) {
        String dynamicScript = dynamicContext.get(variableName);
        if (dynamicScript != null) {
            QLResult result = runner.execute(dynamicScript, this, qlOptions);
            return new DataValue(result.getResult());
        }
        return new MapItemValue(staticContext, variableName);
    }
}
