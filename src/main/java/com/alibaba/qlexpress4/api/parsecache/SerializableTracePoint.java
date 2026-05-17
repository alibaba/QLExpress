package com.alibaba.qlexpress4.api.parsecache;

import java.util.List;

public class SerializableTracePoint {
    private String type;
    
    private String token;
    
    private List<SerializableTracePoint> children;
    
    private int line;
    
    private int col;
    
    private int position;
    
    public SerializableTracePoint() {
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
    
    public List<SerializableTracePoint> getChildren() {
        return children;
    }
    
    public void setChildren(List<SerializableTracePoint> children) {
        this.children = children;
    }
    
    public int getLine() {
        return line;
    }
    
    public void setLine(int line) {
        this.line = line;
    }
    
    public int getCol() {
        return col;
    }
    
    public void setCol(int col) {
        this.col = col;
    }
    
    public int getPosition() {
        return position;
    }
    
    public void setPosition(int position) {
        this.position = position;
    }
}
