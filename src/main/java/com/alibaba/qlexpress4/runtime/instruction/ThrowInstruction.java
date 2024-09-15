package com.alibaba.qlexpress4.runtime.instruction;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.exception.QLErrorCodes;
import com.alibaba.qlexpress4.runtime.QContext;
import com.alibaba.qlexpress4.runtime.QResult;
import com.alibaba.qlexpress4.utils.PrintlnUtils;

import java.util.function.Consumer;

/**
 * Operation: throw top element on the stack
 * Input: 1
 * Output: 0
 * <p>
 * Author: DQinYuan
 */
public class ThrowInstruction extends QLInstruction {
    public ThrowInstruction(ErrorReporter errorReporter) {
        super(errorReporter);
    }

    @Override
    public QResult execute(QContext qContext, QLOptions qlOptions) {
        Object throwObj = qContext.pop().get();
        throw errorReporter.report(throwObj, QLErrorCodes.QL_THROW.name(), QLErrorCodes.QL_THROW.getErrorMsg());
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
        PrintlnUtils.printlnByCurDepth(depth, index + ": Throw", debug);
    }
}
