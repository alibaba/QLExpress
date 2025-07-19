package com.alibaba.qlexpress4.runtime.instruction;

/**
 * Author: DQinYuan
 */
public interface InterWithDefaultMethod {
    
    int getI();
    
    default int returnI() {
        return getI();
    }
}
