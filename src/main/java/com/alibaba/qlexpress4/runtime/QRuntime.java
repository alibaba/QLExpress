package com.alibaba.qlexpress4.runtime;

/**
 * Author: DQinYuan
 */
public interface QRuntime {

    /**
     * get assignable symbol variable by name
     * @param varName variable name
     * @return assignable value
     */
    LeftValue getSymbol(String varName);

    /**
     * get immutable symbol variable value by name
     * @param varName variable name
     * @return immutable value
     */
    Value getSymbolValue(String varName);

    /**
     * define a symbol
     * for example, `Number a = 10`
     *              define("a", Number.class, new Value(10))
     * @param varName symbol name
     * @param varClz symbol clz, declare clz, not real clz
     * @param value symbol init value
     */
    void defineSymbol(String varName, Class<?> varClz, Value value);

    /**
     * push value on the top of stack
     * @param value pushed element
     */
    void push(Value value);

    /**
     * pop number elements on top of stack
     * @param number pop elements' number
     * @return popped elements
     */
    Parameters pop(int number);

    /**
     * pop one element on top of stack
     * @return popped element
     */
    Value pop();

    /**
     * exit and return specified value
     * @param returnValue
     */
    void exitAndReturn(QResult returnValue);

    /**
     * duplicate top element of stack
     */
    void dup();

    /**
     * get script start time
     * @return start time
     */
    long scriptStartTimeStamp();

}
