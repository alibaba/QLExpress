package com.alibaba.qlexpress4.exception;

import com.alibaba.qlexpress4.parser.Token;

public interface ExceptionFactory<T> {

    T newException(String message, int lineNo, int colNo, String errLexeme,
                   String errorCode, String reason, String snippet);

}
