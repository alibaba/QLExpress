package com.ql.util.express.exception;

/**
 * 编译器的异常信息
 *
 * @author tianqiao@taobao.com
 * @since 2019/6/18 2:13 PM
 */
public class QLCompileException extends Exception {
    private static final long serialVersionUID = -4743114416550746038L;

    public QLCompileException() {
    }

    public QLCompileException(String message) {
        super(message);
    }

    public QLCompileException(String message, Throwable cause) {
        super(message, cause);
    }
}
