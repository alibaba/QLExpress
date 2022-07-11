package com.alibaba.qlexpress4.runtime;

import com.alibaba.qlexpress4.runtime.data.DataValue;
import com.alibaba.qlexpress4.runtime.data.MapItemValue;

import java.util.Map;

/**
 * root runtime with external variable and function
 * Author: DQinYuan
 */
public class QvmRootRuntime implements QRuntime {

    private final Map<String, Object> externalVariable;

    private final Map<String, QFunction> externalFunction;

    private final boolean polluteExternal;

    private final long startTime;

    public QvmRootRuntime(Map<String, Object> externalVariable, Map<String, QFunction> externalFunction,
                          boolean polluteExternal, long startTime) {
        this.externalVariable = externalVariable;
        this.externalFunction = externalFunction;
        this.polluteExternal = polluteExternal;
        this.startTime = startTime;
    }

    @Override
    public Value getSymbol(String varName) {
        if (polluteExternal) {
            return new MapItemValue(externalVariable, varName);
        }
        Object varValue = externalVariable.get(varName);
        return varValue == null? null: new DataValue(varValue);
    }

    @Override
    public Object getSymbolValue(String varName) {
        return externalVariable.get(varName);
    }

    @Override
    public LeftValue defineSymbol(String varName, Class<?> varClz) {
        return new MapItemValue(externalVariable, varClz);
    }

    @Override
    public void defineLocalSymbol(String varName, Class<?> varClz, Object value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void defineFunction(String functionName, QFunction function) {
        throw new UnsupportedOperationException();
    }

    @Override
    public QFunction getFunction(String functionName) {
        return externalFunction.get(functionName);
    }

    @Override
    public void push(Value value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Parameters pop(int number) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Value pop() {
        throw new UnsupportedOperationException();
    }

    @Override
    public long scriptStartTimeStamp() {
        return startTime;
    }

    @Override
    public boolean isPopulate() {
        return polluteExternal;
    }
}
