package com.ql.util.express.exception;

/**
 * 系统安全相关异常（比如调用操作系统命令等）
 * @author tianqiao@taobao.com
 * @since 2019/6/18 10:36 AM
 */
public class QLSecurityRiskException extends QLException {

    public QLSecurityRiskException() {
    }

    public QLSecurityRiskException(String message) {
        super(message);
    }
}
