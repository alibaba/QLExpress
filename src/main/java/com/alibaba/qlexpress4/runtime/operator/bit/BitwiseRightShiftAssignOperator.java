package com.alibaba.qlexpress4.runtime.operator.bit;

import com.alibaba.qlexpress4.QLPrecedences;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.runtime.LeftValue;
import com.alibaba.qlexpress4.runtime.Value;
import com.alibaba.qlexpress4.runtime.operator.base.BaseBinaryOperator;

/**
 * @author 冰够
 */
public class BitwiseRightShiftAssignOperator extends BaseBinaryOperator {
    @Override
    public Object execute(Value left, Value right, ErrorReporter errorReporter) {
        assertLeftValue(left, errorReporter);
        LeftValue leftValue = (LeftValue)left;
        Object result = rightShift(left, right, errorReporter);
        leftValue.set(result, errorReporter);
        // TODO: return result or leftValue
        return result;
    }

    @Override
    public String getOperator() {
        return ">>=";
    }

    @Override
    public int getPriority() {
        return QLPrecedences.ASSIGN;
    }
}
