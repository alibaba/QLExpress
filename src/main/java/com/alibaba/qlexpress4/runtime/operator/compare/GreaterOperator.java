package com.alibaba.qlexpress4.runtime.operator.compare;

import com.alibaba.qlexpress4.QLPrecedences;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.runtime.Value;
import com.alibaba.qlexpress4.runtime.operator.base.BaseBinaryOperator;

/**
 * @author 冰够
 */
public class GreaterOperator extends BaseBinaryOperator {
    private static final GreaterOperator INSTANCE = new GreaterOperator();

    private GreaterOperator() {
    }

    @Override
    public String getOperator() {
        return ">";
    }

    @Override
    public int getPriority() {
        return QLPrecedences.COMPARE;
    }

    public static GreaterOperator getInstance() {
        return INSTANCE;
    }

    @Override
    public Object execute(Value left, Value right, ErrorReporter errorReporter) {
        return compare(left, right, errorReporter) > 0;
    }
}
