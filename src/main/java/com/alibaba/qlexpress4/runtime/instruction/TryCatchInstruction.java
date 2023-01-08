package com.alibaba.qlexpress4.runtime.instruction;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.exception.QLRuntimeException;
import com.alibaba.qlexpress4.runtime.*;
import com.alibaba.qlexpress4.runtime.util.ThrowUtils;
import com.alibaba.qlexpress4.runtime.util.ValueUtils;
import com.alibaba.qlexpress4.utils.PrintlnUtils;

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

    private final Map<Class<?>, QLambdaDefinition> exceptionTable;

    private final QLambdaDefinition finalBody;

    public TryCatchInstruction(ErrorReporter errorReporter, QLambdaDefinition body,
                               Map<Class<?>, QLambdaDefinition> exceptionTable,
                               QLambdaDefinition finalBody) {
        super(errorReporter);
        this.body = body;
        this.exceptionTable = exceptionTable;
        this.finalBody = finalBody;
    }

    @Override
    public QResult execute(int index, QContext qContext, QLOptions qlOptions) {
        QResult tryCatchResult = tryCatchResult(qContext, qlOptions);
        QResult finalResult = finalResult(qContext, qlOptions);
        if (finalResult.getResultType() == QResult.ResultType.RETURN) {
            return finalResult;
        }
        if (tryCatchResult.getResultType() == QResult.ResultType.RETURN) {
            return tryCatchResult;
        }
        Value resultValue = finalBody == null? tryCatchResult.getResult(): finalResult.getResult();
        qContext.push(ValueUtils.toImmutable(resultValue));
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
    public void println(int index, int depth, Consumer<String> debug) {
        PrintlnUtils.printlnByCurDepth(index, depth, "TryCatch", debug);
        body.println(depth+1, debug);
        for (Map.Entry<Class<?>, QLambdaDefinition> clsLambdaEn : exceptionTable.entrySet()) {
            PrintlnUtils.printlnByCurDepth(index, depth+1, clsLambdaEn.getKey().getSimpleName(),
                    debug);
            clsLambdaEn.getValue().println(depth+2, debug);
        }
        finalBody.println(depth+1, debug);
    }

    private QResult finalResult(QContext qContext, QLOptions qlOptions) {
        if (finalBody == null) {
            return QResult.NEXT_INSTRUCTION;
        }
        QLambda finalLambda = finalBody.toLambda(qContext, qlOptions, true);
        try {
            return finalLambda.call();
        } catch (Throwable t) {
            throw ThrowUtils.wrapThrowable(t, errorReporter, "TRY_CATCH_FINAL_EXECUTE_ERROR",
                    "try...catch...final... execute error");
        }
    }

    private QResult tryCatchResult(QContext qContext, QLOptions qlOptions) {
        try {
            QLambda bodyLambda = body.toLambda(qContext, qlOptions, true);
            return bodyLambda.call();
        } catch (QLRuntimeException e) {
            Optional<QLambdaDefinition> exceptionHandlerOp = Optional.ofNullable(e.getCatchObj())
                    .map(attach -> exceptionTable.get(attach.getClass()));
            if (!exceptionHandlerOp.isPresent()) {
                throw e;
            }
            QLambda catchHandlerLambda = exceptionHandlerOp.get()
                    .toLambda(qContext, qlOptions, true);

            Object attachment = e.getCatchObj();
            try {
                // call exceptionHandler
                return catchHandlerLambda.call(attachment);
            } catch (Throwable th) {
                throw ThrowUtils.wrapThrowable(th, errorReporter, "CATCH_HANDLER_EXECUTE_ERROR",
                        "try...catch... handler of '%s' execute error", attachment.getClass().getName());
            }
        } catch (Throwable t) {
            throw ThrowUtils.wrapThrowable(t, errorReporter, "TRY_CATCH_BODY_EXECUTE_ERROR",
                    "try... body execute error");
        }
    }
}
