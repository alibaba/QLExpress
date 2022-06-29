package com.alibaba.qlexpress4.runtime.operator.arithmetic;

import java.util.Objects;

import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.runtime.Value;
import com.alibaba.qlexpress4.runtime.operator.base.BaseBinaryOperator;
import com.alibaba.qlexpress4.runtime.operator.constant.OperatorPriority;
import com.alibaba.qlexpress4.runtime.operator.number.NumberMath;

/**
 * QLExpress只支持String和Number类型的+
 *
 * @author 冰够
 */
public class PlusOperator extends BaseBinaryOperator {
    @Override
    public Object execute(Value left, Value right, ErrorReporter errorReporter) {
        Object leftValue = left.get();
        Object rightValue = right.get();

        if (leftValue instanceof String || Objects.equals(left.getDefineType(), String.class)) {
            return (String)leftValue + rightValue;
        }

        if (rightValue instanceof String || Objects.equals(right.getDefineType(), String.class)) {
            return leftValue + (String)rightValue;
        }

        if (isBothNumbers(left, right)) {
            return NumberMath.add((Number)leftValue, (Number)rightValue);
        }

        throw buildInvalidOperandTypeException(left, right, errorReporter);
    }

    @Override
    public String getOperator() {
        return "+";
    }

    @Override
    public int getPriority() {
        return OperatorPriority.PRIORITY_11;
    }
}
