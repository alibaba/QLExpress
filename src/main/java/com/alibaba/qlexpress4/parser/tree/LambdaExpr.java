package com.alibaba.qlexpress4.parser.tree;

import com.alibaba.qlexpress4.parser.Token;

import java.util.List;

public class LambdaExpr extends Expr {

    private final List<VarDecl> parameters;

    private final Block blockBody;

    private final Expr exprBody;

    public LambdaExpr(Token keyToken, List<VarDecl> parameters, Block blockBody, Expr exprBody) {
        super(keyToken);
        this.parameters = parameters;
        this.blockBody = blockBody;
        this.exprBody = exprBody;
    }

    public List<VarDecl> getParameters() {
        return parameters;
    }

    public Block getBlockBody() {
        return blockBody;
    }

    public Expr getExprBody() {
        return exprBody;
    }

    @Override
    public <R, C> R accept(QLProgramVisitor<R, C> visitor, C context) {
        return visitor.visit(this, context);
    }

}
