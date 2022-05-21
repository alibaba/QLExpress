package com.alibaba.qlexpress4.runtime.operator;

import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.runtime.Value;

/**
 * TODO bingo 如何支持用户自定义实现操作符
 * 1. 比如自定义类，student1 + student2
 * Author: DQinYuan
 * date 2022/1/12 2:34 下午
 */
public interface Operator {
    Object execute(Value left, Value right, ErrorReporter errorReporter);

    /**
     * TODO 为啥关心优先级？
     *
     * @return
     */
    int getPrecedence();
}
