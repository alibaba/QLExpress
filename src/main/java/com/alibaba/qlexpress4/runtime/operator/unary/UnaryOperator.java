package com.alibaba.qlexpress4.runtime.operator.unary;

import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.runtime.Value;
import com.alibaba.qlexpress4.runtime.operator.Operator;

/**
 * 一元操作符
 * prefix operator
 * suffix operator
 * Author: DQinYuan
 */
public interface UnaryOperator extends Operator {
    /**
     * 执行操作符计算
     *
     * @param value
     * @param errorReporter
     * @return
     */
    Object execute(Value value, ErrorReporter errorReporter);
}
