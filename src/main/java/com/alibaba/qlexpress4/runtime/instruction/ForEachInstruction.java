package com.alibaba.qlexpress4.runtime.instruction;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.exception.QLRuntimeException;
import com.alibaba.qlexpress4.runtime.*;

/**
 * @Operation: process each element in iterable object on top of stack,
 * @Input: 1
 * @Output: 0
 *
 * Author: DQinYuan
 */
public class ForEachInstruction extends QLInstruction {

    private final QLambdaDefinition body;

    public ForEachInstruction(ErrorReporter errorReporter, QLambdaDefinition body) {
        super(errorReporter);
        this.body = body;
    }

    @Override
    public QResult execute(QRuntime qRuntime, QLOptions qlOptions) {
        Object mayBeIterable = qRuntime.pop().get();
        if (!(mayBeIterable instanceof Iterable)) {
            throw errorReporter.report("FOR_EACH_NOT_ITERABLE",
                    "for-each can only be applied to iterable");
        }
        Iterable<?> iterable = (Iterable<?>) mayBeIterable;
        QLambda bodyLambda = new QLambdaInner(body, qRuntime, qlOptions, true);
        forEachBody:
        for (Object item : iterable) {
            try {
                QResult bodyResult = bodyLambda.call(item);
                switch (bodyResult.getResultType()) {
                    case CASCADE_RETURN:
                        return bodyResult;
                    case BREAK:
                        break forEachBody;
                }
            } catch (Exception e) {
                if (e instanceof QLRuntimeException) {
                    throw (QLRuntimeException) e;
                }
                // should not run there
                throw errorReporter.report("FOR_EACH_UNKNOWN_EXCEPTION",
                        "for each unknown exception");
            }
        }
        return QResult.CONTINUE_RESULT;
    }
}
