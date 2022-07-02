package com.alibaba.qlexpress4.runtime.instruction;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.runtime.QResult;
import com.alibaba.qlexpress4.runtime.QRuntime;
import com.alibaba.qlexpress4.runtime.Value;
import com.alibaba.qlexpress4.runtime.data.DataValue;
import com.alibaba.qlexpress4.runtime.operator.BinaryOperator;

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
    public QResult execute(QRuntime qRuntime, QLOptions qlOptions) {
        Value rightValue = qRuntime.pop();
        Value leftValue = qRuntime.pop();
        Object result = operator.execute(leftValue, rightValue, errorReporter);
        qRuntime.push(new DataValue(result));
        return QResult.CONTINUE_RESULT;
    }

    @Override
    public int stackInput() {
        return 2;
    }

    @Override
    public int stackOutput() {
        return 1;
    }
}
