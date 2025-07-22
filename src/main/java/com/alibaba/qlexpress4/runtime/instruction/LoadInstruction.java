package com.alibaba.qlexpress4.runtime.instruction;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.runtime.*;
import com.alibaba.qlexpress4.runtime.trace.ExpressionTrace;
import com.alibaba.qlexpress4.utils.PrintlnUtils;

import java.util.function.Consumer;

/**
 * Operation: load variable from local to global scope, create when not exist
 * Input: 0
 * Output: 1 left value of local variable
 *
 * Author: DQinYuan
 */
public class LoadInstruction extends QLInstruction {
    
    private final String name;
    
    private final Integer traceKey;
    
    public LoadInstruction(ErrorReporter errorReporter, String name, Integer traceKey) {
        super(errorReporter);
        this.name = name;
        this.traceKey = traceKey;
    }
    
    @Override
    public QResult execute(QContext qContext, QLOptions qlOptions) {
        Value symbolValue = qContext.getSymbol(name);
        qContext.push(symbolValue);
        
        // trace
        ExpressionTrace expressionTrace = qContext.getTraces().getExpressionTraceByKey(traceKey);
        if (expressionTrace != null) {
            expressionTrace.valueEvaluated(symbolValue.get());
        }
        
        return QResult.NEXT_INSTRUCTION;
    }
    
    @Override
    public int stackInput() {
        return 0;
    }
    
    @Override
    public int stackOutput() {
        return 1;
    }
    
    public String getName() {
        return name;
    }
    
    @Override
    public void println(int index, int depth, Consumer<String> debug) {
        PrintlnUtils.printlnByCurDepth(depth, index + ": Load " + name, debug);
    }
}
