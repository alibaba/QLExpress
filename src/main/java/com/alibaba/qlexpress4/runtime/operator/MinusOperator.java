package com.alibaba.qlexpress4.runtime.operator;

import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.runtime.Value;
import com.alibaba.qlexpress4.runtime.operator.base.BaseOperator;
import com.alibaba.qlexpress4.runtime.operator.number.NumberMath;

/**
 * @author 冰够
 */
public class MinusOperator extends BaseOperator {
    public MinusOperator() {
        super("-");
    }

    @Override
    public Object execute(Value left, Value right, ErrorReporter errorReporter) {
        Object leftValue = left.get();
        Object rightValue = right.get();

        if (isBothNumbers(left, right)) {
            return NumberMath.subtract((Number)leftValue, (Number)rightValue);
        }

        throw buildInvalidOperandTypeException(left, right, errorReporter);
    }

    @Override
    public int getPrecedence() {
        return 0;
    }
}
