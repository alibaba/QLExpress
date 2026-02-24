package com.alibaba.qlexpress4.parser.ast;

public class CastNode extends ASTNode implements ExpressionNode {
    private final String typeName;
    
    private final ExpressionNode expression;
    
    public CastNode(int line, int column, int startPosition, String source, String typeName, ExpressionNode expression) {
        super(line, column, startPosition, source);
        this.typeName = typeName;
        this.expression = expression;
    }
    
    @Override
    public <R, C> R accept(ASTVisitor<R, C> visitor, C context)
        throws Exception {
        return visitor.visit(this, context);
    }
    
    public String getTypeName() {
        return typeName;
    }
    
    public ExpressionNode getExpression() {
        return expression;
    }
}
