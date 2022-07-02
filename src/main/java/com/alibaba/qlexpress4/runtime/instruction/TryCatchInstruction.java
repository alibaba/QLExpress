package com.alibaba.qlexpress4.runtime.instruction;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.exception.QLRuntimeException;
import com.alibaba.qlexpress4.runtime.*;
import com.alibaba.qlexpress4.runtime.util.ThrowUtils;
import com.alibaba.qlexpress4.runtime.util.ValueUtils;

import java.util.Map;
import java.util.Optional;

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
    public QResult execute(QRuntime qRuntime, QLOptions qlOptions) {
        QResult tryCatchResult = tryCatchResult(qRuntime, qlOptions);
        QResult finalResult = finalResult(qRuntime, qlOptions);
        if (finalResult.getResultType() == QResult.ResultType.CASCADE_RETURN) {
            return finalResult;
        }
        if (tryCatchResult.getResultType() == QResult.ResultType.CASCADE_RETURN) {
            return tryCatchResult;
        }
        Value resultValue = finalBody == null? tryCatchResult.getResult(): finalResult.getResult();
        qRuntime.push(ValueUtils.toImmutable(resultValue));
        return QResult.CONTINUE_RESULT;
    }

    @Override
    public int stackInput() {
        return 0;
    }

    @Override
    public int stackOutput() {
        return 1;
    }

    private QResult finalResult(QRuntime qRuntime, QLOptions qlOptions) {
        if (finalBody == null) {
            return QResult.CONTINUE_RESULT;
        }
        QLambda finalLambda = finalBody.toLambda(qRuntime, qlOptions, true);
        try {
            return finalLambda.call();
        } catch (Exception e) {
            throw ThrowUtils.wrapException(e, errorReporter, "TRY_CATCH_FINAL_EXECUTE_ERROR",
                    "try...catch...final... execute error");
        }
    }

    private QResult tryCatchResult(QRuntime qRuntime, QLOptions qlOptions) {
        try {
            QLambda bodyLambda = body.toLambda(qRuntime, qlOptions, true);
            return bodyLambda.call();
        } catch (QLRuntimeException e) {
            Optional<QLambdaDefinition> exceptionHandlerOp = Optional.ofNullable(e.getAttachment())
                    .map(attach -> exceptionTable.get(attach.getClass()));
            if (!exceptionHandlerOp.isPresent()) {
                throw e;
            }
            QLambda catchHandlerLambda = exceptionHandlerOp.get()
                    .toLambda(qRuntime, qlOptions, true);

            Object attachment = e.getAttachment();
            try {
                // call exceptionHandler
                return catchHandlerLambda.call(attachment);
            } catch (Exception ex) {
                throw ThrowUtils.wrapException(ex, errorReporter, "CATCH_HANDLER_EXECUTE_ERROR",
                        "try...catch... handler of '%s' execute error", attachment.getClass().getName());
            }
        } catch (Exception e) {
            throw ThrowUtils.wrapException(e, errorReporter, "TRY_CATCH_BODY_EXECUTE_ERROR",
                    "try... body execute error");
        }
    }
}
