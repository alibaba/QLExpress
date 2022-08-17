package com.alibaba.qlexpress4.runtime.operator.arithmetic;

import com.alibaba.qlexpress4.QLPrecedences;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.runtime.Value;
import com.alibaba.qlexpress4.runtime.operator.base.BaseBinaryOperator;

/**
 * @author 冰够
 */
public class ModOperator extends BaseBinaryOperator {
    @Override
    public Object execute(Value left, Value right, ErrorReporter errorReporter) {
        return mod(left, right, errorReporter);
    }

    @Override
    public String getOperator() {
        return "%";
    }

    @Override
    public int getPriority() {
        return QLPrecedences.MULTI;
    }
}
