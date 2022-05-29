package com.alibaba.qlexpress4.test.instruction;

import com.alibaba.qlexpress4.runtime.*;

/**
 * @Author TaoKan
 * @Date 2022/5/15 上午11:03
 */
public class TestQRuntimeParent implements QRuntime {
    private Value value;
    private Parameters parameters;

    public Value getValue() {
        return value;
    }

    public void setParameters(Parameters parameters) {
        this.parameters = parameters;
    }

    @Override
    public LeftValue getSymbol(String varName) {
        return null;
    }

    @Override
    public Value getSymbolValue(String varName) {
        return null;
    }

    @Override
    public void defineSymbol(String varName, Class<?> varClz, Value value) {

    }

    @Override
    public void defineLocalSymbol(String varName, Class<?> varClz, Value value) {

    }

    @Override
    public void push(Value value) {
        this.value = value;
    }

    @Override
    public Parameters pop(int number) {
        return this.parameters;
    }

    @Override
    public Value pop() {
        return null;
    }

    @Override
    public void exitAndReturn(QResult returnValue) {

    }

    @Override
    public long scriptStartTimeStamp() {
        return 0;
    }
}
