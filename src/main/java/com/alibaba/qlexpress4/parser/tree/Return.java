package com.alibaba.qlexpress4.parser.tree;

import com.alibaba.qlexpress4.parser.Token;

public class Return extends Stmt {

    private final Expr expr;

    public Return(Token keyToken, Expr expr) {
        super(keyToken);
        this.expr = expr;
    }
}
