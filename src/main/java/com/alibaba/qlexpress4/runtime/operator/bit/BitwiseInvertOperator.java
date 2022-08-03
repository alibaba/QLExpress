package com.alibaba.qlexpress4.runtime.operator.bit;

import com.alibaba.qlexpress4.QLPrecedences;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.runtime.Value;
import com.alibaba.qlexpress4.runtime.operator.base.BaseUnaryOperator;
import com.alibaba.qlexpress4.runtime.operator.number.NumberMath;

/**
 * @author 冰够
 */
public class BitwiseInvertOperator extends BaseUnaryOperator {
    @Override
    public Object execute(Value value, ErrorReporter errorReporter) {
        Object operand = value.get();
        if (!NumberMath.isNumber(operand)) {
            throw buildInvalidOperandTypeException(value, errorReporter);
        }

        return NumberMath.bitwiseNegate((Number)value);
    }

    @Override
    public String getOperator() {
        return "~";
    }

    @Override
    public int getPriority() {
        return QLPrecedences.UNARY;
    }
}
