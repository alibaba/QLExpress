package com.alibaba.qlexpress4.runtime.instruction;

import com.alibaba.qlexpress4.exception.ErrorReporter;

/**
 * @Operation: get next element from Iterator at top of stack
 * @Input: 1
 * @Output: 1
 *
 * Author: DQinYuan
 */
public class NextInstruction extends QLInstruction {
    public NextInstruction(ErrorReporter errorReporter) {
        super(errorReporter);
    }
}
