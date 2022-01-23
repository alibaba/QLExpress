package com.alibaba.qlexpress4.parser.tree;

import com.alibaba.qlexpress4.parser.Token;

public class MacroStmt extends Stmt {

    private final Identifier name;

    private final Block body;

    public MacroStmt(Token keyToken, Identifier name, Block body) {
        super(keyToken);
        this.name = name;
        this.body = body;
    }
}
