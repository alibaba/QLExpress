package com.alibaba.qlexpress4.runtime.instruction;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.exception.QLRuntimeException;
import com.alibaba.qlexpress4.runtime.*;
import com.alibaba.qlexpress4.utils.PrintlnUtils;

import java.util.function.Consumer;

/**
 * @Operation: process each element in iterable object on top of stack,
 * @Input: 1
 * @Output: 0
 *
 * Author: DQinYuan
 */
public class ForEachInstruction extends QLInstruction {

    private final QLambdaDefinition body;

    private final ErrorReporter targetErrorReporter;

    public ForEachInstruction(ErrorReporter errorReporter, QLambdaDefinition body, ErrorReporter targetErrorReporter) {
        super(errorReporter);
        this.body = body;
        this.targetErrorReporter = targetErrorReporter;
    }

    @Override
    public QResult execute(QContext qContext, QLOptions qlOptions) {
        Object mayBeIterable = qContext.pop().get();
        if (!(mayBeIterable instanceof Iterable)) {
            throw targetErrorReporter.report("FOR_EACH_NOT_ITERABLE",
                    "for-each can only be applied to iterable");
        }
        Iterable<?> iterable = (Iterable<?>) mayBeIterable;
        QLambda bodyLambda = body.toLambda(qContext, qlOptions, true);
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
        return QResult.NEXT_INSTRUCTION;
    }

    @Override
    public int stackInput() {
        return 1;
    }

    @Override
    public int stackOutput() {
        return 0;
    }

    @Override
    public void println(int depth, Consumer<String> debug) {
        PrintlnUtils.printlnByCurDepth(depth, "ForEach", debug);
        body.println(depth+1, debug);
    }
}
