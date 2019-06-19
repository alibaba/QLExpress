package com.ql.util.express.exception;

/**
 * 非QLExpress框架捕获的业务系统代码的异常
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
