package com.alibaba.qlexpress4.runtime.instruction;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.runtime.QResult;
import com.alibaba.qlexpress4.runtime.QRuntime;
import com.alibaba.qlexpress4.runtime.Value;
import com.alibaba.qlexpress4.runtime.data.ReadonlyDataValue;

/**
 * @Operation: return top element and exit lambda
 * @Input: 1
 * @Output: 0
 *
 * Author: DQinYuan
 */
public class ReturnInstruction extends QLInstruction {
    public ReturnInstruction(ErrorReporter errorReporter) {
        super(errorReporter);
    }

    @Override
    public void execute(QRuntime qRuntime, QLOptions qlOptions) {
        Object returnValue = qRuntime.pop().get();
        qRuntime.exitAndReturn(new QResult(new ReadonlyDataValue(returnValue),
                QResult.ResultType.RETURN));
    }
}
