package com.alibaba.qlexpress4.parser.tree;

import com.alibaba.qlexpress4.parser.Token;

public class AssignExpr extends Expr {

    private final Expr left;

    private final Expr right;

    public AssignExpr(Token keyToken, Expr left, Expr right) {
        super(keyToken);
        this.left = left;
        this.right = right;
    }
}
