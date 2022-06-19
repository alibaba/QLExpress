package com.alibaba.qlexpress4.runtime;

import com.alibaba.qlexpress4.runtime.data.AssignableDataValue;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * Author: DQinYuan
 */
public class QvmRuntime implements QRuntime {

    private final QvmRuntime parent;

    private final Map<String, LeftValue> symbolTable = new HashMap<>();

    private final FixedSizeStack opStack;

    public QvmRuntime(QvmRuntime parent, int maxStackSize) {
        this.parent = parent;
        this.opStack = new FixedSizeStack(maxStackSize);
    }

    @Override
    public LeftValue getSymbol(String varName) {
        LeftValue localSymbol = symbolTable.get(varName);
        return localSymbol != null? localSymbol:
                parent != null? parent.getSymbol(varName): null;
    }

    @Override
    public Object getSymbolValue(String varName) {
        LeftValue symbol = getSymbol(varName);
        return symbol == null? null: symbol.get();
    }

    @Override
    public LeftValue defineSymbol(String varName, Class<?> varClz) {
        if (parent == null) {
            LeftValue symbol = new AssignableDataValue(null, varClz);
            symbolTable.put(varName, symbol);
            return symbol;
        }
        return parent.defineSymbol(varName, varClz);
    }

    @Override
    public void defineLocalSymbol(String varName, Class<?> varClz, Object value) {
        symbolTable.put(varName, new AssignableDataValue(value, varClz));
    }

    @Override
    public void push(Value value) {
        opStack.push(value);
    }

    @Override
    public Parameters pop(int number) {
        return opStack.pop(number);
    }

    @Override
    public Value pop() {
        return opStack.pop();
    }

    @Override
    public void exitAndReturn(QResult returnValue) {

    }

    @Override
    public long scriptStartTimeStamp() {
        return 0;
    }
}
