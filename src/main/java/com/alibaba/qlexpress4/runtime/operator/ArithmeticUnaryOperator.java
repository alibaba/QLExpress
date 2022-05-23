package com.alibaba.qlexpress4.runtime.operator;

import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.runtime.Value;
import com.alibaba.qlexpress4.runtime.operator.base.BaseUnaryOperator;
import com.alibaba.qlexpress4.runtime.operator.number.NumberMath;

/**
 * @author 冰够
 */
public class ArithmeticUnaryOperator extends BaseUnaryOperator {
    public ArithmeticUnaryOperator(String operator) {
        super(operator);
    }

    @Override
    public Object execute(Value value, ErrorReporter errorReporter) {
        Object operand = value.get();
        if (!NumberMath.isNumber(operand)) {
            throw buildInvalidOperandTypeException(value, errorReporter);
        }

        Number number = (Number)operand;
        switch (operator) {
            case "+":
                return NumberMath.unaryPlus(number);
            case "-":
                return NumberMath.unaryMinus(number);
            case "++":
                return NumberMath.add(number, 1);
            case "--":
                return NumberMath.subtract(number, 1);
            default:
                throw buildInvalidOperandTypeException(value, errorReporter);
        }
    }
}
