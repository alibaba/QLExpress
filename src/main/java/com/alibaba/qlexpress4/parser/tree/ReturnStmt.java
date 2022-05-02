package com.alibaba.qlexpress4.parser.tree;

import com.alibaba.qlexpress4.parser.Token;

public class ReturnStmt extends Stmt {

    private final Expr expr;

    public ReturnStmt(Token keyToken, Expr expr) {
        super(keyToken);
        this.expr = expr;
    }

    @Override
    public <R> R accept(QLProgramVisitor<R, ?> visitor) {
        return visitor.visit(this);
    }
}
