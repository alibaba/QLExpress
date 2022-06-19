package com.alibaba.qlexpress4.runtime.instruction;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.exception.ErrorReporter;
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
    public QResult execute(QRuntime qRuntime, QLOptions qlOptions) {
        return QResult.BREAK_RESULT;
    }
}
