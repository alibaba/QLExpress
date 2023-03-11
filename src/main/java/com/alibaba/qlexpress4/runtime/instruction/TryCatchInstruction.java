package com.alibaba.qlexpress4.runtime.instruction;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.exception.QLRuntimeException;
import com.alibaba.qlexpress4.runtime.*;
import com.alibaba.qlexpress4.runtime.util.ThrowUtils;
import com.alibaba.qlexpress4.runtime.util.ValueUtils;
import com.alibaba.qlexpress4.utils.PrintlnUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * @Operation: try and catch throw element
 * @Input: 0
 * @Output: 1
 *
 * Author: DQinYuan
 */
public class TryCatchInstruction extends QLInstruction {

    private final QLambdaDefinition body;

    private final List<Map.Entry<Class<?>, QLambdaDefinition>> exceptionTable;

    private final QLambdaDefinition finalBody;

    public TryCatchInstruction(ErrorReporter errorReporter, QLambdaDefinition body,
                               List<Map.Entry<Class<?>, QLambdaDefinition>> exceptionTable,
                               QLambdaDefinition finalBody) {
        super(errorReporter);
        this.body = body;
        this.exceptionTable = exceptionTable;
        this.finalBody = finalBody;
    }

    @Override
    public QResult execute(QContext qContext, QLOptions qlOptions) {
        QResult tryCatchResult = tryCatchResult(qContext, qlOptions);
        Value resultValue = tryCatchResult.getResult();
        qContext.push(ValueUtils.toImmutable(resultValue));

        if (finalBody != null) {
            callFinal(qContext, qlOptions);
        }
        return QResult.NEXT_INSTRUCTION;
    }

    @Override
    public int stackInput() {
        return 0;
    }

    @Override
    public int stackOutput() {
        return 1;
    }

    @Override
    public void println(int depth, Consumer<String> debug) {
        PrintlnUtils.printlnByCurDepth(depth, "TryCatch", debug);
        PrintlnUtils.printlnByCurDepth(depth+1, "Body", debug);
        body.println(depth+2, debug);
        for (Map.Entry<Class<?>, QLambdaDefinition> clsLambdaEn : exceptionTable) {
            PrintlnUtils.printlnByCurDepth(depth+1, clsLambdaEn.getKey().getSimpleName(),
                    debug);
            clsLambdaEn.getValue().println(depth+2, debug);
        }
        if (finalBody != null) {
            PrintlnUtils.printlnByCurDepth(depth+1, "Finally", debug);
            finalBody.println(depth+2, debug);
        }
    }

    private void callFinal(QContext qContext, QLOptions qlOptions) {
        QLambda finalLambda = finalBody.toLambda(qContext, qlOptions, true);
        try {
            finalLambda.call();
        } catch (Throwable t) {
            throw ThrowUtils.wrapThrowable(t, errorReporter, "TRY_CATCH_FINAL_EXECUTE_ERROR",
                    "try...catch...final(x) execute error");
        }
    }

    private QResult tryCatchResult(QContext qContext, QLOptions qlOptions) {
        try {
            QLambda bodyLambda = body.toLambda(qContext, qlOptions, true);
            return bodyLambda.call();
        } catch (QLRuntimeException e) {
            Object catchObj = e.getCatchObj();
            if (catchObj == null) {
                // ensure catch object can catch all exception
                catchObj = new Object();
            }
            QResult result = callExceptionHandler(catchObj, qContext, qlOptions);
            if (result == null) {
                throw e;
            }
            return result;
        } catch (Throwable t) {
            QResult result = callExceptionHandler(t, qContext, qlOptions);
            if (result == null) {
                throw ThrowUtils.wrapThrowable(t, errorReporter, "TRY_CATCH_BODY_EXECUTE_ERROR",
                        "try... body execute error");
            }
            return result;
        }
    }

    private QResult callExceptionHandler(Object catchObj, QContext qContext, QLOptions qlOptions) {
        QLambdaDefinition exceptionHandler = getExceptionHandler(catchObj.getClass());
        if (exceptionHandler == null) {
            return null;
        }
        QLambda catchHandlerLambda = exceptionHandler.toLambda(qContext, qlOptions, true);

        try {
            return catchHandlerLambda.call(catchObj);
        } catch (Throwable th) {
            throw ThrowUtils.wrapThrowable(th, errorReporter, "CATCH_HANDLER_EXECUTE_ERROR",
                    "try...catch... handler of '%s' execute error", catchObj.getClass().getName());
        }
    }

    private QLambdaDefinition getExceptionHandler(Class<?> catchObjClass) {
        for (Map.Entry<Class<?>, QLambdaDefinition> clsLambdaEn : exceptionTable) {
            if (clsLambdaEn.getKey().isAssignableFrom(catchObjClass)) {
                return clsLambdaEn.getValue();
            }
        }
        return null;
    }
}
