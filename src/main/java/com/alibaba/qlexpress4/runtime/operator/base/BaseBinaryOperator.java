package com.alibaba.qlexpress4.runtime.operator.base;

import java.util.Objects;
import java.util.Optional;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.exception.QLRuntimeException;
import com.alibaba.qlexpress4.runtime.LeftValue;
import com.alibaba.qlexpress4.runtime.Value;
import com.alibaba.qlexpress4.runtime.operator.BinaryOperator;
import com.alibaba.qlexpress4.runtime.operator.number.NumberMath;

/**
 * @author bingo
 */
public abstract class BaseBinaryOperator implements BinaryOperator {
    protected boolean isSameType(Value left, Value right) {
        return left.getTypeName() != null && right.getTypeName() != null
            && Objects.equals(left.getTypeName(), right.getTypeName());
    }

    protected boolean isInstanceofComparable(Value value) {
        return value.get() instanceof Comparable;
    }

    protected boolean isBothBoolean(Value left, Value right) {
        return left.get() instanceof Boolean && right.get() instanceof Boolean;
    }

    protected boolean isBooleanAndNull(Value left, Value right) {
        return (left.get() == null && right.get() instanceof Boolean)
            || (left.get() instanceof Boolean && right.get() == null);
    }

    protected boolean isBothNumber(Value left, Value right) {
        return left.get() instanceof Number && right.get() instanceof Number;
    }

    protected boolean isNumberCharacter(Object leftValue, Object rightValue) {
        return (leftValue instanceof Character && rightValue instanceof Number)
            || (leftValue instanceof Number && rightValue instanceof Character);
    }

    protected boolean isCharacter(Object value) {
        return value instanceof Character;
    }

    protected boolean isNumber(Object value) {
        return value instanceof Number;
    }

    protected void assertLeftValue(Value left, ErrorReporter errorReporter) {
        if (!(left instanceof LeftValue)) {
            throw errorReporter.report("INVALID_ASSIGNMENT", "value on the left side of '=' is not assignable");
        }
    }

    protected Object plus(Value left, Value right, ErrorReporter errorReporter) {
        Object leftValue = left.get();
        Object rightValue = right.get();

        if (leftValue instanceof String) {
            return (String)leftValue + rightValue;
        }

        if (rightValue instanceof String) {
            return leftValue + (String)rightValue;
        }

        if (isBothNumber(left, right)) {
            return NumberMath.add((Number)leftValue, (Number)rightValue);
        }

        throw buildInvalidOperandTypeException(left, right, errorReporter);
    }

    protected Object minus(Value left, Value right, ErrorReporter errorReporter) {
        Object leftValue = left.get();
        Object rightValue = right.get();

        if (isBothNumber(left, right)) {
            return NumberMath.subtract((Number)leftValue, (Number)rightValue);
        }

        throw buildInvalidOperandTypeException(left, right, errorReporter);
    }

    protected Object multiply(Value left, Value right, ErrorReporter errorReporter) {
        Object leftValue = left.get();
        Object rightValue = right.get();

        if (isBothNumber(left, right)) {
            return NumberMath.multiply((Number)leftValue, (Number)rightValue);
        }

        throw buildInvalidOperandTypeException(left, right, errorReporter);
    }

    protected Object divide(Value left, Value right, QLOptions qlOptions, ErrorReporter errorReporter) {
        Object leftValue = left.get();
        Object rightValue = right.get();

        if (isBothNumber(left, right)) {
            try {
                if (qlOptions.isPrecise()) {
                    return NumberMath.toBigDecimal((Number)leftValue)
                        .divide(NumberMath.toBigDecimal((Number)rightValue));
                } else {
                    return NumberMath.divide((Number)leftValue, (Number)rightValue);
                }
            } catch (ArithmeticException arithmeticException) {
                throw errorReporter.report(arithmeticException, "INVALID_ARITHMETIC", arithmeticException.getMessage());
            }
        }

        throw buildInvalidOperandTypeException(left, right, errorReporter);
    }

    protected Object mod(Value left, Value right, QLOptions qlOptions, ErrorReporter errorReporter) {
        Object leftValue = left.get();
        Object rightValue = right.get();

        if (isBothNumber(left, right)) {
            if (qlOptions.isPrecise()) {
                return NumberMath.mod(NumberMath.toBigDecimal((Number)leftValue),
                    NumberMath.toBigDecimal((Number)rightValue));
            } else {
                return NumberMath.mod((Number)leftValue, (Number)rightValue);
            }
        }

        throw buildInvalidOperandTypeException(left, right, errorReporter);
    }

