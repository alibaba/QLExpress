package com.alibaba.qlexpress4.runtime.instruction;

import com.alibaba.qlexpress4.exception.ErrorReporter;

/**
 * @Operation: get Iterator from Iterable top of stack
 * @Input: 1
 * @Output: 1
 *
 * Author: DQinYuan
 */
public class IteratorInstruction extends QLInstruction {
    public IteratorInstruction(ErrorReporter errorReporter) {
        super(errorReporter);
    }
}
