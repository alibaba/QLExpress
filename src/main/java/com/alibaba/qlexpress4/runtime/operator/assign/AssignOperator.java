package com.alibaba.qlexpress4.runtime.operator.assign;

import com.alibaba.qlexpress4.QLPrecedences;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.runtime.LeftValue;
import com.alibaba.qlexpress4.runtime.Value;
import com.alibaba.qlexpress4.runtime.operator.base.BaseBinaryOperator;
import com.alibaba.qlexpress4.runtime.operator.constant.OperatorPriority;

/**
 * @author 冰够
 */
public class AssignOperator extends BaseBinaryOperator {
    @Override
    public Object execute(Value left, Value right, ErrorReporter errorReporter) {
        if (!(left instanceof LeftValue)) {
            throw errorReporter.report("INVALID_ASSIGNMENT",
                    "value on the left side of '=' is not assignable");
        }
        LeftValue leftValue = (LeftValue) left;
        Object newValue = right.get();
        leftValue.setInner(newValue);
        return newValue;
    }

    @Override
    public String getOperator() {
        return "=";
    }

    @Override
    public int getPriority() {
        return QLPrecedences.ASSIGN;
    }
}
