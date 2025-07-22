package com.alibaba.qlexpress4.runtime.instruction;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.runtime.QContext;
import com.alibaba.qlexpress4.runtime.QResult;
import com.alibaba.qlexpress4.runtime.trace.ExpressionTrace;
import com.alibaba.qlexpress4.utils.PrintlnUtils;

import java.util.function.Consumer;

/**
 * Operation: no op, only for tracing peek value of stack
 * Input: 0
 * Output: 0
 *
 * Author: DQinYuan
 */
public class TracePeekInstruction extends QLInstruction {
    
    private final Integer traceKey;
    
    public TracePeekInstruction(ErrorReporter errorReporter, Integer traceKey) {
        super(errorReporter);
        this.traceKey = traceKey;
    }
    
    @Override
    public QResult execute(QContext qContext, QLOptions qlOptions) {
        ExpressionTrace expressionTrace = qContext.getTraces().getExpressionTraceByKey(traceKey);
        if (expressionTrace != null) {
            expressionTrace.valueEvaluated(qContext.peek().get());
        }
        return QResult.NEXT_INSTRUCTION;
    }
    
    @Override
    public int stackInput() {
        return 0;
    }
    
    @Override
    public int stackOutput() {
        return 0;
    }
    
    @Override
    public void println(int index, int depth, Consumer<String> debug) {
        PrintlnUtils.printlnByCurDepth(depth, index + ": TracePeek " + traceKey, debug);
    }
}
