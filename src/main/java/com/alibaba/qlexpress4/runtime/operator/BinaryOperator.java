package com.alibaba.qlexpress4.runtime.operator;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.runtime.Value;

/**
 * 二元操作符
 * Author: DQinYuan
 * date 2022/1/12 2:34 下午
 */
public interface BinaryOperator extends Operator {
    /**
     * 执行操作符计算
     *
     * @param left
     * @param right
     * @param qlOptions
     * @param errorReporter
     * @return
     */
    Object execute(Value left, Value right, QLOptions qlOptions, ErrorReporter errorReporter);
}
