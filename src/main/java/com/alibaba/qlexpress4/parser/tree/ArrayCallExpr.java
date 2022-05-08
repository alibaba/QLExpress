package com.alibaba.qlexpress4.parser.tree;

import com.alibaba.qlexpress4.parser.Token;

public class ArrayCallExpr extends Expr {

    private final Expr target;

    private final Expr index;

    public ArrayCallExpr(Token keyToken, Expr target, Expr index) {
        super(keyToken);
        this.target = target;
        this.index = index;
    }

    public Expr getTarget() {
        return target;
    }

    public Expr getIndex() {
        return index;
    }

    @Override
    public <R, C> R accept(QLProgramVisitor<R, C> visitor, C context) {
        return visitor.visit(this, context);
    }
}
