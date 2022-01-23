package com.alibaba.qlexpress4.parser.tree;

import java.util.List;

public class Program {

    private final List<Stmt> stmtList;

    public Program(List<Stmt> stmtList) {
        this.stmtList = stmtList;
    }

    public List<Stmt> getStmtList() {
        return stmtList;
    }
}
