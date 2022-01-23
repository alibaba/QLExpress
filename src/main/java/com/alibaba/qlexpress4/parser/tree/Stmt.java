package com.alibaba.qlexpress4.parser.tree;

import com.alibaba.qlexpress4.parser.Token;

public abstract class Stmt extends SyntaxNode {
    public Stmt(Token keyToken) {
        super(keyToken);
    }
}
