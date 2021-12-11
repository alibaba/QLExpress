package com.ql.util.express.exception;

/**
 * 设置了timeoutMills造成的超时异常
 *
 * @author tianqiao@taobao.com
 * @since 2019/6/18 10:36 AM
 */
public class QLTimeoutException1 extends QLException {

    public QLTimeoutException1() {
    }

    public QLTimeoutException1(String message) {
        super(message);
    }
}
