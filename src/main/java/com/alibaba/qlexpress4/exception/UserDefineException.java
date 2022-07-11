package com.alibaba.qlexpress4.exception;

/**
 * user define error message for custom function/operator
 * Author: DQinYuan
 */
public class UserDefineException extends Exception {

    public UserDefineException(String message) {
        super(message);
    }

}
