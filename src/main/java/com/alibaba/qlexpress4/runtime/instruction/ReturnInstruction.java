package com.alibaba.qlexpress4.runtime.instruction;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.runtime.QResult;
import com.alibaba.qlexpress4.runtime.QRuntime;
import com.alibaba.qlexpress4.runtime.data.DataValue;

/**
 * @Operation: return top element and exit lambda
 * @Input: 1
 * @Output: 0
 * <p>
 * Author: DQinYuan
 */
public class ReturnInstruction extends QLInstruction {

    private final QResult.ResultType resultType;

    public ReturnInstruction(ErrorReporter errorReporter, QResult.ResultType resultType) {
        super(errorReporter);
        this.resultType = resultType;
    }

    @Override
    public QResult execute(QRuntime qRuntime, QLOptions qlOptions) {
        return new QResult(new DataValue(qRuntime.pop()), resultType);
    }

    @Override
    public int stackInput() {
        return 1;
    }

    @Override
    public int stackOutput() {
        return 0;
    }
}
