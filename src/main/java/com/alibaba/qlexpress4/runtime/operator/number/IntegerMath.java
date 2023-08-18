package com.alibaba.qlexpress4.runtime.operator.number;

/**
 * @author bingo
 */
public class IntegerMath extends NumberMath {
    public static final IntegerMath INSTANCE = new IntegerMath();

    private IntegerMath() {}

    @Override
    protected Number absImpl(Number number) {
        return Math.abs(number.intValue());
    }

    @Override
    public Number addImpl(Number left, Number right) {
        return left.intValue() + right.intValue();
    }

    @Override
    public Number subtractImpl(Number left, Number right) {
        return left.intValue() - right.intValue();
    }

    @Override
    public Number multiplyImpl(Number left, Number right) {
        return left.intValue() * right.intValue();
    }

    @Override
    public Number divideImpl(Number left, Number right) {
        return BigDecimalMath.INSTANCE.divideImpl(left, right);
    }

    @Override
    public int compareToImpl(Number left, Number right) {
        int leftVal = left.intValue();
        int rightVal = right.intValue();
        return Integer.compare(leftVal, rightVal);
    }

    @Override
    protected Number orImpl(Number left, Number right) {
        return left.intValue() | right.intValue();
    }

    @Override
    protected Number andImpl(Number left, Number right) {
        return left.intValue() & right.intValue();
    }

    @Override
    protected Number xorImpl(Number left, Number right) {
        return left.intValue() ^ right.intValue();
    }

    @Override
    protected Number intDivImpl(Number left, Number right) {
        return left.intValue() / right.intValue();
    }

    @Override
    protected Number modImpl(Number left, Number right) {
        return left.intValue() % right.intValue();
    }

    @Override
    protected Number unaryMinusImpl(Number left) {
        return -left.intValue();
    }

    @Override
    protected Number unaryPlusImpl(Number left) {
        return left.intValue();
    }

    @Override
    protected Number bitwiseNegateImpl(Number left) {
        return ~left.intValue();
    }

    @Override
    protected Number leftShiftImpl(Number left, Number right) {
        return left.intValue() << right.intValue();
    }

    @Override
    protected Number rightShiftImpl(Number left, Number right) {
        return left.intValue() >> right.intValue();
    }

    @Override
    protected Number rightShiftUnsignedImpl(Number left, Number right) {
        return left.intValue() >>> right.intValue();
    }
}
