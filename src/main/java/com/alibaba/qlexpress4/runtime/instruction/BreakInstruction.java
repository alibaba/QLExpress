package com.alibaba.qlexpress4.runtime.instruction;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.runtime.Parameters;
import com.alibaba.qlexpress4.runtime.QResult;
import com.alibaba.qlexpress4.runtime.QRuntime;

/**
 * @Operation: return break object and exit lambda
 * @Input: 0
 * @Output: 0
 *
 * Author: DQinYuan
 */
public class BreakInstruction extends QLInstruction {
    public BreakInstruction(ErrorReporter errorReporter) {
        super(errorReporter);
    }

    @Override
    public void execute(Parameters parameters, QRuntime qRuntime, QLOptions qlOptions) {
        qRuntime.exitAndReturn(QResult.BREAK_RESULT);
    }
}
