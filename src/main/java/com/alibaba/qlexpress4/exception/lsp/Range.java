package com.alibaba.qlexpress4.exception.lsp;

/**
 * Author: DQinYuan
 */
public class Range {
    
    private final Position start;
    
    private final Position end;
    
    public Range(Position start, Position end) {
        this.start = start;
        this.end = end;
    }
    
    public Position getStart() {
        return start;
    }
    
    public Position getEnd() {
        return end;
    }
}
