package com.alibaba.qlexpress4.exception;

/**
 * user define error message for custom function/operator
 * Author: DQinYuan
 */
public class UserDefineException extends Exception {

    public static final String INVALID_ARGUMENT = "INVALID_ARGUMENT";

    public static final String BIZ_EXCEPTION = "BIZ_EXCEPTION";

    private final String type;

    public UserDefineException(String message) {
        this(BIZ_EXCEPTION, message);
    }

    public UserDefineException(String type, String message) {
        super(message);
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
