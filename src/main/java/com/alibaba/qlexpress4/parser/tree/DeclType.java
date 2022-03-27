package com.alibaba.qlexpress4.parser.tree;

import java.util.List;

public class DeclType {

    /**
     * a.b.c.d
     * not empty
     */
    private final List<Identifier> type;

    private final List<DeclTypeArgument> typeArguments;

    public DeclType(List<Identifier> type, List<DeclTypeArgument> typeArguments) {
        this.type = type;
        this.typeArguments = typeArguments;
    }

    public List<Identifier> getType() {
        return type;
    }

    public List<DeclTypeArgument> getTypeArguments() {
        return typeArguments;
    }
}
