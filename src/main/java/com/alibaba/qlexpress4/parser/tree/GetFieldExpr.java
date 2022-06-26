package com.alibaba.qlexpress4.parser.tree;

import com.alibaba.qlexpress4.parser.Token;

public class GetFieldExpr extends Expr {

    private final Expr expr;

    private final Identifier attribute;

    public GetFieldExpr(Token keyToken, Expr expr, Identifier attribute) {
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
    public <R, C> R accept(QLProgramVisitor<R, C> visitor, C context) {
        return visitor.visit(this, context);
    }
}
