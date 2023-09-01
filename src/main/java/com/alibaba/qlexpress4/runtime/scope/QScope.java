package com.alibaba.qlexpress4.runtime.scope;

import com.alibaba.qlexpress4.runtime.*;
import com.alibaba.qlexpress4.runtime.function.QFunction;

/**
 * Author: DQinYuan
 */
public interface QScope {

    /**
     * get assignable symbol variable by name
     * @param varName variable name
     * @return value, null if not exist
     */
    Value getSymbol(String varName);

    /**
     * get symbol variable value by name
     * @param varName variable name
     * @return inner value, null if not exist
     */
    default Object getSymbolValue(String varName) {
        Value symbolVal = getSymbol(varName);
        return symbolVal == null? null: symbolVal.get();
    }

    /**
     * define a symbol in local scope
     * @param varName
     * @param varClz
     * @param value init value
     */
    void defineLocalSymbol(String varName, Class<?> varClz, Object value);

    /**
     * define local function in scope
     * @param functionName
     * @param function
     */
    void defineFunction(String functionName, QFunction function);

    /**
     * get function or lambda define
     * @param functionName
     * @return null if not exist
     */
    QFunction getFunction(String functionName);

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
     * return top element of stack without pop
     * @return
     */
    Value peek();

    /**
     * @return parent scope
     */
    QScope getParent();

    /**
     * @return new scope base origin stack
     */
    QScope newScope();
}
