package com.alibaba.qlexpress4.runtime.operator;

import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.runtime.Value;
import com.alibaba.qlexpress4.runtime.operator.base.BaseOperator;
import com.alibaba.qlexpress4.runtime.operator.number.NumberMath;

/**
 * 位运算操作符
 *
 * @author 冰够
 */
public class BitOperator extends BaseOperator {
    public BitOperator(String operator) {
        super(operator);
    }

    @Override
    public Object execute(Value left, Value right, ErrorReporter errorReporter) {
        if (!isBothNumbers(left, right)) {
            throw buildInvalidOperandTypeException(left, right, errorReporter);
        }

        Number leftValue = (Number)left.get();
        Number rightValue = (Number)right.get();
        try {
            switch (operator) {
                case "&":
                    return NumberMath.and(leftValue, rightValue);
                case "|":
                    return NumberMath.or(leftValue, rightValue);
                case "^":
                    return NumberMath.xor(leftValue, rightValue);
                case "<<":
                    return NumberMath.leftShift(leftValue, rightValue);
                case ">>":
                    return NumberMath.rightShift(leftValue, rightValue);
                case ">>>":
                    return NumberMath.rightShiftUnsigned(leftValue, rightValue);
                default:
                    throw buildInvalidOperandTypeException(left, right, errorReporter);
            }
        } catch (UnsupportedOperationException e) {
            throw errorReporter.report("InvalidOperandType", e.getMessage());
        }
    }

    @Override
    public int getPrecedence() {
        return 0;
    }
}
