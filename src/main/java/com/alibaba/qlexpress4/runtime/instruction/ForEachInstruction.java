package com.alibaba.qlexpress4.runtime.instruction;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.runtime.QLambda;
import com.alibaba.qlexpress4.runtime.QRuntime;

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

    }
}
