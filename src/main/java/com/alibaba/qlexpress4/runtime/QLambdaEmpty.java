package com.alibaba.qlexpress4.runtime;

import com.alibaba.qlexpress4.runtime.function.CustomFunction;

import java.util.Collections;
import java.util.Map;

/**
 * Author: DQinYuan
 */
public class QLambdaEmpty implements QLambda {
    
    public static QLambda INSTANCE = new QLambdaEmpty();
    
    @Override
    public QResult call(Object... params)
        throws Throwable {
        return QResult.NEXT_INSTRUCTION;
    }
    
}
