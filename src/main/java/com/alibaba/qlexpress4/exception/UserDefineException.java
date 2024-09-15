package com.alibaba.qlexpress4.exception;

/**
 * user define error message for custom function/operator
 * Author: DQinYuan
 */
public class UserDefineException extends Exception {

    public enum ExceptionType {
        INVALID_ARGUMENT, BIZ_EXCEPTION
    };

    private final ExceptionType type;

    public UserDefineException(String message) {
        this(ExceptionType.BIZ_EXCEPTION, message);
    }

    public UserDefineException(ExceptionType type, String message) {
        super(message);
        this.type = type;
    }

    public ExceptionType getType() {
        return type;
    }
}
