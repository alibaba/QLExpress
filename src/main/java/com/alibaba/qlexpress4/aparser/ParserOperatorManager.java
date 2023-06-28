package com.alibaba.qlexpress4.aparser;

/**
 * Author: DQinYuan
 */
public interface ParserOperatorManager {

    enum OpType {
        PREFIX,
        SUFFIX,
        MIDDLE
    }

    /**
     * judge lexeme is opType or not
     * @param lexeme
     * @param opType
     * @return
     */
    boolean isOpType(String lexeme, OpType opType);

    /**
     * get binary operator precedence
     * @param lexeme
     * @return null if lexeme not a operator
     */
    Integer precedence(String lexeme);

}
