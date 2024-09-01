package com.alibaba.qlexpress4.exception;

/**
 * Author: DQinYuan
 */
public enum QLErrorCodes {
    INVALID_INDEX("index can only be number"),
    INDEX_OUT_BOUND("index out of bound"),
    NONINDEXABLE_OBJECT("object of class %s is not indexable"),
    NONTRAVERSABLE_OBJECT("object of class %s is not traversable"),
    GET_FIELD_FROM_NULL("can not get field from null"),
    GET_METHOD_FROM_NULL("can not get method from null"),
    FIELD_NOT_FOUND("'%s' field not found"),
    MISSING_SEMI_AT_STATEMENT("missing ';' at the end of statement"),
    BREAK_CONTINUE_OUTSIDE_LOOP("break/continue must in loop");

    private final String errorMsg;

    QLErrorCodes(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public String getErrorMsg() {
        return errorMsg;
    }
}
