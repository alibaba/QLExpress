package com.alibaba.qlexpress4.runtime.operator.bit;

import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.runtime.Value;
import com.alibaba.qlexpress4.runtime.operator.base.BaseBinaryOperator;
import com.alibaba.qlexpress4.runtime.operator.number.NumberMath;

/**
 * @author 冰够
 */
public class BitwiseOrOperator extends BaseBinaryOperator {
    @Override
    public String getOperator() {
        return "|";
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public Object execute(Value left, Value right, ErrorReporter errorReporter) {
        if (!isBothNumbers(left, right)) {
            throw buildInvalidOperandTypeException(left, right, errorReporter);
        }

        Number leftValue = (Number)left.get();
        Number rightValue = (Number)right.get();
        // TODO 需要统一考虑下NumberMath抛出的异常如何处理
        return NumberMath.or(leftValue, rightValue);
    }
}
