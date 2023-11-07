package com.alibaba.qlexpress4.exception;

import com.alibaba.qlexpress4.exception.lsp.Diagnostic;

/**
 * Author: DQinYuan
 */
public class QLSyntaxException extends QLException {
    protected QLSyntaxException(String message, Diagnostic diagnostic) {
        super(message, diagnostic);
    }
}
