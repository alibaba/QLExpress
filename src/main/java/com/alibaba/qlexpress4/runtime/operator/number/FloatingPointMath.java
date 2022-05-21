package com.alibaba.qlexpress4.runtime.operator.number;

/**
 * @author 冰够
 */
public class FloatingPointMath extends NumberMath {

    public static final FloatingPointMath INSTANCE = new FloatingPointMath();

    private FloatingPointMath() {}

    @Override
    protected Number absImpl(Number number) {
        return Math.abs(number.doubleValue());
    }

    @Override
    public Number addImpl(Number left, Number right) {
        return left.doubleValue() + right.doubleValue();
    }

    @Override
    public Number subtractImpl(Number left, Number right) {
        return left.doubleValue() - right.doubleValue();
    }

    @Override
    public Number multiplyImpl(Number left, Number right) {
        return left.doubleValue() * right.doubleValue();
    }

    @Override
    public Number divideImpl(Number left, Number right) {
        return left.doubleValue() / right.doubleValue();
    }

    @Override
    public int compareToImpl(Number left, Number right) {
        return Double.compare(left.doubleValue(), right.doubleValue());
    }

    @Override
    protected Number modImpl(Number left, Number right) {
        return left.doubleValue() % right.doubleValue();
    }

    @Override
    protected Number unaryMinusImpl(Number left) {
        return -left.doubleValue();
    }

    @Override
    protected Number unaryPlusImpl(Number left) {
        return left.doubleValue();
    }
}
