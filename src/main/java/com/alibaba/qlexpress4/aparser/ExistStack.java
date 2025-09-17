package com.alibaba.qlexpress4.aparser;

public interface ExistStack {
    
    ExistStack push();
    
    ExistStack pop();
    
    boolean exist(String varName);
    
    void add(String varName);
}
