package com.alibaba.qlexpress4.runtime;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.utils.PrintlnUtils;

import java.util.function.Consumer;

/**
 * Author: DQinYuan
 */
public class QLambdaDefinitionEmpty implements QLambdaDefinition {
    
    public static QLambdaDefinition INSTANCE = new QLambdaDefinitionEmpty();
    
    @Override
    public QLambda toLambda(QContext qContext, QLOptions qlOptions, boolean newEnv) {
        return QLambdaEmpty.INSTANCE;
    }
    
    @Override
    public void println(int depth, Consumer<String> debug) {
        PrintlnUtils.printlnByCurDepth(depth, getName(), debug);
    }
    
    @Override
    public String getName() {
        return "EmptyLambdaDefinition";
    }
}
