package com.alibaba.qlexpress4.runtime.instruction;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.runtime.QContext;
import com.alibaba.qlexpress4.runtime.QResult;
import com.alibaba.qlexpress4.utils.PrintlnUtils;

import java.util.function.Consumer;

/**
 * @Operation: check if program timeout
 * @Input: 0
 * @Output: 0
 * <p>
 * Author: DQinYuan
 */
public class CheckTimeOutInstruction extends QLInstruction {
    public CheckTimeOutInstruction(ErrorReporter errorReporter) {
        super(errorReporter);
    }

    @Override
    public QResult execute(QContext qContext, QLOptions qlOptions) {
        if (qlOptions.getTimeoutMillis() <= 0) {
            return QResult.CONTINUE_RESULT;
        }
        if (System.currentTimeMillis() - qContext.scriptStartTimeStamp() > qlOptions.getTimeoutMillis()) {
            // timeout
            throw errorReporter.report("SCRIPT_TIME_OUT",
                    "script exceeds timeout milliseconds, which is " + qlOptions.getTimeoutMillis() + " ms");
        }
        return QResult.CONTINUE_RESULT;
    }

    @Override
    public int stackInput() {
        return 0;
    }

    @Override
    public int stackOutput() {
        return 0;
    }

    @Override
    public void println(int depth, Consumer<String> debug) {
        PrintlnUtils.printlnByCurDepth(depth, "CheckTimeout", debug);
    }
}
