package com.alibaba.qlexpress4.runtime.instruction;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.runtime.Parameters;
import com.alibaba.qlexpress4.runtime.QRuntime;

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

    @Override
    public void execute(Parameters parameters, QRuntime qRuntime, QLOptions qlOptions) {

    }
}
