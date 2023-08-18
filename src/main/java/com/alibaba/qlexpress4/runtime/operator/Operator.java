package com.alibaba.qlexpress4.runtime.operator;

/**
 * 操作符接口
 *
 * @author bingo
 */
public interface Operator {
    /**
     * @return 操作符
     */
    String getOperator();

    /**
     * @return 操作符优先级
     */
    int getPriority();
}
