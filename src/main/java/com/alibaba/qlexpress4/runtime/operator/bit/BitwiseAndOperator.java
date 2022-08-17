package com.alibaba.qlexpress4.runtime.operator.bit;

import com.alibaba.qlexpress4.QLPrecedences;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.runtime.Value;
import com.alibaba.qlexpress4.runtime.operator.base.BaseBinaryOperator;

/**
 * @author 冰够
 */
public class BitwiseAndOperator extends BaseBinaryOperator {
    private static final BitwiseAndOperator INSTANCE = new BitwiseAndOperator();

    private BitwiseAndOperator() {
    }

    public static BitwiseAndOperator getInstance() {
        return INSTANCE;
    }

    @Override
    public String getOperator() {
        return "&";
    }

    @Override
    public int getPriority() {
        return QLPrecedences.BIT_AND;
    }

    @Override
    public Object execute(Value left, Value right, ErrorReporter errorReporter) {
        return bitwiseAnd(left, right, errorReporter);
    }
}
