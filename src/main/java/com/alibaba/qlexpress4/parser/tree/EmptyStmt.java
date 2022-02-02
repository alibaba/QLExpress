package com.alibaba.qlexpress4.parser.tree;

import com.alibaba.qlexpress4.parser.Token;

/**
 * `;`
 */
public class EmptyStmt extends Stmt {
    public EmptyStmt(Token keyToken) {
        super(keyToken);
    }
}
