package com.alibaba.qlexpress4.parser.tree;

import com.alibaba.qlexpress4.parser.Token;

import java.util.List;

/**
 * Author: DQinYuan
 */
public class StmtList extends SyntaxNode {

    private final List<Stmt> stmts;

    public StmtList(Token keyToken, List<Stmt> stmts) {
        super(keyToken);
        this.stmts = stmts;
    }

    public Stmt get(int i) {
        return stmts.get(i);
    }

    public List<Stmt> getStmts() {
        return stmts;
    }

    @Override
    public <R, C> R accept(QLProgramVisitor<R, C> visitor, C context) {
        return visitor.visit(this, context);
    }
}
