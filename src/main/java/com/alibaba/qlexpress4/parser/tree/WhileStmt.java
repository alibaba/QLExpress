package com.alibaba.qlexpress4.parser.tree;

import com.alibaba.qlexpress4.parser.Token;

public class WhileStmt extends Stmt {

    private final Expr condition;

    private final Stmt body;

    public WhileStmt(Token keyToken, Expr condition, Stmt body) {
        super(keyToken);
        this.condition = condition;
        this.body = body;
    }
}
