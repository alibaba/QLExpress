package com.alibaba.qlexpress4.runtime.instruction;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.runtime.Parameters;
import com.alibaba.qlexpress4.runtime.QLambda;
import com.alibaba.qlexpress4.runtime.QRuntime;

/**
 * @Operation: while (condition) do body
 * @Input: 0
 * @Output: 0
 *
 * Author: DQinYuan
 */
public class WhileInstruction extends QLInstruction {

    private final QLambda condition;

    private final QLambda body;

    public WhileInstruction(ErrorReporter errorReporter, QLambda condition, QLambda body) {
        super(errorReporter);
        this.condition = condition;
        this.body = body;
    }

    @Override
    public void execute(Parameters parameters, QRuntime qRuntime, QLOptions qlOptions) {

    }
}
