package com.alibaba.qlexpress4.runtime.instruction;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.runtime.Parameters;
import com.alibaba.qlexpress4.runtime.QRuntime;

/**
 * @Operation: check if program timeout
 * @Input: 0
 * @Output: 0
 *
 * Author: DQinYuan
 */
public class CheckTimeOutInstruction extends QLInstruction {
    public CheckTimeOutInstruction(ErrorReporter errorReporter) {
        super(errorReporter);
    }

    @Override
    public void execute(Parameters parameters, QRuntime qRuntime, QLOptions qlOptions) {
        if (qlOptions.getTimeoutMillis() <= 0) {
            return;
        }
        if (System.currentTimeMillis() - qRuntime.scriptStartTimeStamp() > qlOptions.getTimeoutMillis()) {
            // timeout
            throw errorReporter.report("SCRIPT_TIME_OUT",
                    "script exceeds timeout milliseconds, which is " + qlOptions.getTimeoutMillis() + " ms");
        }
    }
}
