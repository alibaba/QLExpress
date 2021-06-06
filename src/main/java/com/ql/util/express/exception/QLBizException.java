package com.ql.util.express.exception;

/**
 * Business system code exceptions captured by non-QLExpress framework
 * @author tianqiao@taobao.com
 * @since 2019/6/18 2:13 PM
 */
public class QLBizException extends Exception {

    public QLBizException() {
    }

    public QLBizException(String message) {
        super(message);
    }

    public QLBizException(String message, Throwable cause) {
        super(message, cause);
    }
}
