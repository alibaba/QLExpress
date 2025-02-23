package com.alibaba.qlexpress4.runtime;

import com.alibaba.qlexpress4.runtime.trace.QTraces;

public class QLambdaTrace {

    private final QLambda qLambda;

    private final QTraces traces;

    public QLambdaTrace(QLambda qLambda, QTraces traces) {
        this.qLambda = qLambda;
        this.traces = traces;
    }

    public QLambda getqLambda() {
        return qLambda;
    }

    public QTraces getTraces() {
        return traces;
    }
}
