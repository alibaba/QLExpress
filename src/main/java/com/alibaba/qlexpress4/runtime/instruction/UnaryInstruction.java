package com.alibaba.qlexpress4.runtime.instruction;

import java.util.function.Consumer;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.runtime.QContext;
import com.alibaba.qlexpress4.runtime.QResult;
import com.alibaba.qlexpress4.runtime.Value;
import com.alibaba.qlexpress4.runtime.data.DataValue;
import com.alibaba.qlexpress4.runtime.operator.unary.UnaryOperator;
import com.alibaba.qlexpress4.runtime.trace.ExpressionTrace;
import com.alibaba.qlexpress4.utils.PrintlnUtils;

/**
 * Operation: do unary operator like, ++,--,!,~
 * Input: 1
 * Output: 1, unary result
 *
 * Author: DQinYuan
 */
public class UnaryInstruction extends QLInstruction {

    private final UnaryOperator unaryOperator;

    private final Integer traceKey;

    public UnaryInstruction(ErrorReporter errorReporter, UnaryOperator unaryOperator, Integer traceKey) {
        super(errorReporter);
        this.unaryOperator = unaryOperator;
        this.traceKey = traceKey;
    }

    @Override
    public QResult execute(QContext qContext, QLOptions qlOptions) {
        Value value = qContext.pop();
        Object result = unaryOperator.execute(value, errorReporter);
        qContext.push(new DataValue(result));

        // trace
        ExpressionTrace expressionTrace = qContext.getTraces().getExpressionTraceByKey(traceKey);
        if (expressionTrace != null) {
            expressionTrace.valueEvaluated(result);
            expressionTrace.getChildren().get(0).valueEvaluated(value.get());
        }

        return QResult.NEXT_INSTRUCTION;
    }

    @Override
    public int stackInput() {
        return 1;
    }

    @Override
    public int stackOutput() {
        return 1;
    }

    @Override
    public void println(int index, int depth, Consumer<String> debug) {
        PrintlnUtils.printlnByCurDepth(depth, index + ": UnaryOp " + unaryOperator.getOperator(), debug);
    }
}
