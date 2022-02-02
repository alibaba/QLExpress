package com.alibaba.qlexpress4.parser.tree;

public class VarDecl {

    private final DeclType type;

    private final Identifier variable;

    public VarDecl(DeclType type, Identifier variable) {
        this.type = type;
        this.variable = variable;
    }

    public DeclType getType() {
        return type;
    }

    public Identifier getVariable() {
        return variable;
    }

}
