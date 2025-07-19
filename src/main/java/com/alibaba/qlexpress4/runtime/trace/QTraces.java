package com.alibaba.qlexpress4.runtime.trace;

import java.util.List;
import java.util.Map;

public class QTraces {
    
    private final List<ExpressionTrace> expressionTraces;
    
    private final Map<Integer, ExpressionTrace> expressionTraceMap;
    
    public QTraces(List<ExpressionTrace> expressionTraces, Map<Integer, ExpressionTrace> expressionTraceMap) {
        this.expressionTraces = expressionTraces;
        this.expressionTraceMap = expressionTraceMap;
    }
    
    public ExpressionTrace getExpressionTraceByKey(Integer traceKey) {
        if (traceKey == null) {
            return null;
        }
        if (expressionTraceMap == null) {
            return null;
        }
        return expressionTraceMap.get(traceKey);
    }
    
    public List<ExpressionTrace> getExpressionTraces() {
        return expressionTraces;
    }
}
