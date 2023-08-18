package com.alibaba.qlexpress4.runtime.operator;

/**
 * @author bingo
 */
public interface CustomBinaryOperator {
    Object execute(Object left, Object right);
}
