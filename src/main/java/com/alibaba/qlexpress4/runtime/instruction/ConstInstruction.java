package com.alibaba.qlexpress4.runtime.instruction;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.runtime.Parameters;
import com.alibaba.qlexpress4.runtime.QRuntime;

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

    @Override
    public void execute(Parameters parameters, QRuntime qRuntime, QLOptions qlOptions) {

    }
}
