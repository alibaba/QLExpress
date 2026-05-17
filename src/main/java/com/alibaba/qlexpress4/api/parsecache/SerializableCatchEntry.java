package com.alibaba.qlexpress4.api.parsecache;

public class SerializableCatchEntry {
    private String exceptionClassName;
    
    private SerializableLambdaDefinition handler;
    
    public SerializableCatchEntry() {
    }
    
    public String getExceptionClassName() {
        return exceptionClassName;
    }
    
    public void setExceptionClassName(String exceptionClassName) {
        this.exceptionClassName = exceptionClassName;
    }
    
    public SerializableLambdaDefinition getHandler() {
        return handler;
    }
    
    public void setHandler(SerializableLambdaDefinition handler) {
        this.handler = handler;
    }
}
