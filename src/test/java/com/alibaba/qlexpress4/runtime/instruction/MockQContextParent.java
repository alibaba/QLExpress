package com.alibaba.qlexpress4.runtime.instruction;

import com.alibaba.qlexpress4.runtime.*;
import com.alibaba.qlexpress4.runtime.function.CustomFunction;
import com.alibaba.qlexpress4.runtime.scope.QScope;
import com.alibaba.qlexpress4.runtime.trace.QTraces;
import com.alibaba.qlexpress4.security.QLSecurityStrategy;

import java.util.Collections;
import java.util.Map;

/**
 * Author: TaoKan
 */
public class MockQContextParent implements QContext {
    private Value value;
    
    private Parameters parameters;
    
    private final boolean allowPrivateAccess;
    
    private final ReflectLoader reflectLoader;
    
    public MockQContextParent(boolean allowPrivateAccess) {
        this.allowPrivateAccess = allowPrivateAccess;
        this.reflectLoader = new ReflectLoader(QLSecurityStrategy.open(), Collections.emptyList(), allowPrivateAccess);
    }
    
    public Value getValue() {
        return value;
    }
    
    public void setParameters(Parameters parameters) {
        this.parameters = parameters;
    }
    
    @Override
    public LeftValue getSymbol(String varName) {
        return null;
    }
    
    @Override
    public Value getSymbolValue(String varName) {
        return null;
    }
    
    @Override
    public void defineLocalSymbol(String varName, Class<?> varClz, Object value) {
        
    }
    
    @Override
    public void defineFunction(String functionName, CustomFunction function) {
        
    }
    
    @Override
    public CustomFunction getFunction(String functionName) {
        return null;
    }
    
    @Override
    public void push(Value value) {
        this.value = value;
    }
    
    public void pushParameter(Parameters parameters) {
        this.parameters = parameters;
    }
    
    @Override
    public Parameters pop(int number) {
        return this.parameters;
    }
    
    @Override
    public Value pop() {
        return this.value;
    }
    
    @Override
    public Value peek() {
        return this.value;
    }
    
    @Override
    public QScope getParent() {
        return null;
    }
    
    @Override
    public QScope newScope() {
        return null;
    }
    
    @Override
    public long scriptStartTimeStamp() {
        return 0;
    }
    
    @Override
    public Map<String, Object> attachment() {
        return null;
    }
    
    @Override
    public ReflectLoader getReflectLoader() {
        return reflectLoader;
    }
    
    @Override
    public QTraces getTraces() {
        return null;
    }
    
    @Override
    public QScope getCurrentScope() {
        return this;
    }
    
    @Override
    public void closeScope() {
    }
}
