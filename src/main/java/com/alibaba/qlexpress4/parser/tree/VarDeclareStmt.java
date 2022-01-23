package com.alibaba.qlexpress4.parser.tree;

import com.alibaba.qlexpress4.parser.Token;

public class VarDeclareStmt extends Stmt {

    private final Identifier type;

    private final Identifier name;

    /**
     * optional
     */
    private final Expr initializer;

    public VarDeclareStmt(Token keyToken, Identifier type, Identifier name, Expr initializer) {
        super(keyToken);
        this.type = type;
        this.name = name;
        this.initializer = initializer;
    }

    public Identifier getType() {
        return type;
    }

    public Identifier getName() {
        return name;
    }

    public Expr getInitializer() {
        return initializer;
    }
}
