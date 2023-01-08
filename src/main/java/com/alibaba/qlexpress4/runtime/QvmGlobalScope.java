package com.alibaba.qlexpress4.runtime;

import com.alibaba.qlexpress4.runtime.data.AssignableDataValue;
import com.alibaba.qlexpress4.runtime.data.MapItemValue;
import com.alibaba.qlexpress4.runtime.scope.QScope;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * global scope
 *
 * Author: DQinYuan
 */
public class QvmGlobalScope implements QScope {

    private final Map<String, Object> externalVariable;

    private final Map<String, LeftValue> newVariables;

    private final Map<String, QFunction> externalFunction;

    private final boolean polluteExternal;

    public QvmGlobalScope(Map<String, Object> externalVariable, Map<String, QFunction> externalFunction,
                          boolean polluteExternal) {
        this.externalVariable = externalVariable;
        this.newVariables = polluteExternal? Collections.emptyMap(): new HashMap<>();
        this.externalFunction = externalFunction;
        this.polluteExternal = polluteExternal;
    }

    @Override
    public Value getSymbol(String varName) {
        LeftValue newVariable = newVariables.get(varName);
        if (newVariable != null) {
            return newVariable;
        }
        if (polluteExternal) {
            return new MapItemValue(externalVariable, varName);
        }
        newVariable = new AssignableDataValue(externalVariable.get(varName));
        newVariables.put(varName, newVariable);
        return newVariable;
    }

    @Override
    public Object getSymbolValue(String varName) {
        return QScope.super.getSymbolValue(varName);
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
    public Value peek() {
        throw new UnsupportedOperationException();
    }

    @Override
    public QScope getParent() {
        throw new UnsupportedOperationException();
    }

    @Override
    public QScope newScope(ExceptionTable exceptionTable, int baseIndex) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ExceptionTable exceptionTable() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getBaseIndex() {
        throw new UnsupportedOperationException();
    }
}
