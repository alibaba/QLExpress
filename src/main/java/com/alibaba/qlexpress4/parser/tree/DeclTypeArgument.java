package com.alibaba.qlexpress4.parser.tree;

public class DeclTypeArgument {

    public enum Bound {NONE, SUPER, EXTENDS}

    private final DeclType type;

    private final Bound bound;

    public DeclTypeArgument(DeclType type, Bound bound) {
        this.type = type;
        this.bound = bound;
    }

    public DeclType getType() {
        return type;
    }

    public Bound getBound() {
        return bound;
    }
}
