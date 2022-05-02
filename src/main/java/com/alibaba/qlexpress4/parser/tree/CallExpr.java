package com.alibaba.qlexpress4.parser.tree;

import com.alibaba.qlexpress4.parser.Token;

import java.util.List;

public class CallExpr extends Expr {

    private final Expr target;

    private final List<Expr> arguments;

    public CallExpr(Token keyToken, Expr target, List<Expr> arguments) {
        super(keyToken);
        this.target = target;
        this.arguments = arguments;
    }

    public Expr getTarget() {
        return target;
    }

    public List<Expr> getArguments() {
        return arguments;
    }

    @Override
    public <R> R accept(QLProgramVisitor<R, ?> visitor) {
        return visitor.visit(this);
    }
}
