package com.alibaba.qlexpress4.api.parsecache;

public class SerializableConstant {
    private String type;
    
    private Object value;
    
    public SerializableConstant() {
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public Object getValue() {
        return value;
    }
    
    public void setValue(Object value) {
        this.value = value;
    }
}
