package com.alibaba.qlexpress4.parser.tree;

import com.alibaba.qlexpress4.parser.Token;

/**
 * `;`
 */
public class EmptyStmt extends Stmt {
    public EmptyStmt(Token keyToken) {
        super(keyToken);
    }

    @Override
    public <R, C> R accept(QLProgramVisitor<R, C> visitor, C context) {
        return null;
    }
}
