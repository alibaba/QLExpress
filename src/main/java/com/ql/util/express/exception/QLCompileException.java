package com.ql.util.express.exception;

/**
 * 编译器的异常信息
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
