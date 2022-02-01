package com.alibaba.qlexpress4.parser.tree;

import com.alibaba.qlexpress4.parser.Token;

import java.util.List;

public class NewExpr extends Expr {

    private final Identifier clazz;

    private final List<Expr> arguments;

    public NewExpr(Token keyToken, Identifier clazz, List<Expr> arguments) {
        super(keyToken);
        this.clazz = clazz;
        this.arguments = arguments;
    }

    public Identifier getClazz() {
        return clazz;
    }

    public List<Expr> getArguments() {
        return arguments;
    }
}
