package com.alibaba.qlexpress4.runtime.instruction;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.runtime.QResult;
import com.alibaba.qlexpress4.runtime.Parameters;
import com.alibaba.qlexpress4.runtime.QRuntime;
import com.alibaba.qlexpress4.runtime.Value;
import com.alibaba.qlexpress4.runtime.data.DataValue;
import com.alibaba.qlexpress4.utils.BasicUtil;
import com.alibaba.qlexpress4.utils.PrintlnUtils;

import java.util.function.Consumer;

/**
 * @Operation: force cast value to specified type
 * @Input: 2 targetCls and value
 * @Output: 1 casted value
 *
 * Author: DQinYuan
 */
public class CastInstruction extends QLInstruction {

    public CastInstruction(ErrorReporter errorReporter) {
        super(errorReporter);
    }

    @Override
    public QResult execute(QRuntime qRuntime, QLOptions qlOptions) {
        Object value = qRuntime.pop().get();
        Class<?> targetClz = (Class<?>) qRuntime.pop().get();
        try {
            if (value == null) {
                qRuntime.push(Value.NULL_VALUE);
                return QResult.CONTINUE_RESULT;
            }
            Object targetValue = BasicUtil.castObject(value, targetClz, true);
            Value dataCast = new DataValue(targetValue);
            qRuntime.push(dataCast);
        } catch (Exception e) {
            throw errorReporter.report("CAST_VALUE_ERROR", "can not cast from this class");
        }
        return QResult.CONTINUE_RESULT;
    }

    @Override
    public int stackInput() {
        return 2;
    }

    @Override
    public int stackOutput() {
        return 1;
    }

    @Override
    public void println(int depth, Consumer<String> debug) {
        PrintlnUtils.printlnByCurDepth(depth, "Cast", debug);
    }
}
