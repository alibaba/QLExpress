package com.alibaba.qlexpress4.runtime.operator.base;

import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.exception.QLRuntimeException;
import com.alibaba.qlexpress4.runtime.Value;
import com.alibaba.qlexpress4.runtime.operator.unary.UnaryOperator;

/**
 * @author 冰够
 */
public abstract class BaseUnaryOperator implements UnaryOperator {
    //@Override
    //public Object execute(Value value, ErrorReporter errorReporter) {
    //    return null;
    //}
    //
    //protected abstract Object execute(Value value);

    protected QLRuntimeException buildInvalidOperandTypeException(Value value, ErrorReporter errorReporter) {
        return errorReporter.report("InvalidOperandType", "Cannot use %s operator on type:%s with value:%s",
            getOperator(), value.getActualClassName(), value.get());
    }
}
