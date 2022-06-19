package com.alibaba.qlexpress4.runtime.instruction;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.runtime.QResult;
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
    public QResult execute(QRuntime qRuntime, QLOptions qlOptions) {
        qRuntime.pop();
        return QResult.CONTINUE_RESULT;
    }
}
