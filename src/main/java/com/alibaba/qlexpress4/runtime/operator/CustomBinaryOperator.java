package com.alibaba.qlexpress4.runtime.operator;

import com.alibaba.qlexpress4.runtime.Value;

/**
 * @author bingo
 */
public interface CustomBinaryOperator {
    /**
     * @param left left operand
     * @param right right operand
     * @return result
     * @throws Throwable {@link com.alibaba.qlexpress4.exception.UserDefineException} for custom error message
     */
    Object execute(Value left, Value right)
        throws Throwable;
}
