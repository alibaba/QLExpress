package com.alibaba.qlexpress4.runtime.instruction;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.runtime.QRuntime;

/**
 * @Operation: return top element and exit lambda
 * @Input: 1
 * @Output: 0
 * <p>
 * Author: DQinYuan
 */
public class ReturnInstruction extends QLInstruction {
    public ReturnInstruction(ErrorReporter errorReporter) {
        super(errorReporter);
    }

    @Override
    public void execute(QRuntime qRuntime, QLOptions qlOptions) {

    }
}
