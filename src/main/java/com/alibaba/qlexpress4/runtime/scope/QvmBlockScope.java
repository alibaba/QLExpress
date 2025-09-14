package com.alibaba.qlexpress4.runtime.scope;

import com.alibaba.qlexpress4.runtime.*;
import com.alibaba.qlexpress4.runtime.data.AssignableDataValue;
import com.alibaba.qlexpress4.runtime.function.CustomFunction;

import java.util.HashMap;
import java.util.Map;

/**
 * Author: DQinYuan
 */
public class QvmBlockScope implements QScope {
    
    private final QScope parent;
    
    private final Map<String, Value> symbolTable;
    
    private final Map<String, CustomFunction> functionTable;
    
    private final FixedSizeStack opStack;
    
    private final ExceptionTable exceptionTable;
    
    public QvmBlockScope(QScope parent, Map<String, Value> symbolTable, int maxStackSize,
        ExceptionTable exceptionTable) {
        this(parent, symbolTable, new FixedSizeStack(maxStackSize), exceptionTable);
    }
    
    public QvmBlockScope(QScope parent, Map<String, Value> symbolTable, FixedSizeStack reuseStack,
        ExceptionTable exceptionTable) {
        this.parent = parent;
        this.symbolTable = symbolTable;
        this.functionTable = new HashMap<>();
        this.opStack = reuseStack;
        this.exceptionTable = exceptionTable;
    }
    
    @Override
    public Value getSymbol(String varName) {
        Value localSymbol = symbolTable.get(varName);
        return localSymbol != null ? localSymbol : parent.getSymbol(varName);
    }
    
    @Override
    public void defineLocalSymbol(String varName, Class<?> varClz, Object value) {
        symbolTable.put(varName, new AssignableDataValue(varName, value, varClz));
    }
    
    @Override
    public void defineFunction(String functionName, CustomFunction function) {
        functionTable.put(functionName, function);
    }
    
    @Override
    public CustomFunction getFunction(String functionName) {
        CustomFunction function = functionTable.get(functionName);
        return function == null ? parent.getFunction(functionName) : function;
    }
    
    @Override
    public Map<String, CustomFunction> getFunctionTable() {
        return functionTable;
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
        return new QvmBlockScope(this, new HashMap<>(), opStack, exceptionTable);
    }
    
}
