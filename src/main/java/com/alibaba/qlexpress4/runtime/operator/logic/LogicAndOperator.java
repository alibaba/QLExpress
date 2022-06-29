package com.alibaba.qlexpress4.runtime.operator.logic;

import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.runtime.Value;
import com.alibaba.qlexpress4.runtime.operator.base.BaseBinaryOperator;
import com.alibaba.qlexpress4.runtime.operator.constant.OperatorPriority;

/**
 * @author 冰够
 */
public class LogicAndOperator extends BaseBinaryOperator {
    @Override
    public Object execute(Value left, Value right, ErrorReporter errorReporter) {
        Object leftValue = left.get();
        Object rightValue = right.get();
        // 抽取至类型转换工具类
        if (leftValue == null) {
            leftValue = false;
        }
        if (rightValue == null) {
            rightValue = false;
        }

        if (!(leftValue instanceof Boolean) || !(rightValue instanceof Boolean)) {
            throw buildInvalidOperandTypeException(left, right, errorReporter);
        }

        return (Boolean)leftValue && (Boolean)rightValue;
    }

    @Override
    public String getOperator() {
        return "&&";
    }

    @Override
    public int getPriority() {
        return OperatorPriority.PRIORITY_3;
    }
}
