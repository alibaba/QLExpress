package com.alibaba.qlexpress4.exception;

/**
 * Author: DQinYuan
 */
public class QLSyntaxException extends QLException {
    protected QLSyntaxException(String message, int lineNo, int colNo, String errLexeme, String reason, String errorCode, String snippet) {
        super(message, lineNo, colNo, errLexeme, reason, errorCode, snippet);
    }
}
