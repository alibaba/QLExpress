package com.alibaba.qlexpress4.runtime.operator;

import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.runtime.Value;

/**
 * Author: DQinYuan
 */
public interface UnaryOperator {

    Object execute(Value value, ErrorReporter errorReporter);

}
