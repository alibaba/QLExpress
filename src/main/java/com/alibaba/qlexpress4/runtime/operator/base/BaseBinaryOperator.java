package com.alibaba.qlexpress4.runtime.operator.base;

import java.util.Objects;
import java.util.Optional;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.exception.QLErrorCodes;
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

    protected boolean isNumberCharacter(Value left, Value right) {
        return (left.get() instanceof Character && right.get() instanceof Number)
            || (left.get() instanceof Number && right.get() instanceof Character);
    }

    protected boolean isNumber(Value value) {
        return value.get() instanceof Number;
    }

    protected void assertLeftValue(Value left, ErrorReporter errorReporter) {
        if (!(left instanceof LeftValue)) {
            throw errorReporter.reportFormat(QLErrorCodes.INVALID_ASSIGNMENT.name(),
                    QLErrorCodes.INVALID_ASSIGNMENT.getErrorMsg(), "on the left side");
        }
    }

    protected Object plus(Value left, Value right, QLOptions qlOptions, ErrorReporter errorReporter) {
        Object leftValue = left.get();
        Object rightValue = right.get();

        if (leftValue instanceof String) {
            return (String)leftValue + rightValue;
        }

        if (rightValue instanceof String) {
            return leftValue + (String)rightValue;
        }

        if (isBothNumber(left, right)) {
            if (qlOptions.isPrecise()) {
                return NumberMath.add(NumberMath.toBigDecimal((Number)leftValue),
                    NumberMath.toBigDecimal((Number)rightValue));
            } else {
                return NumberMath.add((Number)leftValue, (Number)rightValue);
            }
        }

        throw buildInvalidOperandTypeException(left, right, errorReporter);
    }

    protected Object minus(Value left, Value right, QLOptions qlOptions, ErrorReporter errorReporter) {
        Object leftValue = left.get();
        Object rightValue = right.get();

        if (isBothNumber(left, right)) {
            if (qlOptions.isPrecise()) {
                return NumberMath.subtract(NumberMath.toBigDecimal((Number)leftValue),
                    NumberMath.toBigDecimal((Number)rightValue));
            } else {
                return NumberMath.subtract((Number)leftValue, (Number)rightValue);
            }
        }

        throw buildInvalidOperandTypeException(left, right, errorReporter);
    }

    protected Object multiply(Value left, Value right, QLOptions qlOptions, ErrorReporter errorReporter) {
        Object leftValue = left.get();
        Object rightValue = right.get();

        if (isBothNumber(left, right)) {
            if (qlOptions.isPrecise()) {
                return NumberMath.multiply(NumberMath.toBigDecimal((Number)leftValue),
                    NumberMath.toBigDecimal((Number)rightValue));
            } else {
                return NumberMath.multiply((Number)leftValue, (Number)rightValue);
            }
        }

        throw buildInvalidOperandTypeException(left, right, errorReporter);
    }

    protected Object divide(Value left, Value right, QLOptions qlOptions, ErrorReporter errorReporter) {
        Object leftValue = left.get();
        Object rightValue = right.get();

        if (isBothNumber(left, right)) {
            try {
                if (qlOptions.isPrecise()) {
                    return NumberMath.divide(NumberMath.toBigDecimal((Number)leftValue),
                        NumberMath.toBigDecimal((Number)rightValue));
                } else {
                    return NumberMath.divide((Number)leftValue, (Number)rightValue);
                }
            } catch (ArithmeticException arithmeticException) {
                throw errorReporter.report(arithmeticException, QLErrorCodes.INVALID_ARITHMETIC.name(), arithmeticException.getMessage());
            }
        }

        throw buildInvalidOperandTypeException(left, right, errorReporter);
    }

    protected Object remainder(Value left, Value right, QLOptions qlOptions, ErrorReporter errorReporter) {
        Object leftValue = left.get();
        Object rightValue = right.get();

        if (isBothNumber(left, right)) {
            if (qlOptions.isPrecise()) {
                return NumberMath.remainder(NumberMath.toBigDecimal((Number)leftValue),
                    NumberMath.toBigDecimal((Number)rightValue));
            } else {
                return NumberMath.remainder((Number)leftValue, (Number)rightValue);
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

    protected int compare(Value left, Value right, ErrorReporter errorReporter) {
        if (Objects.equals(left.get(), right.get())) {
            return 0;
        }

        if (isBothNumber(left, right)) {
            return NumberMath.compareTo((Number)left.get(), (Number)right.get());
        }

        if (isNumberCharacter(left, right)) {
            if (isNumber(left)) {
                return NumberMath.compareTo((Number)left.get(), (int)(Character)right.get());
            } else {
                return NumberMath.compareTo((int)(Character)left.get(), (Number)right.get());
            }
        }

        if (isSameType(left, right) && isInstanceofComparable(left)) {
            return ((Comparable)(left.get())).compareTo(right.get());
        }

        throw buildInvalidOperandTypeException(left, right, errorReporter);
    }

    protected boolean equals(Value left, Value right, ErrorReporter errorReporter) {
        Object leftValue = left.get();
        Object rightValue = right.get();
        if (isBothNumber(left, right) || isNumberCharacter(left, right)
            || (isSameType(left, right) && isInstanceofComparable(left))) {
            return compare(left, right, errorReporter) == 0;
        } else {
            return Objects.equals(leftValue, rightValue);
        }
    }

    protected QLRuntimeException buildInvalidOperandTypeException(Value left, Value right,
        ErrorReporter errorReporter) {
        return errorReporter.reportFormat(QLErrorCodes.INVALID_BINARY_OPERAND.name(),
            QLErrorCodes.INVALID_BINARY_OPERAND.getErrorMsg(),
            getOperator(), left.getTypeName(), left.get(), right.getTypeName(), right.get());
    }
}