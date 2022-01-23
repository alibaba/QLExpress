package com.alibaba.qlexpress4.parser.tree;

import com.alibaba.qlexpress4.parser.Token;

public abstract class Expr extends Stmt {
    public Expr(Token keyToken) {
        super(keyToken);
    }
}
