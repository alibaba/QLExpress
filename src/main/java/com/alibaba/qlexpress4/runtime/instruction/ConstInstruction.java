package com.alibaba.qlexpress4.runtime.instruction;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.runtime.QContext;
import com.alibaba.qlexpress4.runtime.QResult;
import com.alibaba.qlexpress4.runtime.data.DataValue;
import com.alibaba.qlexpress4.runtime.trace.ExpressionTrace;
import com.alibaba.qlexpress4.utils.PrintlnUtils;

import java.util.function.Consumer;

/**
 * Operation: push constObj to stack
 * Input: 0
 * Output: 1
 * <p>
 * Author: DQinYuan
 */
public class ConstInstruction extends QLInstruction {

    private final Object constObj;

    private final Integer traceKey;

    public ConstInstruction(ErrorReporter errorReporter, Object constObj, Integer traceKey) {
        super(errorReporter);
        this.constObj = constObj;
        this.traceKey = traceKey;
    }

    @Override
    public QResult execute(QContext qContext, QLOptions qlOptions) {
        qContext.push(new DataValue(constObj));

        // trace
        ExpressionTrace expressionTrace = qContext.getTraces().getExpressionTraceByKey(traceKey);
        if (expressionTrace != null) {
            expressionTrace.valueEvaluated(constObj);
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

    public Object getConstObj() {
        return constObj;
    }

    @Override
    public void println(int index, int depth, Consumer<String> debug) {
        PrintlnUtils.printlnByCurDepth(depth,
                index + ": LoadConst " + (constObj == null? "null": constObj.toString()), debug);
    }
}
