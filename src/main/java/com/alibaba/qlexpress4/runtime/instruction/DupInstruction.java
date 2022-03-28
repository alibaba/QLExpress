package com.alibaba.qlexpress4.runtime.instruction;

import com.alibaba.qlexpress4.exception.ErrorReporter;

/**
 * @Operation: duplicate top element at the top of stack
 * @Input: 0
 * @Output: 1, duplication of top element
 *
 * Author: DQinYuan
 */
public class DupInstruction extends QLInstruction {
    public DupInstruction(ErrorReporter errorReporter) {
        super(errorReporter);
    }
}
