package com.ql.util.express.exception;

/**
 * 设置了timeoutMills造成的超时异常
 * @author tianqiao@taobao.com
 * @since 2019/6/18 10:36 AM
 */
public class QLTimeOutException extends QLException {

    public QLTimeOutException() {
    }

    public QLTimeOutException(String message) {
        super(message);
    }
}
