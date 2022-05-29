package com.alibaba.qlexpress4.runtime.operator.base;

import java.util.Objects;

import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.exception.QLRuntimeException;
import com.alibaba.qlexpress4.runtime.Value;
import com.alibaba.qlexpress4.runtime.operator.Operator;

/**
 * @author 冰够
 */
public abstract class BaseOperator implements Operator {
    protected String operator;

    public BaseOperator(String operator) {
        this.operator = operator;
    }

    protected boolean isSameType(Value left, Value right) {
        return left.getType() != null && right.getType() != null && Objects.equals(left.getType(), right.getType());
    }

    protected boolean instanceofComparable(Value value) {
        return value.get() instanceof Comparable;
    }

    protected boolean isBothNumbers(Value left, Value right) {
        return left.get() instanceof Number && right.get() instanceof Number;
    }

    protected QLRuntimeException buildInvalidOperandTypeException(Value left, Value right,
        ErrorReporter errorReporter) {
        return errorReporter.report("InvalidOperandType",
            "Cannot use %s operator on leftType:%s with leftValue:%s and rightType:%s with rightValue:%s",
            operator, left.getTypeName(), left.get(), right.getTypeName(), right.get());
    }
}
