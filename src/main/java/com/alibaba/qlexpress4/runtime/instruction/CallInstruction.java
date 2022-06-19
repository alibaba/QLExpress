package com.alibaba.qlexpress4.runtime.instruction;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.runtime.QResult;
import com.alibaba.qlexpress4.runtime.Parameters;
import com.alibaba.qlexpress4.runtime.QLambda;
import com.alibaba.qlexpress4.runtime.QRuntime;
import com.alibaba.qlexpress4.runtime.data.lambda.QLambdaMethod;

/**
 * @Operation: call a lambda with fixed number of arguments
 * @Input: ${argNum} + 1
 * @Output: 1, lambda return result
 * <p>
 * Author: DQinYuan
 */
public class CallInstruction extends QLInstruction {

    private final int argNum;

    public CallInstruction(ErrorReporter errorReporter, int argNum) {
        super(errorReporter);
        this.argNum = argNum;
    }

    @Override
    public QResult execute(QRuntime qRuntime, QLOptions qlOptions) {
        Parameters parameters = qRuntime.pop(this.argNum + 1);
        Object bean = parameters.get(0).get();
        Object[] params = this.argNum > 0 ? new Object[this.argNum] : null;
        for (int i = 0; i < this.argNum; i++) {
            params[i] = parameters.get(i + 1).get();
        }
        if (bean == null) {
            throw this.errorReporter.report("CALL_LAMBDA_ERROR", "can not get lambda method from null");
        }
        try {
            QLambda qLambda = (QLambdaMethod) bean;
            qRuntime.push(qLambda.call(params).getResult());
        } catch (Exception e) {
            throw errorReporter.report("CALL_LAMBDA_VALUE_ERROR", "callable method not accessible");
        }
        return QResult.CONTINUE_RESULT;
    }
}
