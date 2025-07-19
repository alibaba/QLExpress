package com.alibaba.qlexpress4.runtime;

import com.alibaba.qlexpress4.QLOptions;

import java.util.function.Consumer;

/**
 * Author: DQinYuan
 */
public interface QLambdaDefinition {
    
    QLambda toLambda(QContext qContext, QLOptions qlOptions, boolean newEnv);
    
    void println(int depth, Consumer<String> debug);
    
    String getName();
}
