package com.alibaba.qlexpress4.runtime.instruction;

import com.alibaba.qlexpress4.exception.ErrorReporter;

/**
 * @Operation: throw top element on the stack
 * @Input: 1
 * @Output: 0
 *
 * Author: DQinYuan
 */
public class ThrowInstruction extends QLInstruction {
    public ThrowInstruction(ErrorReporter errorReporter) {
        super(errorReporter);
    }
}
