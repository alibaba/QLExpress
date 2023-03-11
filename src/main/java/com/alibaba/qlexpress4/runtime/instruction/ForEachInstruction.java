package com.alibaba.qlexpress4.runtime.instruction;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.exception.QLRuntimeException;
import com.alibaba.qlexpress4.exception.UserDefineException;
import com.alibaba.qlexpress4.runtime.*;
import com.alibaba.qlexpress4.utils.PrintlnUtils;

import java.lang.reflect.Array;
import java.text.MessageFormat;
import java.util.Iterator;
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

    private final Class<?> itCls;

    public ForEachInstruction(ErrorReporter errorReporter, QLambdaDefinition body,
            Class<?> itCls, ErrorReporter targetErrorReporter) {
        super(errorReporter);
        this.body = body;
        this.itCls = itCls;
        this.targetErrorReporter = targetErrorReporter;
    }

    @Override
    public QResult execute(QContext qContext, QLOptions qlOptions) {
        Object mayBeIterable = qContext.pop().get();
        if (mayBeIterable != null && mayBeIterable.getClass().isArray()) {
            mayBeIterable = new ReflectArrayIterable(mayBeIterable);
        } else if (!(mayBeIterable instanceof Iterable)) {
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
                    case RETURN:
                        return bodyResult;
                    case BREAK:
                        break forEachBody;
                }
            } catch (UserDefineException e) {
                throw errorReporter.report("FOR_EACH_ACCEPT_INVALID_TYPE",
                        MessageFormat.format("for-each accept invalid type, required {0}, but {1} provided",
                                itCls.getName(), item == null? "null": item.getClass().getName()));
            } catch (Throwable t) {
                if (t instanceof QLRuntimeException) {
                    throw (QLRuntimeException) t;
                }
                // should not run there
                throw errorReporter.report("FOR_EACH_UNKNOWN_EXCEPTION",
                        "for-each unknown exception");
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

    private static class ReflectArrayIterable implements Iterable<Object> {

        private final Object arrObj;

        private ReflectArrayIterable(Object arrObj) {
            this.arrObj = arrObj;
        }

        @Override
        public Iterator<Object> iterator() {
            return new ReflectArrayIterator();
        }

        private class ReflectArrayIterator implements Iterator<Object> {

            private int cursor;

            @Override
            public boolean hasNext() {
                return cursor < Array.getLength(arrObj);
            }

            @Override
            public Object next() {
                return Array.get(arrObj, cursor++);
            }
        }
    }

}
