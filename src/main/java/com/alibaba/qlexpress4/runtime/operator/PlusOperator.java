package com.alibaba.qlexpress4.runtime.operator;

import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.runtime.Value;
import com.alibaba.qlexpress4.runtime.operator.base.BaseOperator;
import com.alibaba.qlexpress4.runtime.operator.number.NumberMath;

/**
 * QLExpress只支持String和Number类型的+
 *
 * @author 冰够
 */
public class PlusOperator extends BaseOperator {
    public PlusOperator() {
        super("+");
    }

    @Override
    public Object execute(Value left, Value right, ErrorReporter errorReporter) {
        // TODO 都为null，抛异常？ com.ql.util.express.OperatorOfNumber.add
        Object leftValue = left.get();
        Object rightValue = right.get();

        if (leftValue instanceof String) {
            return (String)leftValue + rightValue;
        }

        if (rightValue instanceof String) {
            return leftValue + (String)rightValue;
        }

        if (isBothNumbers(left, right)) {
            return NumberMath.add((Number)leftValue, (Number)rightValue);
        }

        throw buildInvalidOperandTypeException(left, right, errorReporter);
    }

    @Override
    public int getPrecedence() {
        return 0;
    }
}
