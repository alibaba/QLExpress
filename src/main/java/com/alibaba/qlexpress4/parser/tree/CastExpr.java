package com.alibaba.qlexpress4.parser.tree;

import com.alibaba.qlexpress4.parser.Token;

/**
 * force type cast
 */
public class CastExpr extends Expr {

    private final Expr typeExpr;

    private final Expr target;

    public CastExpr(Token keyToken, Expr typeExpr, Expr target) {
        super(keyToken);
        this.typeExpr = typeExpr;
        this.target = target;
    }

    public Expr getTypeExpr() {
        return typeExpr;
    }

    public Expr getTarget() {
        return target;
    }

    @Override
    public <R, C> R accept(QLProgramVisitor<R, C> visitor, C context) {
        return visitor.visit(this, context);
    }
}
