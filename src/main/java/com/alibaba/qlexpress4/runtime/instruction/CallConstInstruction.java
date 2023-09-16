package com.alibaba.qlexpress4.runtime.instruction;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.exception.UserDefineException;
import com.alibaba.qlexpress4.runtime.*;
import com.alibaba.qlexpress4.runtime.util.ThrowUtils;
import com.alibaba.qlexpress4.runtime.util.ValueUtils;
import com.alibaba.qlexpress4.utils.PrintlnUtils;

import java.util.function.Consumer;

/**
 * Operation: call const lambda
 * Input: ${argNum}
 * Output: 1 const lambda result
 * <p>
 * Author: DQinYuan
 */
public class CallConstInstruction extends QLInstruction {

    private final QLambda constLambda;

    private final int argNum;

    private final String lambdaName;

    public CallConstInstruction(ErrorReporter errorReporter, QLambda constLambda, int argNum, String lambdaName) {
        super(errorReporter);
        this.constLambda = constLambda;
        this.argNum = argNum;
        this.lambdaName = lambdaName;
    }

    @Override
    public QResult execute(QContext qContext, QLOptions qlOptions) {
        Parameters args = qContext.pop(argNum);
        Object[] argArr = new Object[argNum];
        for (int i = 0; i < argNum; i++) {
            argArr[i] = args.getValue(i);
        }

        try {
            QResult result = constLambda.call(argArr);
            qContext.push(ValueUtils.toImmutable(result.getResult()));
            return QResult.NEXT_INSTRUCTION;
        } catch (UserDefineException e) {
            throw ThrowUtils.reportUserDefinedException(errorReporter, e);
        } catch (Throwable t) {
            throw ThrowUtils.wrapThrowable(t, errorReporter, "BLOCK_EXECUTE_ERROR", "block execute error");
        }
    }

    @Override
    public int stackInput() {
        return argNum;
    }

    @Override
    public int stackOutput() {
        return 1;
    }

    @Override
    public void println(int index, int depth, Consumer<String> debug) {
        PrintlnUtils.printlnByCurDepth(depth, index + ": CallConstLambda " + lambdaName, debug);
    }
}
