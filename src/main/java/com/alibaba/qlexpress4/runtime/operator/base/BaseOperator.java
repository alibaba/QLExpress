package com.alibaba.qlexpress4.runtime.operator.base;

import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.exception.QLRuntimeException;
import com.alibaba.qlexpress4.runtime.Value;
import com.alibaba.qlexpress4.runtime.operator.Operator;

/**
 * @author 冰够
 */
public abstract class BaseOperator implements Operator {
    protected QLRuntimeException buildInvalidOperandTypeException(Value left, Value right,
        ErrorReporter errorReporter) {
        return errorReporter.report("InvalidOperandType",
            "Cannot use %s operator on leftType:%s with leftValue:%s and rightType:%s with rightValue:%s",
            getOperator(), left.getValueType(), left.get(), right.getValueType(), right.get());
    }

    protected abstract String getOperator();
}
