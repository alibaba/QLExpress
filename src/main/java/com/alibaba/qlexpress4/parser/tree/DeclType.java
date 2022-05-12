package com.alibaba.qlexpress4.parser.tree;

import com.alibaba.qlexpress4.parser.Token;

import java.util.List;

public class DeclType {

    /**
     * not empty
     */
    private final Token keyToken;

    /**
     * not empty
     */
    private final Class<?> clz;

    private final List<DeclTypeArgument> typeArguments;

    public DeclType(Token keyToken, Class<?> clz, List<DeclTypeArgument> typeArguments) {
        this.keyToken = keyToken;
        this.clz = clz;
        this.typeArguments = typeArguments;
    }

    public Token getKeyToken() {
        return keyToken;
    }

    public Class<?> getClz() {
        return clz;
    }

    public List<DeclTypeArgument> getTypeArguments() {
        return typeArguments;
    }
}
