package com.alibaba.qlexpress4.parser.ast;

public class TernaryNode extends ASTNode implements ExpressionNode {
    private final ExpressionNode condition;
    
    private final ExpressionNode thenExpr;
    
    private final ExpressionNode elseExpr;
    
    public TernaryNode(int line, int column, int startPosition, String source, ExpressionNode condition,
        ExpressionNode thenExpr, ExpressionNode elseExpr) {
        super(line, column, startPosition, source);
        this.condition = condition;
        this.thenExpr = thenExpr;
        this.elseExpr = elseExpr;
    }
    
    @Override
    public <R, C> R accept(ASTVisitor<R, C> visitor, C context)
        throws Exception {
        return visitor.visit(this, context);
    }
    
    public ExpressionNode getCondition() {
        return condition;
    }
    
    public ExpressionNode getThenExpr() {
        return thenExpr;
    }
    
    public ExpressionNode getElseExpr() {
        return elseExpr;
    }
}
