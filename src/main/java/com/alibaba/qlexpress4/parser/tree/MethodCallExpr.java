package com.alibaba.qlexpress4.parser.tree;

import com.alibaba.qlexpress4.parser.Token;

import java.util.List;

public class MethodCallExpr extends Expr {

    private final Expr objectExpr;

    private final List<Expr> arguments;

    public MethodCallExpr(Token keyToken, Expr objectExpr, List<Expr> arguments) {
        super(keyToken);
        this.objectExpr = objectExpr;
        this.arguments = arguments;
    }

    public Expr getObjectExpr() {
        return objectExpr;
    }

    public List<Expr> getArguments() {
        return arguments;
    }
}
