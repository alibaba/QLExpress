package com.alibaba.qlexpress4.api.parsecache;

public class SerializableSource {
    private int start;
    
    private int line;
    
    private int col;
    
    private String lexeme;
    
    public SerializableSource() {
    }
    
    public int getStart() {
        return start;
    }
    
    public void setStart(int start) {
        this.start = start;
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
    
    public String getLexeme() {
        return lexeme;
    }
    
    public void setLexeme(String lexeme) {
        this.lexeme = lexeme;
    }
}
