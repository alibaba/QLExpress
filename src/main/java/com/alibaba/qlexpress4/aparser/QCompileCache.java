package com.alibaba.qlexpress4.aparser;

import com.alibaba.qlexpress4.runtime.QLambdaDefinition;
import com.alibaba.qlexpress4.runtime.trace.TracePointTree;

import java.util.List;

public class QCompileCache {
    
    private final QLambdaDefinition qLambdaDefinition;
    
    private final List<TracePointTree> expressionTracePoints;
    
    public QCompileCache(QLambdaDefinition qLambdaDefinition, List<TracePointTree> expressionTracePoints) {
        this.qLambdaDefinition = qLambdaDefinition;
        this.expressionTracePoints = expressionTracePoints;
    }
    
    public QLambdaDefinition getQLambdaDefinition() {
        return qLambdaDefinition;
    }
    
    public List<TracePointTree> getExpressionTracePoints() {
        return expressionTracePoints;
    }
}
