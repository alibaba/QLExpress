package com.alibaba.qlexpress4.runtime.operator;

import com.alibaba.qlexpress4.QLPrecedences;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.runtime.Value;
import com.alibaba.qlexpress4.runtime.operator.base.BaseBinaryOperator;

/**
 * @author 冰够
 */
public class InstanceOfOperator extends BaseBinaryOperator {
    @Override
    public Object execute(Value left, Value right, ErrorReporter errorReporter) {
        Object sourceObject = left.get();
        Object targetClass = right.get();
        if (targetClass == null) {
            throw errorReporter.report("INVALID_OPERAND", "value on the right side of 'instanceof' is null");
        }
        if (!(targetClass instanceof Class)) {
            throw errorReporter.report("INVALID_OPERAND", "value on the right side of 'instanceof' is not Class");
        }
        if (sourceObject == null) {
            return false;
        }
        return ((Class<?>)targetClass).isAssignableFrom(sourceObject.getClass());
    }

    @Override
    public String getOperator() {
        return "instanceof";
    }

    @Override
    public int getPriority() {
        return QLPrecedences.COMPARE;
    }
}
