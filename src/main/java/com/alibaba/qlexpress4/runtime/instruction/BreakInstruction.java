package com.alibaba.qlexpress4.runtime.instruction;

import com.alibaba.qlexpress4.exception.ErrorReporter;

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
}
