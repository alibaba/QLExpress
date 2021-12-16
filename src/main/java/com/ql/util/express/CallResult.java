package com.ql.util.express;

public class CallResult {
    private Object returnValue;
    private boolean isExit;

    public CallResult(Object returnValue, boolean isExit) {
        this.initial(returnValue, isExit);
    }

    public void initial(Object returnValue, boolean isExit) {
        this.returnValue = returnValue;
        this.isExit = isExit;
    }

    public void clear() {
        this.returnValue = null;
        this.isExit = false;
    }

    public Object getReturnValue() {
        return returnValue;
    }

    public boolean isExit() {
        return isExit;
    }
}

