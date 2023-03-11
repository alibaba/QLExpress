package com.alibaba.qlexpress4.parser.tree;

import com.alibaba.qlexpress4.parser.Token;

public class ReturnStmt extends Stmt {

    /**
     * Nullable
     */
    private final Expr expr;

    public ReturnStmt(Token keyToken, Expr expr) {
        super(keyToken);
        this.expr = expr;
    }

    public Expr getExpr() {
        return expr;
    }

    @Override
    public <R, C> R accept(QLProgramVisitor<R, C> visitor, C context) {
        return visitor.visit(this, context);
    }
}
