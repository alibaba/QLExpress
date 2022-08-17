package com.alibaba.qlexpress4.runtime.operator.compare;

import com.alibaba.qlexpress4.QLPrecedences;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.runtime.Value;
import com.alibaba.qlexpress4.runtime.operator.base.BaseBinaryOperator;

/**
 * @author 冰够
 */
public class EqualOperator extends BaseBinaryOperator {
    private static final EqualOperator INSTANCE = new EqualOperator();

    private EqualOperator() {
    }

    public static EqualOperator getInstance() {
        return INSTANCE;
    }

    @Override
    public String getOperator() {
        return "==";
    }

    @Override
    public int getPriority() {
        return QLPrecedences.EQUAL;
    }

    @Override
    public Object execute(Value left, Value right, ErrorReporter errorReporter) {
        return compare(left, right, errorReporter) == 0;
    }
}
