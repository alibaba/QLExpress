package com.alibaba.qlexpress4.runtime.instruction;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.runtime.QRuntime;
import com.alibaba.qlexpress4.runtime.Value;
import com.alibaba.qlexpress4.runtime.operator.unary.UnaryOperator;

/**
 * @Operation: do unary operator like, ++,--,!,~
 * @Input: 1
 * @Output: 1, unary result
 *
 * Author: DQinYuan
 */
public class UnaryInstruction extends QLInstruction {

    private final UnaryOperator unaryOperator;

    public UnaryInstruction(ErrorReporter errorReporter, UnaryOperator unaryOperator) {
        super(errorReporter);
        this.unaryOperator = unaryOperator;
    }

    @Override
    public void execute(QRuntime qRuntime, QLOptions qlOptions) {
        Value value = qRuntime.pop();
        Object result = unaryOperator.execute(value, errorReporter);
        // push
        //qRuntime.push();
    }
}
