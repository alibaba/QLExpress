package com.alibaba.qlexpress4.runtime.instruction;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.runtime.QRuntime;

/**
 * Instruction Specification:
 * @Operation: What does it do?
 * @Input: How many stack element it consumes? and their means
 * @Output: How many stack element it push back? and their means
 *
 * Author: DQinYuan
 */
public abstract class QLInstruction {

    protected final ErrorReporter errorReporter;

    public QLInstruction(ErrorReporter errorReporter) {
        this.errorReporter = errorReporter;
    }

    public abstract void execute(QRuntime qRuntime, QLOptions qlOptions);
}
