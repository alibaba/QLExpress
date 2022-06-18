package com.alibaba.qlexpress4.exception;

/**
 * Author: DQinYuan
 */
public interface ExceptionFactory<T> {

    T newException(String message, int lineNo, int colNo, String errLexeme,
                   String errorCode, String reason, String snippet);

}
