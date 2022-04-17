package com.alibaba.qlexpress4.runtime.instruction;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.runtime.Parameters;
import com.alibaba.qlexpress4.runtime.QRuntime;

/**
 * @Operation: force cast value to specified type
 * @Input: 1
 * @Output: 1
 *
 * Author: DQinYuan
 */
public class CastInstruction extends QLInstruction {

    private final Class<?> targetClz;

    public CastInstruction(ErrorReporter errorReporter, Class<?> targetClz) {
        super(errorReporter);
        this.targetClz = targetClz;
    }

    @Override
    public void execute(Parameters parameters, QRuntime qRuntime, QLOptions qlOptions) {

    }
}
