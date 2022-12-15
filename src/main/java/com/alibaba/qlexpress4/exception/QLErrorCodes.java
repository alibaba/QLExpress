package com.alibaba.qlexpress4.exception;

/**
 * Author: DQinYuan
 */
public enum QLErrorCodes {
    INVALID_INDEX("index can only be number"),
    INDEX_OUT_BOUND("index out of bound"),
    MISSING_SEMI_AT_STATEMENT("missing ';' at the end of statement");

    private final String errorMsg;

    QLErrorCodes(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public String getErrorMsg() {
        return errorMsg;
    }
}
