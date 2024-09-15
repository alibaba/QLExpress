package com.alibaba.qlexpress4.runtime.instruction;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.exception.QLErrorCodes;
import com.alibaba.qlexpress4.exception.UserDefineException;
import com.alibaba.qlexpress4.runtime.*;
import com.alibaba.qlexpress4.runtime.data.DataValue;
import com.alibaba.qlexpress4.runtime.function.CustomFunction;
import com.alibaba.qlexpress4.runtime.util.ThrowUtils;
import com.alibaba.qlexpress4.runtime.util.ValueUtils;
import com.alibaba.qlexpress4.utils.PrintlnUtils;

import java.util.function.Consumer;

/**
 * Operation: call ql function from function table
 * Input: ${argNum}
 * Output: 1 function result
 * Author: DQinYuan
 */
public class CallFunctionInstruction extends QLInstruction {

    private final String functionName;

    private final int argNum;

    public CallFunctionInstruction(ErrorReporter errorReporter, String functionName, int argNum) {
        super(errorReporter);
        this.functionName = functionName;
        this.argNum = argNum;
    }

    @Override
    public QResult execute(QContext qContext, QLOptions qlOptions) {
        CustomFunction function = qContext.getFunction(functionName);
        if (function == null) {
            callLambda(qContext, qlOptions);
            return QResult.NEXT_INSTRUCTION;
        }
        Parameters parameters = qContext.pop(argNum);
        try {
            qContext.push(new DataValue(function.call(qContext, parameters)));
            return QResult.NEXT_INSTRUCTION;
        } catch (UserDefineException e) {
            throw ThrowUtils.reportUserDefinedException(errorReporter, e);
        } catch (Throwable t) {
            throw ThrowUtils.wrapThrowable(t, errorReporter, QLErrorCodes.INVOKE_FUNCTION_INNER_ERROR.name(),
                    String.format(QLErrorCodes.INVOKE_FUNCTION_INNER_ERROR.getErrorMsg(), functionName, t.getMessage())
            );
        }
    }

    private void callLambda(QContext qContext, QLOptions qlOptions) {
        Object lambdaSymbol = qContext.getSymbolValue(functionName);
        if (lambdaSymbol == null) {
            if (qlOptions.isAvoidNullPointer()) {
                qContext.pop(argNum);
                qContext.push(DataValue.NULL_VALUE);
            } else {
                throw errorReporter.report(new NullPointerException(), QLErrorCodes.FUNCTION_NOT_FOUND.name(),
                        String.format(QLErrorCodes.FUNCTION_NOT_FOUND.getErrorMsg(), functionName)
                );
            }
            return;
        }
        if (!(lambdaSymbol instanceof QLambda)) {
            throw errorReporter.report(
                    QLErrorCodes.FUNCTION_TYPE_MISMATCH.name(),
                    String.format(QLErrorCodes.FUNCTION_TYPE_MISMATCH.getErrorMsg(), functionName)
            );
        }
        Parameters parameters = qContext.pop(argNum);
        Object[] parametersArr = new Object[parameters.size()];
        for (int i = 0; i < parametersArr.length; i++) {
            parametersArr[i] = parameters.get(i).get();
        }
        try {
            Value resultValue = ((QLambda) lambdaSymbol).call(parametersArr).getResult();
            qContext.push(ValueUtils.toImmutable(resultValue));
        } catch (UserDefineException e) {
            throw ThrowUtils.reportUserDefinedException(errorReporter, e);
        } catch (Throwable t) {
            throw ThrowUtils.wrapThrowable(t, errorReporter,
                    QLErrorCodes.INVOKE_LAMBDA_ERROR.name(),
                    QLErrorCodes.INVOKE_LAMBDA_ERROR.getErrorMsg()
            );
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
        PrintlnUtils.printlnByCurDepth(depth, index + ": CallFunction " + functionName + " " + argNum, debug);
    }
}
