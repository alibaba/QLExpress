package com.alibaba.qlexpress4.runtime.instruction;

import com.alibaba.qlexpress4.exception.ErrorReporter;

/**
 * @Operation: pop top element
 * @Input: 1
 * @Output: 0
 *
 * Author: DQinYuan
 */
public class PopInstruction extends QLInstruction {
    public PopInstruction(ErrorReporter errorReporter) {
        super(errorReporter);
    }
}
