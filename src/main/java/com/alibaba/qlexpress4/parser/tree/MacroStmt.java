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

    public Identifier getName() {
        return name;
    }

    public Block getBody() {
        return body;
    }

    @Override
    public <R, C> R accept(QLProgramVisitor<R, C> visitor, C context) {
        return visitor.visit(this, context);
    }
}
