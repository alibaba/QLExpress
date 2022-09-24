package com.alibaba.qlexpress4.runtime;

import com.alibaba.qlexpress4.cache.QLCaches;
import com.alibaba.qlexpress4.runtime.scope.QScope;

import java.util.Map;

/**
 * Author: DQinYuan
 */
public class DelegateQContext implements QContext {

    private final QRuntime qRuntime;

    private final QScope qScope;

    public DelegateQContext(QRuntime qRuntime, QScope qScope) {
        this.qRuntime = qRuntime;
        this.qScope = qScope;
    }

    @Override
    public LeftValue defineSymbol(String varName, Class<?> varClz) {
        return qRuntime.defineSymbol(varName, varClz);
    }

    @Override
    public long scriptStartTimeStamp() {
        return qRuntime.scriptStartTimeStamp();
    }

    @Override
    public Map<String, Object> attachment() {
        return qRuntime.attachment();
    }

    @Override
    public QLCaches getQLCaches() {
        return qRuntime.getQLCaches();
    }

    @Override
    public Value getSymbol(String varName) {
        return qScope.getSymbol(varName);
    }

    @Override
    public Object getSymbolValue(String varName) {
        return qScope.getSymbolValue(varName);
    }

    @Override
    public void defineLocalSymbol(String varName, Class<?> varClz, Object value) {
        qScope.defineLocalSymbol(varName, varClz, value);
    }

    @Override
    public void defineFunction(String functionName, QFunction function) {
        qScope.defineFunction(functionName, function);
    }

    @Override
    public QFunction getFunction(String functionName) {
        return qScope.getFunction(functionName);
    }

    @Override
    public void push(Value value) {
        qScope.push(value);
    }

    @Override
    public Parameters pop(int number) {
        return qScope.pop(number);
    }

    @Override
    public Value pop() {
        return qScope.pop();
    }

    @Override
    public QScope getQScope() {
        return qScope;
    }
}
