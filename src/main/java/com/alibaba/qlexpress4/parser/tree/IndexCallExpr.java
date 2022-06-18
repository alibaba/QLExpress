package com.alibaba.qlexpress4.parser.tree;

import com.alibaba.qlexpress4.parser.Token;

/**
 * m[1], m['a'] etc.
 */
public class IndexCallExpr extends Expr {

    private final Expr target;

    private final Expr index;

    public IndexCallExpr(Token keyToken, Expr target, Expr index) {
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
