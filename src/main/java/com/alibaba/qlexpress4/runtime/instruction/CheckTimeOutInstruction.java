package com.alibaba.qlexpress4.runtime.instruction;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.runtime.QResult;
import com.alibaba.qlexpress4.runtime.QRuntime;

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
    public QResult execute(QRuntime qRuntime, QLOptions qlOptions) {
        if (qlOptions.getTimeoutMillis() <= 0) {
            return QResult.CONTINUE_RESULT;
        }
        if (System.currentTimeMillis() - qRuntime.scriptStartTimeStamp() > qlOptions.getTimeoutMillis()) {
            // timeout
            throw errorReporter.report("SCRIPT_TIME_OUT",
                    "script exceeds timeout milliseconds, which is " + qlOptions.getTimeoutMillis() + " ms");
        }
        return QResult.CONTINUE_RESULT;
    }
}
