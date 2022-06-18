package com.alibaba.qlexpress4.runtime.instruction;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.exception.QLRuntimeException;
import com.alibaba.qlexpress4.runtime.QLambda;
import com.alibaba.qlexpress4.runtime.QResult;
import com.alibaba.qlexpress4.runtime.QRuntime;
import com.alibaba.qlexpress4.runtime.data.DataValue;
import com.alibaba.qlexpress4.runtime.util.ThrowUtils;

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

    private final QLambda body;

    private final Map<Class<?>, QLambda> exceptionTable;

    private final QLambda finalBody;

    public TryCatchInstruction(ErrorReporter errorReporter, QLambda body, Map<Class<?>, QLambda> exceptionTable,
                               QLambda finalBody) {
        super(errorReporter);
        this.body = body;
        this.exceptionTable = exceptionTable;
        this.finalBody = finalBody;
    }

    @Override
    public void execute(QRuntime qRuntime, QLOptions qlOptions) {
        QResult tryCatchResult = tryCatchResult(qRuntime);
        if (finalBody != null) {
            try {
                tryCatchResult = finalBody.call();
            } catch (Exception e) {
                throw ThrowUtils.wrapException(e, errorReporter, "TRY_CATCH_FINAL_EXECUTE_ERROR",
                        "try...catch...final... execute error");
            }
        }
        qRuntime.push(new DataValue(tryCatchResult.getResult()));
    }

    private QResult tryCatchResult(QRuntime qRuntime) {
        try {
            QResult callResult = body.call();
            qRuntime.cascadeReturn(callResult);
            return callResult;
        } catch (QLRuntimeException e) {
            Optional<QLambda> exceptionHandlerOp = Optional.ofNullable(e.getAttachment())
                    .map(attach -> exceptionTable.get(attach.getClass()));
            if (!exceptionHandlerOp.isPresent()) {
                throw e;
            }
            Object attachment = e.getAttachment();
            try {
                QResult exceptionHandlerResult = exceptionHandlerOp.get().call(attachment);
                qRuntime.cascadeReturn(exceptionHandlerResult);
                return exceptionHandlerResult;
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
