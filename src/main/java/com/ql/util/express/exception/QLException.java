package com.ql.util.express.exception;

/**
 * QLExpress的框架执行过程中捕获的异常
 *
 * @author tianqiao@taobao.com
 * @since 2019/6/18 2:13 PM
 */
public class QLException extends Exception {
    private static final long serialVersionUID = -1861857045313408218L;

    public QLException() {
    }

    public QLException(String message) {
        super(message);
    }

    public QLException(String message, Throwable cause) {
        super(message, cause);
    }
}
