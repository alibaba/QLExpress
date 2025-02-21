package com.alibaba.qlexpress4.test.performance.operator;

import java.util.Arrays;
import java.util.Collection;

import com.alibaba.qlexpress4.runtime.Value;

/**
 * @author 冰够
 */
public class InOperator extends BaseCustomBinaryOperator {
    private static final InOperator INSTANCE = new InOperator();

    private InOperator() {
    }

    public static InOperator getInstance() {
        return INSTANCE;
    }

    @Override
    public Object execute(Value left, Value right) throws Throwable {
        if (right.get() == null) {
            return false;
        }
        Collection<?> rightCollection = right.get() instanceof Collection ? (Collection<?>)right.get() : Arrays.asList((Object[])right.get());
        for (Object rightItem : rightCollection) {
            if (equals(left.get(), rightItem)) {
                return true;
            }
        }
        return false;
    }
}
