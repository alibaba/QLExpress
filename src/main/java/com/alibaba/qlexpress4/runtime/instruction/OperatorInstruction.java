package com.alibaba.qlexpress4.runtime.instruction;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.runtime.QRuntime;

/**
 * @Operation: do middle operator +=,>>,>>>,<<,.
 * @Input: 2
 * @Output: 1, operator result
 *
 * Author: DQinYuan
 */
public class OperatorInstruction extends QLInstruction {

    private final String operator;

    public OperatorInstruction(ErrorReporter errorReporter, String operator) {
        super(errorReporter);
        this.operator = operator;
    }

    @Override
    public void execute(QRuntime qRuntime, QLOptions qlOptions) {

    }
}
