package com.alibaba.qlexpress4.parser.tree;

import com.alibaba.qlexpress4.parser.Token;

import java.util.List;

public class Block extends Expr {

    private final StmtList stmtList;

    public Block(Token keyToken, StmtList stmtList) {
        super(keyToken);
        this.stmtList = stmtList;
    }

    public StmtList getStmtList() {
        return stmtList;
    }

    @Override
    public <R, C> R accept(QLProgramVisitor<R, C> visitor, C context) {
        return visitor.visit(this, context);
    }
}
