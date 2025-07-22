package com.alibaba.qlexpress4.runtime.context;

import com.alibaba.qlexpress4.annotation.QLAlias;
import com.alibaba.qlexpress4.exception.QLException;
import com.alibaba.qlexpress4.runtime.Value;
import com.alibaba.qlexpress4.runtime.data.MapItemValue;

import java.util.HashMap;
import java.util.Map;

public class QLAliasContext implements ExpressContext {
    
    private final Map<String, Object> context;
    
    public QLAliasContext(Object... os) {
        Map<String, Object> context = new HashMap<>();
        for (Object o : os) {
            QLAlias[] qlAliases = o.getClass().getAnnotationsByType(QLAlias.class);
            for (QLAlias qlAlias : qlAliases) {
                for (String alias : qlAlias.value()) {
                    context.put(alias, o);
                }
            }
        }
        this.context = context;
    }
    
    @Override
    public Value get(Map<String, Object> attachments, String variableName) {
        return new MapItemValue(context, variableName);
    }
}
