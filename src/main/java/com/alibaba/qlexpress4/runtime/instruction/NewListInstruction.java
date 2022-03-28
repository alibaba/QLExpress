package com.alibaba.qlexpress4.runtime.instruction;

import com.alibaba.qlexpress4.exception.ErrorReporter;

/**
 * @Operation: new a List with top ${initLength} stack element
 * @Input: ${initLength}
 * @Output: 1
 *
 * Author: DQinYuan
 */
public class NewListInstruction extends QLInstruction {

    private final int initLength;

    public NewListInstruction(ErrorReporter errorReporter, int initLength) {
        super(errorReporter);
        this.initLength = initLength;
    }
}
