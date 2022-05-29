package com.alibaba.qlexpress4.runtime.instruction;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.runtime.QLambda;
import com.alibaba.qlexpress4.runtime.QRuntime;

import java.util.Map;

/**
 * @Operation: try and catch throw element
 * @Input: 0
 * @Output: 0
 * <p>
 * Author: DQinYuan
 */
public class TryCatchInstruction extends QLInstruction {

    private final QLambda body;

    private final Map<Class<?>, QLambda> exceptionTable;

    public TryCatchInstruction(ErrorReporter errorReporter, QLambda body, Map<Class<?>, QLambda> exceptionTable) {
        super(errorReporter);
        this.body = body;
        this.exceptionTable = exceptionTable;
    }

    @Override
    public void execute(QRuntime qRuntime, QLOptions qlOptions) {

    }
}