    protected Object bitwiseAnd(Value left, Value right, ErrorReporter errorReporter) {
        if (isBothBoolean(left, right) || isBooleanAndNull(left, right)) {
            return (Boolean)Optional.ofNullable(left.get()).orElse(Boolean.FALSE)
                & (Boolean)Optional.ofNullable(right.get()).orElse(Boolean.FALSE);
        }

        if (isBothNumber(left, right)) {
            Number leftValue = (Number)left.get();
            Number rightValue = (Number)right.get();
            return NumberMath.and(leftValue, rightValue);
        }

        throw buildInvalidOperandTypeException(left, right, errorReporter);
    }

    protected Object bitwiseOr(Value left, Value right, ErrorReporter errorReporter) {
        if (isBothBoolean(left, right) || isBooleanAndNull(left, right)) {
            return (Boolean)Optional.ofNullable(left.get()).orElse(Boolean.FALSE)
                | (Boolean)Optional.ofNullable(right.get()).orElse(Boolean.FALSE);
        }

        if (isBothNumber(left, right)) {
            Number leftValue = (Number)left.get();
            Number rightValue = (Number)right.get();
            return NumberMath.or(leftValue, rightValue);
        }

        throw buildInvalidOperandTypeException(left, right, errorReporter);
    }

    protected Object bitwiseXor(Value left, Value right, ErrorReporter errorReporter) {
        if (isBothBoolean(left, right) || isBooleanAndNull(left, right)) {
            return (Boolean)Optional.ofNullable(left.get()).orElse(Boolean.FALSE)
                ^ (Boolean)Optional.ofNullable(right.get()).orElse(Boolean.FALSE);
        }

        if (isBothNumber(left, right)) {
            Number leftValue = (Number)left.get();
            Number rightValue = (Number)right.get();
            return NumberMath.xor(leftValue, rightValue);
        }

        throw buildInvalidOperandTypeException(left, right, errorReporter);
    }

    protected Object leftShift(Value left, Value right, ErrorReporter errorReporter) {
        if (isBothNumber(left, right)) {
            Number leftValue = (Number)left.get();
            Number rightValue = (Number)right.get();
            return NumberMath.leftShift(leftValue, rightValue);
        }

        throw buildInvalidOperandTypeException(left, right, errorReporter);
    }

    protected Object rightShift(Value left, Value right, ErrorReporter errorReporter) {
        if (isBothNumber(left, right)) {
            Number leftValue = (Number)left.get();
            Number rightValue = (Number)right.get();
            return NumberMath.rightShift(leftValue, rightValue);
        }

        throw buildInvalidOperandTypeException(left, right, errorReporter);
    }

    protected Object rightShiftUnsigned(Value left, Value right, ErrorReporter errorReporter) {
        if (isBothNumber(left, right)) {
            Number leftValue = (Number)left.get();
            Number rightValue = (Number)right.get();
            return NumberMath.rightShiftUnsigned(leftValue, rightValue);
        }

        throw buildInvalidOperandTypeException(left, right, errorReporter);
    }

    /**
     * 参考groovy
     * org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation#compareEqual
     * org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation#compareToWithEqualityCheck
     *
     * @param left
     * @param right
     * @param errorReporter
     * @return
     */
    protected int compare(Value left, Value right, ErrorReporter errorReporter) {
        if (Objects.equals(left.get(), right.get())) {
            return 0;
        }

        // null TODO 参考 groovy
        if (isSameType(left, right) && isInstanceofComparable(left)) {
            return ((Comparable)(left.get())).compareTo(right.get());
        }

        // TODO 两个都实现Comparable接口，参考groovy
        if (isBothNumber(left, right)) {
            return NumberMath.compareTo((Number)left.get(), (Number)right.get());
        }

        throw buildInvalidOperandTypeException(left, right, errorReporter);
    }

    protected boolean equals(Value left, Value right) {
        Object leftValue = left.get();
        Object rightValue = right.get();
        if (isBothNumber(left, right)) {
            return NumberMath.compareTo((Number)leftValue, (Number)rightValue) == 0;
        } else if (isNumberCharacter(leftValue, rightValue)) {
            if (isNumber(leftValue)) {
                return NumberMath.compareTo((Number)leftValue, (int)(Character)rightValue) == 0;
            } else {
                return NumberMath.compareTo((int)(Character)leftValue, (Number)rightValue) == 0;
            }
        } else {
            return Objects.equals(leftValue, rightValue);
        }
    }

    protected QLRuntimeException buildInvalidOperandTypeException(Value left, Value right,
        ErrorReporter errorReporter) {
        return errorReporter.reportFormat("INVALID_OPERAND_TYPE",
            "Cannot use %s operator on leftType:%s with leftValue:%s and rightType:%s with rightValue:%s",
            getOperator(), left.getTypeName(), left.get(), right.getTypeName(), right.get());
    }
}