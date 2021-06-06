package com.ql.util.express.exception;

/**
 * Compiler exception information
 * @author tianqiao@taobao.com
 * @since 2019/6/18 2:13 PM
 */
public class QLCompileException extends Exception {

    public QLCompileException() {
    }

    public QLCompileException(String message) {
        super(message);
    }

    public QLCompileException(String message, Throwable cause) {
        super(message, cause);
    }
}
