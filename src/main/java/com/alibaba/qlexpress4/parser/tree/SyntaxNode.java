package com.alibaba.qlexpress4.parser.tree;

import com.alibaba.qlexpress4.parser.Token;

public abstract class SyntaxNode {

    /**
     * key token in current syntax
     */
    private final Token keyToken;

    public SyntaxNode(Token keyToken) {
        this.keyToken = keyToken;
    }

    public Token getKeyToken() {
        return keyToken;
    }

    public abstract <R, C> R accept(QLProgramVisitor<R, C> visitor, C context);
}
