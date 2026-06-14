package com.alibaba.qlexpress4.aparser;

public interface ParseTree {
    
    <T> T accept(QLParserBaseVisitor<T> visitor);
    
    String getText();
}
