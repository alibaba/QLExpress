package com.alibaba.qlexpress4.parser.tree;

import com.alibaba.qlexpress4.parser.Token;

public class ForStmt extends Stmt {

    /**
     * {@link LocalVarDeclareStmt} or {@link Expr}
     * nullable
     */
    private final Stmt forInit;

    /**
     * nullable
     */
    private final Expr condition;

    /**
     * nullable
     */
    private final Expr forUpdate;

    private final Stmt body;

    public ForStmt(Token keyToken, Stmt forInit, Expr condition, Expr forUpdate, Stmt body) {
        super(keyToken);
        this.forInit = forInit;
        this.condition = condition;
        this.forUpdate = forUpdate;
        this.body = body;
    }

    public Stmt getForInit() {
        return forInit;
    }

    public Expr getCondition() {
        return condition;
    }

    public Expr getForUpdate() {
        return forUpdate;
    }

    public Stmt getBody() {
        return body;
    }

    @Override
    public <R, C> R accept(QLProgramVisitor<R, C> visitor, C context) {
        return visitor.visit(this, context);
    }
}
