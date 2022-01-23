package com.alibaba.qlexpress4.parser.tree;

import com.alibaba.qlexpress4.parser.Token;

import java.util.List;

public class FunctionStmt extends Stmt {

    private final Identifier name;

    private final List<VarDecl> params;

    private final Block body;

    public FunctionStmt(Token keyToken, Identifier name, List<VarDecl> params, Block body) {
        super(keyToken);
        this.name = name;
        this.params = params;
        this.body = body;
    }

    public Identifier getName() {
        return name;
    }

    public List<VarDecl> getParams() {
        return params;
    }

    public Block getBody() {
        return body;
    }
}
