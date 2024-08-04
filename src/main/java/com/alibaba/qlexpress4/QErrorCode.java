package com.alibaba.qlexpress4;

/**
 * Author: DQinYuan
 */
public enum QErrorCode {
    INVALID_ASSIGNMENT("value %s is not assignable");

    private final String errMsg;

    QErrorCode(String errMsg) {
        this.errMsg = errMsg;
    }

    public String getErrMsg() {
        return errMsg;
    }
}
