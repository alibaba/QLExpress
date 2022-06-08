package com.alibaba.qlexpress4.runtime.operator.base;

import java.util.Objects;

import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.exception.QLRuntimeException;
import com.alibaba.qlexpress4.runtime.Value;
import com.alibaba.qlexpress4.runtime.operator.BinaryOperator;

/**
 * @author 冰够
 */
public abstract class BaseBinaryOperator implements BinaryOperator {
    protected boolean isSameType(Value left, Value right) {
        return left.getActualClassName() != null && right.getActualClassName() != null
            && Objects.equals(left.getActualClassName(), right.getActualClassName());
    }

    protected boolean isInstanceofComparable(Value value) {
        return value.get() instanceof Comparable;
    }

    protected boolean isBothNumbers(Value left, Value right) {
        return left.get() instanceof Number && right.get() instanceof Number;
    }

    protected QLRuntimeException buildInvalidOperandTypeException(Value left, Value right,
        ErrorReporter errorReporter) {
        // 错误码统一规范
        return errorReporter.report("InvalidOperandType",
            "Cannot use %s operator on leftType:%s with leftValue:%s and rightType:%s with rightValue:%s",
            getOperator(), left.getActualClassName(), left.get(), right.getActualClassName(), right.get());
    }
}
