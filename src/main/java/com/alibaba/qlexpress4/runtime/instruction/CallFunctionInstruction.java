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
 * @Operation: call ql function from function table
 * @Input: ${argNum}
 * @Output: 1 function result
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
        QFunction function = qContext.getFunction(functionName);
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
            throw ThrowUtils.wrapThrowable(t, errorReporter, "CALL_FUNCTION_UNKNOWN_EXCEPTION",
                    "call function unknown exception");
        }
    }

    private void callLambda(QContext qContext, QLOptions qlOptions) {
        Object lambdaSymbol = qContext.getSymbolValue(functionName);
        if (lambdaSymbol == null) {
            if (qlOptions.isAvoidNullPointer()) {
                qContext.push(DataValue.NULL_VALUE);
            } else {
                throw errorReporter.report(new NullPointerException(), "CALL_FUNCTION_FROM_NULL",
                        "can not call function from null" + functionName);
            }
            return;
        }
        if (!(lambdaSymbol instanceof QLambda)) {
            throw errorReporter.report(
                    "CAN_NOT_FIND_FUNCTION", "can not find function " + functionName);
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
                    "LAMBDA_EXECUTE_EXCEPTION", "lambda execute exception");
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
    public void println(int depth, Consumer<String> debug) {
        PrintlnUtils.printlnByCurDepth(depth, "CallFunction " + functionName + " " + argNum, debug);
    }
}
