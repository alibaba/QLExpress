package com.alibaba.qlexpress4.runtime;

import com.alibaba.qlexpress4.cache.QLCaches;
import com.alibaba.qlexpress4.runtime.data.AssignableDataValue;
import com.alibaba.qlexpress4.runtime.data.MapItemValue;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * root runtime with external variable and function
 * Author: DQinYuan
 */
public class QvmRuntime implements QContext {

    private final Map<String, Object> externalVariable;

    private final Map<String, LeftValue> newVariables;

    private final Map<String, QFunction> externalFunction;

    private final Map<String, Object> attachments;

    private final QLCaches qlCaches;

    private final boolean polluteExternal;

    private final long startTime;

    public QvmRuntime(Map<String, Object> externalVariable, Map<String, QFunction> externalFunction,
                      Map<String, Object> attachments, QLCaches qlCaches, boolean polluteExternal, long startTime) {
        this.externalVariable = externalVariable;
        this.externalFunction = externalFunction;
        this.attachments = attachments;
        this.qlCaches = qlCaches;
        this.polluteExternal = polluteExternal;
        this.startTime = startTime;
        this.newVariables = polluteExternal? Collections.emptyMap(): new HashMap<>();
    }

    @Override
    public Value getSymbol(String varName) {
        LeftValue newVariable = newVariables.get(varName);
        if (newVariable != null) {
            return newVariable;
        }
        Object externalValue = externalVariable.get(varName);
        if (externalValue == null) {
            return null;
        }
        if (polluteExternal) {
            return new MapItemValue(externalVariable, varName);
        }
        newVariable = new AssignableDataValue(externalValue);
        newVariables.put(varName, newVariable);
        return newVariable;
    }

    @Override
    public LeftValue defineSymbol(String varName, Class<?> varClz) {
        return polluteExternal?
                new MapItemValue(externalVariable, varName):
                newVariables.computeIfAbsent(varName, ignore ->
                        new AssignableDataValue(externalVariable.get(varName)));
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
    public Map<String, Object> attachment() {
        return attachments;
    }

    @Override
    public QLCaches getQLCaches() {
        return qlCaches;
    }
}
