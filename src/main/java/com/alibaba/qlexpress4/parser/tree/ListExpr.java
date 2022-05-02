package com.alibaba.qlexpress4.parser.tree;

import com.alibaba.qlexpress4.parser.Token;

import java.util.List;

/**
 * list literal expression
 */
public class ListExpr extends Expr {

    private final List<Expr> elements;

    public ListExpr(Token keyToken, List<Expr> elements) {
        super(keyToken);
        this.elements = elements;
    }

    public List<Expr> getElements() {
        return elements;
    }

    @Override
    public <R> R accept(QLProgramVisitor<R, ?> visitor) {
        return visitor.visit(this);
    }
}
