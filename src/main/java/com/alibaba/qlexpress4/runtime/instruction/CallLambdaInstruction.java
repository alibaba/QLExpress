package com.alibaba.qlexpress4.runtime.instruction;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.runtime.QLambda;
import com.alibaba.qlexpress4.runtime.QRuntime;

/**
 * Author: DQinYuan
 */
public class CallLambdaInstruction extends QLInstruction {

    private final QLambda qLambda;

    public CallLambdaInstruction(ErrorReporter errorReporter, QLambda qLambda) {
        super(errorReporter);
        this.qLambda = qLambda;
    }

    @Override
    public void execute(QRuntime qRuntime, QLOptions qlOptions) {

    }
}
