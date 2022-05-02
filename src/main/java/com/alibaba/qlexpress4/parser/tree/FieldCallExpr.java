package com.alibaba.qlexpress4.parser.tree;

import com.alibaba.qlexpress4.parser.Token;

public class FieldCallExpr extends Expr {

    private final Expr expr;

    private final Identifier attribute;

    public FieldCallExpr(Token keyToken, Expr expr, Identifier attribute) {
        super(keyToken);
        this.expr = expr;
        this.attribute = attribute;
    }

    public Expr getExpr() {
        return expr;
    }

    public Identifier getAttribute() {
        return attribute;
    }

    @Override
    public <R> R accept(QLProgramVisitor<R, ?> visitor) {
        return visitor.visit(this);
    }
}
