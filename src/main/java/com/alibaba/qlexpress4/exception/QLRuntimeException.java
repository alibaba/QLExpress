package com.alibaba.qlexpress4.exception;

import com.alibaba.qlexpress4.exception.lsp.Diagnostic;
import com.alibaba.qlexpress4.exception.lsp.Range;

public class QLRuntimeException extends QLException {
    
    /**
     * catchObj can be catched at QLExpress catch clause
     */
    private final Object catchObj;
    
    /*
     * Visible for test
     */
    protected QLRuntimeException(Object catchObj, String reason, String errorCode) {
        super("", new Diagnostic(0, new Range(null, null), "", errorCode, reason, ""));
        this.catchObj = catchObj;
    }
    
    protected QLRuntimeException(Object catchObj, String message, Diagnostic diagnostic) {
        super(message, diagnostic);
        this.catchObj = catchObj;
        if (catchObj instanceof Throwable) {
            super.initCause((Throwable)catchObj);
        }
    }
    
    public Object getCatchObj() {
        return catchObj;
    }
}
