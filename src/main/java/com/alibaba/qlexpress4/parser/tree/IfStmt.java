package com.alibaba.qlexpress4.parser.tree;

import com.alibaba.qlexpress4.parser.Token;

public class IfStmt extends Stmt {

    private final Expr condition;

    private final Stmt thenBranch;

    private final Stmt elseBranch;

    public IfStmt(Token keyToken, Expr condition, Stmt thenBranch, Stmt elseBranch) {
        super(keyToken);
        this.condition = condition;
        this.thenBranch = thenBranch;
        this.elseBranch = elseBranch;
    }

    @Override
    public <R, C> R accept(QLProgramVisitor<R, C> visitor, C context) {
        return visitor.visit(this, context);
    }

    public Expr getCondition() {
        return condition;
    }

    public Stmt getThenBranch() {
        return thenBranch;
    }

    public Stmt getElseBranch() {
        return elseBranch;
    }
}
