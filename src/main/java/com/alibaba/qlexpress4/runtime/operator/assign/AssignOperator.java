package com.alibaba.qlexpress4.runtime.operator.assign;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.QLPrecedences;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.runtime.LeftValue;
import com.alibaba.qlexpress4.runtime.QRuntime;
import com.alibaba.qlexpress4.runtime.Value;
import com.alibaba.qlexpress4.runtime.operator.base.BaseBinaryOperator;

/**
 * @author bingo
 */
public class AssignOperator extends BaseBinaryOperator {
    private static final AssignOperator INSTANCE = new AssignOperator();

    private AssignOperator() {
    }

    public static AssignOperator getInstance() {
        return INSTANCE;
    }

    @Override
    public Object execute(Value left, Value right, QRuntime qRuntime, QLOptions qlOptions,
        ErrorReporter errorReporter) {
        assertLeftValue(left, errorReporter);
        LeftValue leftValue = (LeftValue)left;
        Object newValue = right.get();
        leftValue.set(newValue, errorReporter);
        return newValue;
    }

    @Override
    public String getOperator() {
        return "=";
    }

    @Override
    public int getPriority() {
        return QLPrecedences.ASSIGN;
    }
}
