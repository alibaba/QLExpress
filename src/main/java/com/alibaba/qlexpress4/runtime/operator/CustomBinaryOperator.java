package com.alibaba.qlexpress4.runtime.operator;

import com.alibaba.qlexpress4.runtime.Value;

/**
 * @author bingo
 */
public interface CustomBinaryOperator {
    Object execute(Value left, Value right);
}
