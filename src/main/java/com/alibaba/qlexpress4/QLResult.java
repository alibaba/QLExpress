package com.alibaba.qlexpress4;

import com.alibaba.qlexpress4.runtime.trace.ExpressionTrace;

import java.util.List;

public class QLResult {

    private final Object result;

    private final List<ExpressionTrace> expressionTraces;

    public QLResult(Object result, List<ExpressionTrace> expressionTraces) {
        this.result = result;
        this.expressionTraces = expressionTraces;
    }

    public Object getResult() {
        return result;
    }

    public List<ExpressionTrace> getExpressionTraces() {
        return expressionTraces;
    }
}
