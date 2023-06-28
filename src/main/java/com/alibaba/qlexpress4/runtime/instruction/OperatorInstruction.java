package com.alibaba.qlexpress4.runtime.instruction;

import java.util.function.Consumer;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.runtime.QContext;
import com.alibaba.qlexpress4.runtime.QResult;
import com.alibaba.qlexpress4.runtime.Value;
import com.alibaba.qlexpress4.runtime.data.DataValue;
import com.alibaba.qlexpress4.runtime.operator.BinaryOperator;
import com.alibaba.qlexpress4.utils.PrintlnUtils;

/**
 * @Operation: do middle operator +=,>>,>>>,<<,.
 * @Input: 2
 * @Output: 1, operator result
 * <p>
 * Author: DQinYuan
 */
public class OperatorInstruction extends QLInstruction {

    private final BinaryOperator operator;

    public OperatorInstruction(ErrorReporter errorReporter, BinaryOperator operator) {
        super(errorReporter);
        this.operator = operator;
    }

    @Override
    public QResult execute(QContext qContext, QLOptions qlOptions) {
        Value rightValue = qContext.pop();
        Value leftValue = qContext.pop();
        Object result = operator.execute(leftValue, rightValue, qlOptions, errorReporter);
        qContext.push(new DataValue(result));
        return QResult.NEXT_INSTRUCTION;
    }

    @Override
    public int stackInput() {
        return 2;
    }

    @Override
    public int stackOutput() {
        return 1;
    }

    @Override
    public void println(int depth, Consumer<String> debug) {
        PrintlnUtils.printlnByCurDepth(depth, "Operator " + operator.getOperator(), debug);
    }

    public BinaryOperator getOperator() {
        return operator;
    }
}
