package com.ql.util.express.exception;

/**
 * 非QLExpress框架捕获的业务系统代码的异常
 *
 * @author tianqiao@taobao.com
 * @since 2019/6/18 2:13 PM
 */
public class QLBizException extends Exception {
    private static final long serialVersionUID = -5602081330453002691L;

    public QLBizException() {
    }

    public QLBizException(String message) {
        super(message);
    }

    public QLBizException(String message, Throwable cause) {
        super(message, cause);
    }
}
