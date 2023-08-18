package com.alibaba.qlexpress4.runtime.operator.bit;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.QLPrecedences;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.runtime.QRuntime;
import com.alibaba.qlexpress4.runtime.Value;
import com.alibaba.qlexpress4.runtime.operator.base.BaseBinaryOperator;

/**
 * @author bingo
 */
public class BitwiseRightShiftUnsignedOperator extends BaseBinaryOperator {
    private static final BitwiseRightShiftUnsignedOperator INSTANCE = new BitwiseRightShiftUnsignedOperator();

    private BitwiseRightShiftUnsignedOperator() {
    }

    public static BitwiseRightShiftUnsignedOperator getInstance() {
        return INSTANCE;
    }

    @Override
    public String getOperator() {
        return ">>>";
    }

    @Override
    public int getPriority() {
        return QLPrecedences.BIT_MOVE;
    }

    @Override
    public Object execute(Value left, Value right, QRuntime qRuntime, QLOptions qlOptions,
        ErrorReporter errorReporter) {
        return rightShiftUnsigned(left, right, errorReporter);
    }
}
