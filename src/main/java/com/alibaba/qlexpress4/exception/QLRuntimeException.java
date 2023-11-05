package com.alibaba.qlexpress4.exception;

public class QLRuntimeException extends QLException {

    /**
     * catchObj can be catched at QLExpress catch clause
     */
    private final Object catchObj;

    /*
     * Visible for test
     */
    protected QLRuntimeException(Object catchObj, String reason, String errorCode) {
        super("", 0, 0, 0, "", reason, errorCode, "");
        this.catchObj = catchObj;
    }

    protected QLRuntimeException(Object catchObj, String message, int pos, int lineNo, int colNo,
                                 String errLexeme, String reason, String errorCode, String snippet) {
        super(message, pos, lineNo, colNo, errLexeme, reason, errorCode, snippet);
        this.catchObj = catchObj;
        if (catchObj instanceof Throwable) {
            super.initCause((Throwable) catchObj);
        }
    }

    public Object getCatchObj() {
        return catchObj;
    }
}
