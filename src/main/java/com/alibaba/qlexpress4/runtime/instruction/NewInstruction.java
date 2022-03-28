package com.alibaba.qlexpress4.runtime.instruction;

import com.alibaba.qlexpress4.exception.ErrorReporter;

/**
 * @Operation: new a class
 * @Input: ${argNum} + 1
 * @Output: 1
 *
 * Author: DQinYuan
 */
public class NewInstruction extends QLInstruction {

    private final Class<?> newClz;

    private final int argNum;

    public NewInstruction(ErrorReporter errorReporter, Class<?> newClz, int argNum) {
        super(errorReporter);
        this.newClz = newClz;
        this.argNum = argNum;
    }
}
