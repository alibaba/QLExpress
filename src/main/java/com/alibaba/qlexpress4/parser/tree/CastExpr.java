package com.alibaba.qlexpress4.parser.tree;

import com.alibaba.qlexpress4.parser.Token;

public class CastExpr extends Expr {

    private final Identifier castTarget;

    private final Expr expr;

    public CastExpr(Token keyToken, Identifier castTarget, Expr expr) {
        super(keyToken);
        this.castTarget = castTarget;
        this.expr = expr;
    }

    public Identifier getCastTarget() {
        return castTarget;
    }

    public Expr getExpr() {
        return expr;
    }
}
