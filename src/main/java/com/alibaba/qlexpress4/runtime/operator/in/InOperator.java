package com.alibaba.qlexpress4.runtime.operator.in;

import java.util.Collection;

import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.runtime.Value;
import com.alibaba.qlexpress4.runtime.data.DataValue;
import com.alibaba.qlexpress4.runtime.operator.base.BaseBinaryOperator;
import com.alibaba.qlexpress4.runtime.operator.compare.EqualOperator;
import com.alibaba.qlexpress4.runtime.operator.constant.OperatorPriority;

/**
 * @author 冰够
 */
public class InOperator extends BaseBinaryOperator {
    @Override
    public Object execute(Value left, Value right, ErrorReporter errorReporter) {
        Object rightOperand = right.get();
        if (rightOperand == null) {
            return false;
        }
        if (!(rightOperand instanceof Collection)) {
            throw buildInvalidOperandTypeException(left, right, errorReporter);
        }

        Collection<?> rightCollection = (Collection<?>)right;
        // TODO bingo 这样处理？
        EqualOperator equalOperator = new EqualOperator();
        for (Object rightElement : rightCollection) {
            boolean executeResult = (boolean)equalOperator.execute(left, new DataValue(rightElement), errorReporter);
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
        return OperatorPriority.PRIORITY_10;
    }
}
