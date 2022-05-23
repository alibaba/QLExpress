package com.alibaba.qlexpress4.runtime.operator.base;

import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.exception.QLRuntimeException;
import com.alibaba.qlexpress4.runtime.Value;
import com.alibaba.qlexpress4.runtime.operator.UnaryOperator;

/**
 * @author 冰够
 */
public abstract class BaseUnaryOperator implements UnaryOperator {
    protected String operator;

    public BaseUnaryOperator(String operator) {
        this.operator = operator;
    }

    protected QLRuntimeException buildInvalidOperandTypeException(Value value, ErrorReporter errorReporter) {
        return errorReporter.report("InvalidOperandType", "Cannot use %s operator on type:%s with value:%s", operator,
            value.getTypeName(), value.get());
    }
}
