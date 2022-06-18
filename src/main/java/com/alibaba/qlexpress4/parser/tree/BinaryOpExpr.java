package com.alibaba.qlexpress4.parser.tree;

import com.alibaba.qlexpress4.parser.Token;

public class BinaryOpExpr extends Expr {

    private final Expr left;

    private final Expr right;

    public BinaryOpExpr(Token keyToken, Expr left, Expr right) {
        super(keyToken);
        this.left = left;
        this.right = right;
    }

    public Expr getLeft() {
        return left;
    }

    public Expr getRight() {
        return right;
    }

    @Override
    public <R, C> R accept(QLProgramVisitor<R, C> visitor, C context) {
        return visitor.visit(this, context);
    }
}
