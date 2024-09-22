package com.alibaba.qlexpress4.runtime.instruction;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.exception.QLErrorCodes;
import com.alibaba.qlexpress4.runtime.*;
import com.alibaba.qlexpress4.runtime.data.DataValue;
import com.alibaba.qlexpress4.utils.PrintlnUtils;

import java.util.function.Consumer;

/**
 * Operation: get specified field of object on the top of stack
 * Input: 1
 * Output: 1
 * <p>
 * Author: DQinYuan
 */
public class GetFieldInstruction extends QLInstruction {

    private final String fieldName;

    private final boolean optional;

    public GetFieldInstruction(ErrorReporter errorReporter, String fieldName, boolean optional) {
        super(errorReporter);
        this.fieldName = fieldName;
        this.optional = optional;
    }

    @Override
    public QResult execute(QContext qContext, QLOptions qlOptions) {
        Object bean = qContext.pop().get();
        if (bean == null) {
            if (qlOptions.isAvoidNullPointer() || optional) {
                qContext.push(DataValue.NULL_VALUE);
                return QResult.NEXT_INSTRUCTION;
            }
            throw errorReporter.report(new NullPointerException(),
                    QLErrorCodes.NULL_FIELD_ACCESS.name(), QLErrorCodes.NULL_FIELD_ACCESS.getErrorMsg());
        }
        Value fieldValue = qContext.getReflectLoader().loadField(bean, fieldName, false, errorReporter);
        if (fieldValue == null) {
            throw errorReporter.reportFormat(QLErrorCodes.FIELD_NOT_FOUND.name(),
                    QLErrorCodes.FIELD_NOT_FOUND.getErrorMsg(), fieldName);
        }
        qContext.push(fieldValue);
        return QResult.NEXT_INSTRUCTION;
    }

    @Override
    public int stackInput() {
        return 1;
    }

    @Override
    public int stackOutput() {
        return 1;
    }

    @Override
    public void println(int index, int depth, Consumer<String> debug) {
        PrintlnUtils.printlnByCurDepth(depth, index + ": GetField " + fieldName, debug);
    }

    public String getFieldName() {
        return fieldName;
    }

}
