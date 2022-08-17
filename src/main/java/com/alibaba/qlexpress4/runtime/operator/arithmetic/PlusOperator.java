package com.alibaba.qlexpress4.runtime.operator.arithmetic;

import com.alibaba.qlexpress4.QLPrecedences;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.runtime.Value;
import com.alibaba.qlexpress4.runtime.operator.base.BaseBinaryOperator;

/**
 * QLExpress只支持String和Number类型的+
 *
 * @author 冰够
 */
public class PlusOperator extends BaseBinaryOperator {
    private static final PlusOperator INSTANCE = new PlusOperator();

    private PlusOperator() {
    }

    @Override
    public Object execute(Value left, Value right, ErrorReporter errorReporter) {
        return plus(left, right, errorReporter);
    }

    @Override
    public String getOperator() {
        return "+";
    }

    @Override
    public int getPriority() {
        return QLPrecedences.ADD;
    }

    public static PlusOperator getInstance() {
        return INSTANCE;
    }
}
