package com.alibaba.qlexpress4.runtime.operator.compare;

import com.alibaba.qlexpress4.QLPrecedences;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.runtime.Value;
import com.alibaba.qlexpress4.runtime.operator.base.BaseBinaryOperator;

import java.util.Objects;

/**
 * @author 冰够
 */
public class UnequalOperator extends BaseBinaryOperator {
    private static final UnequalOperator INSTANCE = new UnequalOperator();

    private UnequalOperator() {
    }

    public static UnequalOperator getInstance() {
        return INSTANCE;
    }

    @Override
    public String getOperator() {
        return "!=";
    }

    @Override
    public int getPriority() {
        return QLPrecedences.EQUAL;
    }

    @Override
    public Object execute(Value left, Value right, ErrorReporter errorReporter) {
        // TODO: 为了方便编写用例,先简单写一下 bingo 优化一下
        return !Objects.equals(left.get(), right.get());
    }
}
