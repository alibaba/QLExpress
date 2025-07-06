package com.alibaba.qlexpress4.runtime.instruction;

import java.util.function.Consumer;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.runtime.QContext;
import com.alibaba.qlexpress4.runtime.QResult;
import com.alibaba.qlexpress4.runtime.Value;
import com.alibaba.qlexpress4.runtime.data.DataValue;
import com.alibaba.qlexpress4.runtime.trace.ExpressionTrace;
import com.alibaba.qlexpress4.utils.PrintlnUtils;

/**
 * Operation: return top element and exit lambda
 * Input: 1
 * Output: 0
 * <p>
 * Author: DQinYuan
 */
public class ReturnInstruction extends QLInstruction {

    private final QResult.ResultType resultType;
    private final Integer traceKey;

    public ReturnInstruction(ErrorReporter errorReporter, QResult.ResultType resultType, Integer traceKey) {
        super(errorReporter);
        this.resultType = resultType;
        this.traceKey = traceKey;
    }

    @Override
    public QResult execute(QContext qContext, QLOptions qlOptions) {
        Value returnValue = qContext.pop();
        if (traceKey != null) {
            ExpressionTrace expressionTrace = qContext.getTraces().getExpressionTraceByKey(traceKey);
            if (expressionTrace != null) {
                expressionTrace.valueEvaluated(returnValue.get());
            }
        }
        return new QResult(new DataValue(returnValue.get()), resultType);
    }

    @Override
    public int stackInput() {
        return 1;
    }

    @Override
    public int stackOutput() {
        return 0;
    }

    @Override
    public void println(int index, int depth, Consumer<String> debug) {
        PrintlnUtils.printlnByCurDepth(depth, index + ": Return", debug);
    }
}
