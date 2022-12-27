package com.alibaba.qlexpress4.runtime.scope;

import com.alibaba.qlexpress4.runtime.*;
import com.alibaba.qlexpress4.runtime.data.AssignableDataValue;

import java.util.HashMap;
import java.util.Map;

/**
 * Author: DQinYuan
 */
public class QvmBlockScope implements QScope {

    private final QScope parent;

    private final Map<String, Value> symbolTable;

    private final Map<String, QFunction> functionTable;

    // TODO: stack 优化, 只有一个总的 stack, 不需要每个 scope 一个
    private final FixedSizeStack opStack;

    public QvmBlockScope(QScope parent, Map<String, Value> symbolTable, int maxStackSize) {
        this.parent = parent;
        // TODO: 优化成 fixedArrayMap
        this.symbolTable = symbolTable;
        // TODO: 优化成 fixedArrayMap, 大多数表达式根本没有函数定义
        this.functionTable = new HashMap<>();
        this.opStack = new FixedSizeStack(maxStackSize);
    }

    public QvmBlockScope(QScope parent, Map<String, Value> symbolTable, FixedSizeStack reuseStack) {
        this.parent = parent;
        // TODO: 优化成 fixedArrayMap
        this.symbolTable = symbolTable;
        // TODO: 优化成 fixedArrayMap, 大多数表达式根本没有函数定义
        this.functionTable = new HashMap<>();
        this.opStack = reuseStack;
    }

    @Override
    public Value getSymbol(String varName) {
        Value localSymbol = symbolTable.get(varName);
        return localSymbol != null? localSymbol: parent.getSymbol(varName);
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
        return function == null? parent.getFunction(functionName): function;
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
    public Value peek() {
        return opStack.peak();
    }

    @Override
    public QScope getParent() {
        return parent;
    }

    @Override
    public QScope newScope() {
        return new QvmBlockScope(this, new HashMap<>(), opStack);
    }
}
