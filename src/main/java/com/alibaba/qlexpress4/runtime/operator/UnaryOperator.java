package com.alibaba.qlexpress4.runtime.operator;

import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.runtime.Value;

/**
 * TODO 无法区分：++i, i++
 * Author: DQinYuan
 */
public interface UnaryOperator {

    Object execute(Value value, ErrorReporter errorReporter);

}
