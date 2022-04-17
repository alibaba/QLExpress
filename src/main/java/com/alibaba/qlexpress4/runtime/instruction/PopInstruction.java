package com.alibaba.qlexpress4.runtime.instruction;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.runtime.Parameters;
import com.alibaba.qlexpress4.runtime.QRuntime;

/**
 * @Operation: pop top element
 * @Input: 1
 * @Output: 0
 *
 * Author: DQinYuan
 */
public class PopInstruction extends QLInstruction {
    public PopInstruction(ErrorReporter errorReporter) {
        super(errorReporter);
    }

    @Override
    public void execute(Parameters parameters, QRuntime qRuntime, QLOptions qlOptions) {

    }
}
