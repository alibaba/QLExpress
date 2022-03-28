package com.alibaba.qlexpress4.runtime.instruction;

import com.alibaba.qlexpress4.exception.ErrorReporter;

/**
 * @Operation: return top element and exit lambda
 * @Input: 1
 * @Output: 0
 *
 * Author: DQinYuan
 */
public class ReturnInstruction extends QLInstruction {
    public ReturnInstruction(ErrorReporter errorReporter) {
        super(errorReporter);
    }
}
