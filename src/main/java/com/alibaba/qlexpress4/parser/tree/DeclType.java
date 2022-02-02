package com.alibaba.qlexpress4.parser.tree;

import java.util.List;

public class DeclType {

    private final Identifier type;

    private final List<DeclTypeArgument> typeArguments;

    public DeclType(Identifier type, List<DeclTypeArgument> typeArguments) {
        this.type = type;
        this.typeArguments = typeArguments;
    }

    public Identifier getType() {
        return type;
    }

    public List<DeclTypeArgument> getTypeArguments() {
        return typeArguments;
    }
}
