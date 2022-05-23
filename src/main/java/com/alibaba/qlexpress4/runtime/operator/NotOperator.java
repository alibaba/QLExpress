package com.alibaba.qlexpress4.runtime.operator;

import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.runtime.Value;
import com.alibaba.qlexpress4.runtime.operator.base.BaseUnaryOperator;

/**
 * @author 冰够
 */
public class NotOperator extends BaseUnaryOperator {
    public NotOperator() {
        super("!");
    }

    @Override
    public Object execute(Value value, ErrorReporter errorReporter) {
        Object operand = value.get();
        if (operand == null) {
            operand = false;
        }
        if (!(operand instanceof Boolean)) {
            throw buildInvalidOperandTypeException(value, errorReporter);
        }
        return !(Boolean)operand;
    }
}
