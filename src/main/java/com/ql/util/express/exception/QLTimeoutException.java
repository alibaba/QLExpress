package com.ql.util.express.exception;

/**
 * 设置了timeoutMills造成的超时异常
 *
 * @author tianqiao@taobao.com
 * @since 2019/6/18 10:36 AM
 */
public class QLTimeoutException extends QLException {
    private static final long serialVersionUID = 8386258847642771321L;

    public QLTimeoutException() {
    }

    public QLTimeoutException(String message) {
        super(message);
    }
}
