package com.alibaba.qlexpress4.parser.tree;

public class VarDecl {

    private final Identifier type;

    private final Identifier variable;

    public VarDecl(Identifier type, Identifier variable) {
        this.type = type;
        this.variable = variable;
    }

    public Identifier getType() {
        return type;
    }

    public Identifier getVariable() {
        return variable;
    }

}
