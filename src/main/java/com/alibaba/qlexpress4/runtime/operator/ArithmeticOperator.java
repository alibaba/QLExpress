package com.alibaba.qlexpress4.runtime.operator;

import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.runtime.Value;
import com.alibaba.qlexpress4.runtime.operator.base.BaseOperator;
import com.alibaba.qlexpress4.runtime.operator.number.NumberMath;

/**
 * @author 冰够
 */
public class ArithmeticOperator extends BaseOperator {
    public ArithmeticOperator(String operator) {
        super(operator);
    }

    @Override
    public Object execute(Value left, Value right, ErrorReporter errorReporter) {
        Object leftValue = left.get();
        Object rightValue = right.get();
        // TODO bingo
        switch (operator) {
            case "+":
                return NumberMath.add((Number)leftValue, (Number)rightValue);
            case "-":
                return NumberMath.subtract((Number)leftValue, (Number)rightValue);
            default:
                throw buildInvalidOperandTypeException(left, right, errorReporter);
        }
    }

    @Override
    public int getPrecedence() {
        return 0;
    }
}
