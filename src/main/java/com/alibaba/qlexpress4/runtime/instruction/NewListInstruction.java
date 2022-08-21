package com.alibaba.qlexpress4.runtime.instruction;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.runtime.QContext;
import com.alibaba.qlexpress4.runtime.QResult;
import com.alibaba.qlexpress4.utils.PrintlnUtils;

import java.util.function.Consumer;

/**
 * @Operation: new a List with top ${initLength} stack element
 * @Input: ${initLength}
 * @Output: 1
 * <p>
 * Author: DQinYuan
 */
public class NewListInstruction extends QLInstruction {

    private final int initLength;

    public NewListInstruction(ErrorReporter errorReporter, int initLength) {
        super(errorReporter);
        this.initLength = initLength;
    }

    @Override
    public QResult execute(QContext qContext, QLOptions qlOptions) {
        return QResult.CONTINUE_RESULT;
    }

    @Override
    public int stackInput() {
        return initLength;
    }

    @Override
    public int stackOutput() {
        return 1;
    }

    @Override
    public void println(int depth, Consumer<String> debug) {
        PrintlnUtils.printlnByCurDepth(depth, "NewList " + initLength, debug);
    }
}
