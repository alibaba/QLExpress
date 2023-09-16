package com.alibaba.qlexpress4.runtime.operator.unary;

import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.runtime.Value;
import com.alibaba.qlexpress4.runtime.operator.Operator;

/**
 * unary operator, include:
 * prefix operator
 * suffix operator
 * Author: DQinYuan
 */
public interface UnaryOperator extends Operator {
    /**
     * @param value operand
     * @param errorReporter operator
     * @return result of operator
     */
    Object execute(Value value, ErrorReporter errorReporter);
}
