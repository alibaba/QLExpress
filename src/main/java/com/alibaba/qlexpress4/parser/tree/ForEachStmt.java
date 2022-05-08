package com.alibaba.qlexpress4.parser.tree;

import com.alibaba.qlexpress4.parser.Token;

public class ForEachStmt extends Stmt {

    private final VarDecl itVar;

    private final Expr target;

    private final Stmt body;

    public ForEachStmt(Token keyToken, VarDecl itVar, Expr target, Stmt body) {
        super(keyToken);
        this.itVar = itVar;
        this.target = target;
        this.body = body;
    }

    public VarDecl getItVar() {
        return itVar;
    }

    public Expr getTarget() {
        return target;
    }

    public Stmt getBody() {
        return body;
    }

    @Override
    public <R, C> R accept(QLProgramVisitor<R, C> visitor, C context) {
        return visitor.visit(this, context);
    }
}
