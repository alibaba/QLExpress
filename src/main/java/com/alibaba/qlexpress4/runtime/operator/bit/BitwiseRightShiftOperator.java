package com.alibaba.qlexpress4.runtime.operator.bit;

import com.alibaba.qlexpress4.QLPrecedences;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.runtime.Value;
import com.alibaba.qlexpress4.runtime.operator.base.BaseBinaryOperator;

/**
 * @author 冰够
 */
public class BitwiseRightShiftOperator extends BaseBinaryOperator {
    private static final BitwiseRightShiftOperator INSTANCE = new BitwiseRightShiftOperator();

    private BitwiseRightShiftOperator() {
    }

    @Override
    public String getOperator() {
        return ">>";
    }

    @Override
    public int getPriority() {
        return QLPrecedences.BIT_MOVE;
    }

    @Override
    public Object execute(Value left, Value right, ErrorReporter errorReporter) {
        return rightShift(left, right, errorReporter);
    }

    public static BitwiseRightShiftOperator getInstance() {
        return INSTANCE;
    }
}
