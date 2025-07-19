package com.alibaba.qlexpress4.runtime.function;

import com.alibaba.qlexpress4.runtime.IMethod;

import java.lang.reflect.InvocationTargetException;

/**
 * Author: DQinYuan
 */
public abstract class ExtensionFunction implements IMethod {
    
    @Override
    public boolean isVarArgs() {
        return false;
    }
    
    @Override
    public boolean isAccess() {
        return true;
    }
    
    @Override
    public void setAccessible(boolean flag) {
        
    }
    
}
