package com.alibaba.qlexpress4.runtime.instruction;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.exception.QLRuntimeException;
import com.alibaba.qlexpress4.runtime.QLambda;
import com.alibaba.qlexpress4.runtime.QResult;
import com.alibaba.qlexpress4.runtime.QRuntime;

import java.util.Iterator;

/**
 * @Operation: process each element in iterable object on top of stack,
 * @Input: 1
 * @Output: 0
 *
 * Author: DQinYuan
 */
public class ForEachInstruction extends QLInstruction {

    private final QLambda body;

    public ForEachInstruction(ErrorReporter errorReporter, QLambda body) {
        super(errorReporter);
        this.body = body;
    }

    @Override
    public void execute(QRuntime qRuntime, QLOptions qlOptions) {
        Object mayBeIterable = qRuntime.pop().get();
        if (!(mayBeIterable instanceof Iterable)) {
            throw errorReporter.report("FOR_EACH_NOT_ITERABLE",
                    "for-each can only be applied to iterable");
        }
        Iterable<?> iterable = (Iterable<?>) mayBeIterable;
        for (Object item : iterable) {
            try {
                QResult bodyResult = body.call(item);
                if (QResult.ResultType.BREAK == bodyResult.getResultType()) {
                    break;
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
    }
}
