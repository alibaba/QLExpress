package com.alibaba.qlexpress4.parser.tree;

import com.alibaba.qlexpress4.parser.Token;

import java.util.List;

public class LambdaExpr extends Expr {

    private final List<VarDecl> parameters;

    private final Expr body;

    public LambdaExpr(Token keyToken, List<VarDecl> parameters, Expr body) {
        super(keyToken);
        this.parameters = parameters;
        this.body = body;
    }

    public List<VarDecl> getParameters() {
        return parameters;
    }

    public Expr getBody() {
        return body;
    }

    @Override
    public <R, C> R accept(QLProgramVisitor<R, C> visitor, C context) {
        return visitor.visit(this, context);
    }

}
