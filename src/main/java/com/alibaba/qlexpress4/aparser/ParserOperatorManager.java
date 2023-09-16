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
     * @param lexeme lexeme
     * @param opType type of operator
     * @return true if lexeme is opType
     */
    boolean isOpType(String lexeme, OpType opType);

    /**
     * get binary operator precedence
     * @param lexeme lexeme
     * @return null if lexeme not a operator
     */
    Integer precedence(String lexeme);

}
