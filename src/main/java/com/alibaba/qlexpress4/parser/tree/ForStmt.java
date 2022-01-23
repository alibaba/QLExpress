package com.alibaba.qlexpress4.parser.tree;

import com.alibaba.qlexpress4.parser.Token;

public class ForStmt extends Stmt {

    private final Expr forInit;

    private final Expr condition;

    private final Expr forUpdate;

    private final Stmt body;

    public ForStmt(Token keyToken, Expr forInit, Expr condition, Expr forUpdate, Stmt body) {
        super(keyToken);
        this.forInit = forInit;
        this.condition = condition;
        this.forUpdate = forUpdate;
        this.body = body;
    }
}
