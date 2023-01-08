package com.alibaba.qlexpress4.runtime.instruction;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.exception.UserDefineException;
import com.alibaba.qlexpress4.runtime.*;
import com.alibaba.qlexpress4.runtime.data.DataValue;
import com.alibaba.qlexpress4.runtime.util.ThrowUtils;
import com.alibaba.qlexpress4.runtime.util.ValueUtils;
import com.alibaba.qlexpress4.utils.PrintlnUtils;

import java.util.function.Consumer;

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
    public QResult execute(int index, QContext qContext, QLOptions qlOptions) {
        Parameters parameters = qContext.pop(this.argNum + 1);
        Object bean = parameters.get(0).get();
        if (bean == null && qlOptions.isAvoidNullPointer()) {
            qContext.push(DataValue.NULL_VALUE);
            return QResult.NEXT_INSTRUCTION;
        }
        if (!(bean instanceof QLambda)) {
            throw this.errorReporter.report("OBJECT_NOT_CALLABLE",
                    "left side is not callable object");
        }
        Object[] params = new Object[this.argNum];
        for (int i = 0; i < this.argNum; i++) {
            params[i] = parameters.get(i + 1).get();
        }
        try {
            QLambda qLambda = (QLambda) bean;
            qContext.push(ValueUtils.toImmutable(qLambda.call(params).getResult()));
            return QResult.NEXT_INSTRUCTION;
        } catch (UserDefineException e) {
            throw ThrowUtils.reportUserDefinedException(errorReporter, e);
        } catch (Throwable t) {
            throw ThrowUtils.wrapThrowable(t, errorReporter,
                    "LAMBDA_EXECUTE_EXCEPTION", "lambda execute exception");
        }
    }

    @Override
    public int stackInput() {
        return argNum + 1;
    }

    @Override
    public int stackOutput() {
        return 1;
    }

    @Override
    public void println(int index, int depth, Consumer<String> debug) {
        PrintlnUtils.printlnByCurDepth(index, depth, "Call with argNum " + argNum, debug);
    }
}
