package com.alibaba.qlexpress4.runtime.instruction;

import com.alibaba.qlexpress4.exception.ErrorReporter;

/**
 * @Operation: load variable with local scope
 * @Input: 0
 * @Output: 1 left value of local variable
 *
 * Author: DQinYuan
 */
public class LoadLocalInstruction extends QLInstruction {

    private final String variableName;

    public LoadLocalInstruction(ErrorReporter errorReporter, String variableName) {
        super(errorReporter);
        this.variableName = variableName;
    }
}
