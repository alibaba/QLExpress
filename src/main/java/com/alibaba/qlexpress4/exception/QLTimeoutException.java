package com.alibaba.qlexpress4.exception;

import com.alibaba.qlexpress4.exception.lsp.Diagnostic;

public class QLTimeoutException extends QLRuntimeException {
    protected QLTimeoutException(Object catchObj, String reason, String errorCode) {
        super(catchObj, reason, errorCode);
    }
    protected QLTimeoutException(Object catchObj, String message, Diagnostic diagnostic) {
        super(catchObj, message, diagnostic);
    }
}
