package com.alibaba.qlexpress4.runtime.instruction;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.runtime.*;
import com.alibaba.qlexpress4.runtime.data.DataValue;
import com.alibaba.qlexpress4.utils.PrintlnUtils;
import com.alibaba.qlexpress4.utils.PropertiesUtil;

import java.util.function.Consumer;

/**
 * @Operation: get specified field of object on the top of stack
 * @Input: 1
 * @Output: 1
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
                    "GET_FIELD_FROM_NULL", "can not get field from null");
        }
        Value fieldValue = qContext.getReflectLoader().loadField(bean, fieldName, errorReporter);
        if (fieldValue == null) {
            throw errorReporter.report("FIELD_NOT_FOUND", "'" + fieldName + "' not found");
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
    public void println(int depth, Consumer<String> debug) {
        PrintlnUtils.printlnByCurDepth(depth, "GetField " + fieldName, debug);
    }

    public String getFieldName() {
        return fieldName;
    }

}
