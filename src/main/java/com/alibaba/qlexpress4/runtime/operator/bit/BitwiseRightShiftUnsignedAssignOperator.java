package com.alibaba.qlexpress4.runtime.operator.bit;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.QLPrecedences;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.runtime.LeftValue;
import com.alibaba.qlexpress4.runtime.Value;
import com.alibaba.qlexpress4.runtime.operator.base.BaseBinaryOperator;

/**
 * @author 冰够
 */
public class BitwiseRightShiftUnsignedAssignOperator extends BaseBinaryOperator {
    private static final BitwiseRightShiftUnsignedAssignOperator INSTANCE
        = new BitwiseRightShiftUnsignedAssignOperator();

    private BitwiseRightShiftUnsignedAssignOperator() {
    }

    public static BitwiseRightShiftUnsignedAssignOperator getInstance() {
        return INSTANCE;
    }

    @Override
    public Object execute(Value left, Value right, QLOptions qlOptions, ErrorReporter errorReporter) {
        assertLeftValue(left, errorReporter);
        LeftValue leftValue = (LeftValue)left;
        Object result = rightShiftUnsigned(left, right, errorReporter);
        leftValue.set(result, errorReporter);
        return result;
    }

    @Override
    public String getOperator() {
        return ">>>=";
    }

    @Override
    public int getPriority() {
        return QLPrecedences.ASSIGN;
    }
}
