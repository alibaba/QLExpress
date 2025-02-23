package com.alibaba.qlexpress4.aparser;

import com.alibaba.qlexpress4.runtime.QLambdaDefinitionInner;
import com.alibaba.qlexpress4.runtime.trace.TracePointTree;

import java.util.List;

public class QCompileCache {

    private final QLambdaDefinitionInner qLambdaDefinition;

    private final List<TracePointTree> expressionTracePoints;

    public QCompileCache(QLambdaDefinitionInner qLambdaDefinition, List<TracePointTree> expressionTracePoints) {
        this.qLambdaDefinition = qLambdaDefinition;
        this.expressionTracePoints = expressionTracePoints;
    }

    public QLambdaDefinitionInner getQLambdaDefinition() {
        return qLambdaDefinition;
    }

    public List<TracePointTree> getExpressionTracePoints() {
        return expressionTracePoints;
    }
}
