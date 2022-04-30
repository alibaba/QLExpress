package com.alibaba.qlexpress4.parser.tree;

public class VarDecl {

    /**
     * null when variable without type declare
     */
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
