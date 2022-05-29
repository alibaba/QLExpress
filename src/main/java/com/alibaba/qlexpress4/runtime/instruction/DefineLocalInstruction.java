package com.alibaba.qlexpress4.runtime.instruction;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.runtime.QRuntime;

/**
 * @Operation: define a symbol in local scope
 * @Input: 1 symbol init value
 * @Output: 0
 *
 * Author: DQinYuan
 */
public class DefineLocalInstruction extends QLInstruction {

    private final String variableName;

    public DefineLocalInstruction(ErrorReporter errorReporter, String variableName) {
        super(errorReporter);
        this.variableName = variableName;
    }

    @Override
    public void execute(QRuntime qRuntime, QLOptions qlOptions) {

    }
}
