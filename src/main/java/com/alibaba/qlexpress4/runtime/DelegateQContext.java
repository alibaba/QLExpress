package com.alibaba.qlexpress4.runtime;

import com.alibaba.qlexpress4.cache.QLCaches;
import com.alibaba.qlexpress4.runtime.scope.QScope;

import java.util.Map;

/**
 * Author: DQinYuan
 */
public class DelegateQContext implements QContext {

    private final QRuntime qRuntime;

    private QScope qScope;

    public DelegateQContext(QRuntime qRuntime, QScope qScope) {
        this.qRuntime = qRuntime;
        this.qScope = qScope;
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
    public int getBaseIndex() {
        return qScope.getBaseIndex();
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
    public Value peek() {
        return qScope.peek();
    }

    @Override
    public QScope getParent() {
        return qScope.getParent();
    }

    @Override
    public QScope getCurrentScope() {
        return qScope;
    }

    @Override
    public QScope newScope(ExceptionTable exceptionTable, int baseIndex) {
        return qScope = qScope.newScope(exceptionTable, baseIndex);
    }

    @Override
    public int absoluteJump(int relativeJump) {
        return relativeJump + qScope.getBaseIndex();
    }

    @Override
    public Integer toHandlerScope(Object catchObj, QScope until) {
        while (true) {
            Integer relativePos = qScope.exceptionTable().getRelativePos(catchObj);
            if (relativePos != null) {
                return relativePos + qScope.getBaseIndex();
            }
            if (qScope == until) {
                return null;
            }
            closeScope();
        }
    }

    @Override
    public ExceptionTable exceptionTable() {
        return qScope.exceptionTable();
    }

    @Override
    public void closeScope() {
        qScope = qScope.getParent();
    }
}
