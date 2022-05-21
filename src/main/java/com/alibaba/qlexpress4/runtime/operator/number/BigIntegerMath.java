package com.alibaba.qlexpress4.runtime.operator.number;

/**
 * @author 冰够
 */
public class BigIntegerMath extends NumberMath {
    public static final BigIntegerMath INSTANCE = new BigIntegerMath();

    private BigIntegerMath() {}

    @Override
    protected Number absImpl(Number number) {
        return toBigInteger(number).abs();
    }

    @Override
    public Number addImpl(Number left, Number right) {
        return toBigInteger(left).add(toBigInteger(right));
    }

    @Override
    public Number subtractImpl(Number left, Number right) {
        return toBigInteger(left).subtract(toBigInteger(right));
    }

    @Override
    public Number multiplyImpl(Number left, Number right) {
        return toBigInteger(left).multiply(toBigInteger(right));
    }

    @Override
    public Number divideImpl(Number left, Number right) {
        return BigDecimalMath.INSTANCE.divideImpl(left, right);
    }

    @Override
    public int compareToImpl(Number left, Number right) {
        return toBigInteger(left).compareTo(toBigInteger(right));
    }

    @Override
    protected Number intDivImpl(Number left, Number right) {
        return toBigInteger(left).divide(toBigInteger(right));
    }

    @Override
    protected Number modImpl(Number left, Number right) {
        return toBigInteger(left).mod(toBigInteger(right));
    }

    @Override
    protected Number unaryMinusImpl(Number left) {
        return toBigInteger(left).negate();
    }

    @Override
    protected Number unaryPlusImpl(Number left) {
        return toBigInteger(left);
    }

    @Override
    protected Number bitwiseNegateImpl(Number left) {
        return toBigInteger(left).not();
    }

    @Override
    protected Number orImpl(Number left, Number right) {
        return toBigInteger(left).or(toBigInteger(right));
    }

    @Override
    protected Number andImpl(Number left, Number right) {
        return toBigInteger(left).and(toBigInteger(right));
    }

    @Override
    protected Number xorImpl(Number left, Number right) {
        return toBigInteger(left).xor(toBigInteger(right));
    }

    @Override
    protected Number leftShiftImpl(Number left, Number right) {
        return toBigInteger(left).shiftLeft(right.intValue());
    }

    @Override
    protected Number rightShiftImpl(Number left, Number right) {
        return toBigInteger(left).shiftRight(right.intValue());
    }
}
