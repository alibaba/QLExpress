package com.alibaba.qlexpress4.runtime;

import com.alibaba.qlexpress4.runtime.data.AssignableDataValue;

import java.util.HashMap;
import java.util.Map;

/**
 * Author: DQinYuan
 */
public class QvmRuntime implements QRuntime {

    private final QRuntime parent;

    private final Map<String, LeftValue> symbolTable;

    private final FixedSizeStack opStack;

    /**
     * equals to parent
     */
    private final long startTime;

    public QvmRuntime(QRuntime parent, Map<String, LeftValue> symbolTable, int maxStackSize, long startTime) {
        this.parent = parent;
        this.symbolTable = symbolTable;
        this.opStack = new FixedSizeStack(maxStackSize);
        this.startTime = startTime;
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
    public long scriptStartTimeStamp() {
        return startTime;
    }
}
