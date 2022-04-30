package com.alibaba.qlexpress4.parser.tree;

import java.util.List;

public class DeclType {

    /**
     * a.b.c.d
     * not empty
     */
    private final ConstExpr type;

    private final List<DeclTypeArgument> typeArguments;

    public DeclType(ConstExpr type, List<DeclTypeArgument> typeArguments) {
        this.type = type;
        this.typeArguments = typeArguments;
    }

    public ConstExpr getType() {
        return type;
    }

    public List<DeclTypeArgument> getTypeArguments() {
        return typeArguments;
    }
}
