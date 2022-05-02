package com.alibaba.qlexpress4.parser.tree;

import com.alibaba.qlexpress4.parser.Token;

public class IdExpr extends Expr {
    public IdExpr(Token keyToken) {
        super(keyToken);
    }

    @Override
    public <R> R accept(QLProgramVisitor<R, ?> visitor) {
        return visitor.visit(this);
    }
}
