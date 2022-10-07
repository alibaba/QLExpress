package com.alibaba.qlexpress4.runtime.operator;

/**
 * @author 冰够
 */
public interface CustomBinaryOperator {
    Object execute(Object left, Object right);
}
