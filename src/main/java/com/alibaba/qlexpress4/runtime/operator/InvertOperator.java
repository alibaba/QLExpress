package com.alibaba.qlexpress4.runtime.operator;

import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.runtime.Value;
import com.alibaba.qlexpress4.runtime.operator.base.BaseUnaryOperator;
import com.alibaba.qlexpress4.runtime.operator.number.NumberMath;

/**
 * @author 冰够
 */
public class InvertOperator extends BaseUnaryOperator {
    public InvertOperator() {
        super("~");
    }

    @Override
    public Object execute(Value value, ErrorReporter errorReporter) {
        Object operand = value.get();
        if (!NumberMath.isNumber(operand)) {
            throw buildInvalidOperandTypeException(value, errorReporter);
        }

        return NumberMath.bitwiseNegate((Number)value);
    }
}
