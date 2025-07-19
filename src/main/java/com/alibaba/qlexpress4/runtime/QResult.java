package com.alibaba.qlexpress4.runtime;

/**
 * Author: DQinYuan
 */
public class QResult {
    
    public static final QResult LOOP_BREAK_RESULT = new QResult(Value.NULL_VALUE, ResultType.BREAK);
    
    public static final QResult LOOP_CONTINUE_RESULT = new QResult(Value.NULL_VALUE, ResultType.CONTINUE);
    
    public static final QResult NEXT_INSTRUCTION = new QResult(Value.NULL_VALUE, ResultType.NEXT_INSTRUCTION);
    
    public enum ResultType {
        // break
        BREAK,
        // without return, different with return null
        CONTINUE,
        // jump to other instruction position.
        // in this case, result is Value of int, which is the position to be jumped
        JUMP,
        // return from function/lambda/script
        RETURN,
        // execute next instruction
        NEXT_INSTRUCTION
    }
    
    public QResult(Value result, ResultType rType) {
        this.result = result;
        this.resultType = rType;
    }
    
    private final Value result;
    
    private final ResultType resultType;
    
    public Value getResult() {
        return result;
    }
    
    public ResultType getResultType() {
        return resultType;
    }
}
