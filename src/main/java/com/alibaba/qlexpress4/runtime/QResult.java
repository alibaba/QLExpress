package com.alibaba.qlexpress4.runtime;

/**
 * Author: DQinYuan
 */
public class QResult {

    public static final QResult BREAK_RESULT = new QResult(Value.NULL_VALUE, ResultType.BREAK);

    enum ResultType {
        // break
        BREAK,
        // normal return
        RETURN
    }

    public QResult(Value result, ResultType rType) {
        this.result = result;
        this.resultType = rType;
    }

    private final Value result;

    private final ResultType resultType;

    public Value getResult() {
        return result;
    }

    public ResultType getResultType() {
        return resultType;
    }
}
