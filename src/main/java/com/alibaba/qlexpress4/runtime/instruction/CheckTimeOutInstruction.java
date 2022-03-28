package com.alibaba.qlexpress4.runtime.instruction;

import com.alibaba.qlexpress4.exception.ErrorReporter;

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
}
