package com.alibaba.qlexpress4.aparser;

public class Token {
    public static final int EOF = -1;
    
    public static final int EPSILON = -2;
    
    private int type;
    
    private final String text;
    
    private final int startIndex;
    
    private final int stopIndex;
    
    private final int line;
    
    private final int charPositionInLine;
    
    public Token(int type, String text, int startIndex, int stopIndex, int line, int charPositionInLine) {
        this.type = type;
        this.text = text;
        this.startIndex = startIndex;
        this.stopIndex = stopIndex;
        this.line = line;
        this.charPositionInLine = charPositionInLine;
    }
    
    public int getType() {
        return type;
    }
    
    public void setType(int type) {
        this.type = type;
    }
    
    public String getText() {
        return text;
    }
    
    public int getStartIndex() {
        return startIndex;
    }
    
    public int getStopIndex() {
        return stopIndex;
    }
    
    public int getLine() {
        return line;
    }
    
    public int getCharPositionInLine() {
        return charPositionInLine;
    }
    
    @Override
    public String toString() {
        return text;
    }
}
