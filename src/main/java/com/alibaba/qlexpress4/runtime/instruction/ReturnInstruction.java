package com.alibaba.qlexpress4.runtime.instruction;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.runtime.QContext;
import com.alibaba.qlexpress4.runtime.QResult;
import com.alibaba.qlexpress4.runtime.data.DataValue;
import com.alibaba.qlexpress4.utils.PrintlnUtils;

import java.util.function.Consumer;

/**
 * Operation: return top element and exit lambda
 * Input: 1
 * Output: 0
 * <p>
 * Author: DQinYuan
 */
public class ReturnInstruction extends QLInstruction {

    private final QResult.ResultType resultType;

    public ReturnInstruction(ErrorReporter errorReporter, QResult.ResultType resultType) {
        super(errorReporter);
        this.resultType = resultType;
    }

    @Override
    public QResult execute(QContext qContext, QLOptions qlOptions) {
        return new QResult(new DataValue(qContext.pop()), resultType);
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
        PrintlnUtils.printlnByCurDepth(depth, index + ": Return", debug);
    }
}
