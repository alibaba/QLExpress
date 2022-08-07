package com.alibaba.qlexpress4.runtime;

import com.alibaba.qlexpress4.cache.QLCaches;
import com.alibaba.qlexpress4.runtime.data.AssignableDataValue;

import java.util.HashMap;
import java.util.Map;

/**
 * Author: DQinYuan
 */
public class QvmRuntime implements QRuntime {

    private final QRuntime parent;

    private final Map<String, Value> symbolTable;

    private final Map<String, QFunction> functionTable;

    private final Map<String, Object> attachments;

    private final FixedSizeStack opStack;

    /**
     * equals to parent
     */
    private final long startTime;

    public QvmRuntime(QRuntime parent, Map<String, Value> symbolTable,
                      Map<String, Object> attachments, int maxStackSize, long startTime) {
        this.parent = parent;
        // TODO: 优化成 fixedArrayMap
        this.symbolTable = symbolTable;
        // TODO: 优化成 fixedArrayMap, 大多数表达式根本没有函数定义
        this.functionTable = new HashMap<>();
        this.attachments = attachments;
        this.opStack = new FixedSizeStack(maxStackSize);
        this.startTime = startTime;
    }

    @Override
    public Value getSymbol(String varName) {
        Value localSymbol = symbolTable.get(varName);
        return localSymbol != null? localSymbol:
                parent != null? parent.getSymbol(varName): null;
    }

    @Override
    public Object getSymbolValue(String varName) {
        Value symbol = getSymbol(varName);
        return symbol == null? null: symbol.get();
    }

    @Override
    public LeftValue defineSymbol(String varName, Class<?> varClz) {
        if (parent == null) {
            return defineSymbolInner(varName, varClz);
        }
        return parent.isPopulate()? parent.defineSymbol(varName, varClz):
                defineSymbolInner(varName, varClz);
    }

    private LeftValue defineSymbolInner(String varName, Class<?> varClz) {
        LeftValue symbol = new AssignableDataValue(null, varClz);
        symbolTable.put(varName, symbol);
        return symbol;
    }

    @Override
    public void defineLocalSymbol(String varName, Class<?> varClz, Object value) {
        symbolTable.put(varName, new AssignableDataValue(value, varClz));
    }

    @Override
    public void defineFunction(String functionName, QFunction function) {
        functionTable.put(functionName, function);
    }

    @Override
    public QFunction getFunction(String functionName) {
        QFunction function = functionTable.get(functionName);
        return function == null && parent != null? parent.getFunction(functionName):
                function;
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

    @Override
    public boolean isPopulate() {
        return true;
    }

    @Override
    public Map<String, Object> attachment() {
        return attachments;
    }

    @Override
    public QLCaches getQLCaches() {
        return null;
    }
}
