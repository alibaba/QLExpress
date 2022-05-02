package com.alibaba.qlexpress4.parser.tree;

import com.alibaba.qlexpress4.parser.Token;

public class TernaryExpr extends Expr {

    private final Expr condition;

    private final Expr thenExpr;

    private final Expr elseExpr;

    public TernaryExpr(Token keyToken, Expr condition, Expr thenExpr, Expr elseExpr) {
        super(keyToken);
        this.condition = condition;
        this.thenExpr = thenExpr;
        this.elseExpr = elseExpr;
    }

    public Expr getCondition() {
        return condition;
    }

    public Expr getThenExpr() {
        return thenExpr;
    }

    public Expr getElseExpr() {
        return elseExpr;
    }

    @Override
    public <R> R accept(QLProgramVisitor<R, ?> visitor) {
        return visitor.visit(this);
    }
}
