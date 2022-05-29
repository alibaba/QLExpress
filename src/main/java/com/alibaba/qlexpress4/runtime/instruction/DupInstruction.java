package com.alibaba.qlexpress4.runtime.instruction;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.runtime.QRuntime;

/**
 * @Operation: duplicate top element at the top of stack
 * @Input: 0
 * @Output: 1, duplication of top element
 * <p>
 * Author: DQinYuan
 */
public class DupInstruction extends QLInstruction {
    public DupInstruction(ErrorReporter errorReporter) {
        super(errorReporter);
    }

    @Override
    public void execute(QRuntime qRuntime, QLOptions qlOptions) {
        qRuntime.dup();
    }
}
