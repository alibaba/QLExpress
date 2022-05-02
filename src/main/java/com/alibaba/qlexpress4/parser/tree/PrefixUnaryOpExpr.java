package com.alibaba.qlexpress4.parser.tree;

import com.alibaba.qlexpress4.parser.Token;

public class PrefixUnaryOpExpr extends Expr {

    private final Expr expr;

    public PrefixUnaryOpExpr(Token keyToken, Expr expr) {
        super(keyToken);
        this.expr = expr;
    }

    public Expr getExpr() {
        return expr;
    }

    @Override
    public <R> R accept(QLProgramVisitor<R, ?> visitor) {
        return visitor.visit(this);
    }
}
