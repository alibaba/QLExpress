package com.alibaba.qlexpress4.parser.tree;

import com.alibaba.qlexpress4.parser.Token;

public class TypeExpr extends Expr {

    private final DeclType declType;

    public TypeExpr(Token keyToken, DeclType declType) {
        super(keyToken);
        this.declType = declType;
    }

    public DeclType getDeclType() {
        return declType;
    }
}
