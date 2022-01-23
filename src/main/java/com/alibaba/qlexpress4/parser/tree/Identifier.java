package com.alibaba.qlexpress4.parser.tree;

import com.alibaba.qlexpress4.parser.Token;

public class Identifier extends SyntaxNode {
    public Identifier(Token keyToken) {
        super(keyToken);
    }
}
