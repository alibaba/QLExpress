package com.alibaba.qlexpress4.runtime;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.runtime.context.ExpressContext;
import com.alibaba.qlexpress4.runtime.data.AssignableDataValue;
import com.alibaba.qlexpress4.runtime.function.CustomFunction;
import com.alibaba.qlexpress4.runtime.scope.QScope;

import java.util.HashMap;
import java.util.Map;

/**
 * global scope
 *
 * Author: DQinYuan
 */
public class QvmGlobalScope implements QScope {

    private final ExpressContext externalVariable;

    private final Map<String, LeftValue> newVariables;

    private final Map<String, CustomFunction> externalFunction;

    private final QLOptions qlOptions;

    public QvmGlobalScope(ExpressContext externalVariable, Map<String, CustomFunction> externalFunction,
                          QLOptions qlOptions) {
        this.externalVariable = externalVariable;
        this.newVariables = new HashMap<>();
        this.externalFunction = externalFunction;
        this.qlOptions = qlOptions;
    }

    @Override
    public Value getSymbol(String varName) {
        LeftValue newVariable = newVariables.get(varName);
        if (newVariable != null) {
            return newVariable;
        }
        Value externalValue = externalVariable.get(qlOptions.getAttachments(), varName);
        if (externalValue != null && qlOptions.isPolluteUserContext()) {
            return externalValue;
        }
        newVariable = new AssignableDataValue(varName, externalValue == null? null: externalValue.get());
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
    public void defineFunction(String functionName, CustomFunction function) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CustomFunction getFunction(String functionName) {
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
    public QScope newScope() {
        throw new UnsupportedOperationException();
    }
}
