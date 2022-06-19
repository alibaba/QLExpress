package com.alibaba.qlexpress4.runtime.instruction;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.runtime.QResult;
import com.alibaba.qlexpress4.runtime.QRuntime;
import com.alibaba.qlexpress4.runtime.Value;
import com.alibaba.qlexpress4.runtime.operator.BinaryOperator;

/**
 * @Operation: do middle operator +=,>>,>>>,<<,.
 * @Input: 2
 * @Output: 1, operator result
 *
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
        // a += 1;
        Value leftValue = qRuntime.pop();
        Value rightValue = qRuntime.pop();
        Object result = operator.execute(leftValue, rightValue, errorReporter);
        // TODO bingo 应该push吧？灵葙实现Value
        //qRuntime.push(new Value(result);
        return QResult.CONTINUE_RESULT;
    }
}
