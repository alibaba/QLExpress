package com.alibaba.qlexpress4.runtime.operator.collection;

import java.util.Collection;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.QLPrecedences;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.runtime.Value;
import com.alibaba.qlexpress4.runtime.data.DataValue;
import com.alibaba.qlexpress4.runtime.operator.base.BaseBinaryOperator;

/**
 * @author 冰够
 */
public class InOperator extends BaseBinaryOperator {
    private static final InOperator INSTANCE = new InOperator();

    private InOperator() {
    }

    public static InOperator getInstance() {
        return INSTANCE;
    }

    @Override
    public Object execute(Value left, Value right, QLOptions qlOptions, ErrorReporter errorReporter) {
        Object rightOperand = right.get();
        if (rightOperand == null) {
            return false;
        }
        if (!(rightOperand instanceof Collection)) {
            throw buildInvalidOperandTypeException(left, right, errorReporter);
        }

        Collection<?> rightCollection = (Collection<?>)right;
        for (Object rightElement : rightCollection) {
            boolean executeResult = compare(left, new DataValue(rightElement), errorReporter) == 0;
            if (executeResult) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getOperator() {
        return "in";
    }

    @Override
    public int getPriority() {
        return QLPrecedences.IN_LIKE;
    }
}
