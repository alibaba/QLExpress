package com.alibaba.qlexpress4.runtime.scope;

import com.alibaba.qlexpress4.runtime.*;
import com.alibaba.qlexpress4.runtime.function.CustomFunction;

import java.util.Map;

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
        return symbolVal == null ? null : symbolVal.get();
    }
    
    /**
     * define a symbol in local scope
     * @param varName variable name
     * @param varClz class of variable
     * @param value init value
     */
    void defineLocalSymbol(String varName, Class<?> varClz, Object value);
    
    /**
     * define local function in scope
     * @param functionName name of function
     * @param function implement of function
     */
    void defineFunction(String functionName, CustomFunction function);
    
    /**
     * get function or lambda define
     * @param functionName name of function
     * @return null if not exist
     */
    CustomFunction getFunction(String functionName);
    
    /**
     * get function table in this scope
     * @return function table
     */
    Map<String, CustomFunction> getFunctionTable();
    
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
     * @return top element of stack without pop
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
