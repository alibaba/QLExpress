package com.alibaba.qlexpress4.parser.tree;

import java.util.List;

public class Program {

    private final StmtList stmtList;

    public Program(StmtList stmtList) {
        this.stmtList = stmtList;
    }

    public StmtList getStmtList() {
        return stmtList;
    }

    public <R, C> R accept(QLProgramVisitor<R, C> visitor, C context) {
        return visitor.visit(this, context);
    }
}
