package com.alibaba.qlexpress4.runtime.instruction;

import java.util.function.Consumer;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.runtime.QResult;
import com.alibaba.qlexpress4.runtime.QRuntime;
import com.alibaba.qlexpress4.runtime.Value;
import com.alibaba.qlexpress4.runtime.data.DataValue;
import com.alibaba.qlexpress4.runtime.operator.unary.UnaryOperator;
import com.alibaba.qlexpress4.utils.PrintlnUtils;

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
    public QResult execute(QRuntime qRuntime, QLOptions qlOptions) {
        Value value = qRuntime.pop();
        Object result = unaryOperator.execute(value, errorReporter);
        qRuntime.push(new DataValue(result));
        return QResult.CONTINUE_RESULT;
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
    public void println(int depth, Consumer<String> debug) {
        PrintlnUtils.printlnByCurDepth(depth + 1, "UnaryOp " + unaryOperator.getOperator(), debug);
    }
}
