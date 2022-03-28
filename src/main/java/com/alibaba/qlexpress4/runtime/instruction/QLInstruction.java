package com.alibaba.qlexpress4.runtime.instruction;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.exception.ErrorReporter;

/**
 * Instruction Specification:
 * @Operation: What does it do?
 * @Input: How many stack element it consumes? and their means
 * @Output: How many stack element it push back? and their means
 *
 * Author: DQinYuan
 */
public abstract class QLInstruction {

    private final ErrorReporter errorReporter;

    public QLInstruction(ErrorReporter errorReporter) {
        this.errorReporter = errorReporter;
    }

    public void execute(QLOptions qlOptions) {
        throw errorReporter.report("SOME_CODE", "type error");
    }
}
