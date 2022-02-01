package com.alibaba.qlexpress4.parser.tree;

import com.alibaba.qlexpress4.parser.Token;

public class LocalVarDeclareStmt extends Stmt {

    private final VarDecl varDecl;

    /**
     * optional
     */
    private final Expr initializer;

    public LocalVarDeclareStmt(Token keyToken, VarDecl varDecl, Expr initializer) {
        super(keyToken);
        this.varDecl = varDecl;
        this.initializer = initializer;
    }

    public VarDecl getVarDecl() {
        return varDecl;
    }

    public Expr getInitializer() {
        return initializer;
    }
}
