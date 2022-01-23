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
}
