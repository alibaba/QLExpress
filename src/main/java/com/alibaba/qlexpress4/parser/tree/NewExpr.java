package com.alibaba.qlexpress4.parser.tree;

import com.alibaba.qlexpress4.parser.Token;

import java.util.List;

public class NewExpr extends Expr {

    private final DeclType clazz;

    private final List<Expr> arguments;

    public NewExpr(Token keyToken, DeclType clazz, List<Expr> arguments) {
        super(keyToken);
        this.clazz = clazz;
        this.arguments = arguments;
    }

    public DeclType getClazz() {
        return clazz;
    }

    public List<Expr> getArguments() {
        return arguments;
    }

    @Override
    public <R, C> R accept(QLProgramVisitor<R, C> visitor, C context) {
        return visitor.visit(this, context);
    }
}
