package com.alibaba.qlexpress4.runtime;

/**
 * Author: DQinYuan
 */
public interface QRuntime {

    /**
     * get assignable symbol variable by name
     * @param varName variable name
     * @return assignable value, null if not exist
     */
    LeftValue getSymbol(String varName);

    /**
     * get symbol variable value by name
     * @param varName variable name
     * @return inner value, null if not exist
     */
    Object getSymbolValue(String varName);

    /**
     * define a symbol in global scope
     * for example, `Number a = 10`
     *              define("a", Number.class, new Value(10))
     * @param varName symbol name
     * @param varClz symbol clz, declare clz, not real clz
     */
    LeftValue defineSymbol(String varName, Class<?> varClz);

    /**
     * define a symbol in local scope
     * @param varName
     * @param varClz
     * @param value init value
     */
    void defineLocalSymbol(String varName, Class<?> varClz, Object value);

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
     * cascade return by child runtime result
     * @param cascadeReturnValue child return value
     */
    default void cascadeReturn(QResult cascadeReturnValue) {
        if (QResult.ResultType.RETURN == cascadeReturnValue.getResultType()) {
            exitAndReturn(cascadeReturnValue);
        }
    }

    /**
     * exit and return specified value
     * @param returnValue
     */
    void exitAndReturn(QResult returnValue);

    /**
     * get script start time
     * @return start time
     */
    long scriptStartTimeStamp();

}
