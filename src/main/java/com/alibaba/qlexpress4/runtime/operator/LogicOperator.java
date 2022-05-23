package com.alibaba.qlexpress4.runtime.operator;

import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.runtime.Value;
import com.alibaba.qlexpress4.runtime.operator.base.BaseOperator;

/**
 * TODO bingo null 如何处理？
 *
 * @author 冰够
 */
public class LogicOperator extends BaseOperator {
    public LogicOperator(String operator) {
        super(operator);
    }

    @Override
    public Object execute(Value left, Value right, ErrorReporter errorReporter) {
        Object leftValue = left.get();
        Object rightValue = right.get();
        if (leftValue == null) {
            leftValue = false;
        }
        if (rightValue == null) {
            rightValue = false;
        }

        if (!(leftValue instanceof Boolean) || !(rightValue instanceof Boolean)) {
            throw buildInvalidOperandTypeException(left, right, errorReporter);
        }

        switch (operator) {
            case "&&":
            case "and":
                return (Boolean)leftValue && (Boolean)rightValue;
            case "||":
            case "or":
                return (Boolean)leftValue || (Boolean)rightValue;
            default:
                throw buildInvalidOperandTypeException(left, right, errorReporter);
        }
    }

    @Override
    public int getPrecedence() {
        return 0;
    }
}
