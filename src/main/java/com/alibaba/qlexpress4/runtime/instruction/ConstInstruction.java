package com.alibaba.qlexpress4.runtime.instruction;

import com.alibaba.qlexpress4.exception.ErrorReporter;

/**
 * @Operation: push constObj to stack
 * @Input: 0
 * @Output: 1
 *
 * Author: DQinYuan
 */
public class ConstInstruction extends QLInstruction {

    private final Object constObj;

    public ConstInstruction(ErrorReporter errorReporter, Object constObj) {
        super(errorReporter);
        this.constObj = constObj;
    }
}
