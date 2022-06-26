package com.alibaba.qlexpress4.runtime.instruction;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.runtime.QResult;
import com.alibaba.qlexpress4.runtime.QRuntime;
import com.alibaba.qlexpress4.runtime.data.DataValue;

/**
 * @Operation: push constObj to stack
 * @Input: 0
 * @Output: 1
 * <p>
 * Author: DQinYuan
 */
public class ConstInstruction extends QLInstruction {

    private final Object constObj;

    public ConstInstruction(ErrorReporter errorReporter, Object constObj) {
        super(errorReporter);
        this.constObj = constObj;
    }

    @Override
    public QResult execute(QRuntime qRuntime, QLOptions qlOptions) {
        qRuntime.push(new DataValue(constObj));
        return QResult.CONTINUE_RESULT;
    }

    @Override
    public int stackInput() {
        return 0;
    }

    @Override
    public int stackOutput() {
        return 1;
    }
}
