package com.alibaba.qlexpress4.runtime.instruction;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.runtime.QRuntime;

/**
 * @Operation: get next element from Iterator at top of stack
 * @Input: 1
 * @Output: 1
 * <p>
 * Author: DQinYuan
 */
public class NextInstruction extends QLInstruction {
    public NextInstruction(ErrorReporter errorReporter) {
        super(errorReporter);
    }

    @Override
    public void execute(QRuntime qRuntime, QLOptions qlOptions) {

    }
}
