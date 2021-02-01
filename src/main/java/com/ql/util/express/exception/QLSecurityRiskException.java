package com.ql.util.express.exception;

/**
 * System security related exceptions (such as calling operating system commands, etc.)
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
