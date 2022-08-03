package com.alibaba.qlexpress4.runtime.operator.assign;

import com.alibaba.qlexpress4.QLPrecedences;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.runtime.LeftValue;
import com.alibaba.qlexpress4.runtime.Value;
import com.alibaba.qlexpress4.runtime.operator.base.BaseBinaryOperator;

/**
 * @author 冰够
 */
public class PlusAssignOperator extends BaseBinaryOperator {
    @Override
    public Object execute(Value left, Value right, ErrorReporter errorReporter) {
        LeftValue leftValue = (LeftValue)left;
        // TODO bingo parse阶段实现？不然我的new PlusOperator，然后把left和right传入进行计算
        //int newValue = left.get() + right.get();
        //leftValue.set(newValue);
        //return newValue;
        return null;
    }

    @Override
    public String getOperator() {
        return "+=";
    }

    @Override
    public int getPriority() {
        return QLPrecedences.ASSIGN;
    }
}
