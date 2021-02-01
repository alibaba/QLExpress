package com.ql.util.express.exception;

/**
 * The exception caught during the execution of the QLExpress framework
 * @author tianqiao@taobao.com
 * @since 2019/6/18 2:13 PM
 */
public class QLException extends Exception {

    public QLException() {
    }

    public QLException(String message) {
        super(message);
    }

    public QLException(String message, Throwable cause) {
        super(message, cause);
    }
}
