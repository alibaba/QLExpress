package com.alibaba.qlexpress4.parser.ast;

public class InstanceOfNode extends ASTNode implements ExpressionNode {
    private final ExpressionNode expression;
    
    private final String typeName;
    
    public InstanceOfNode(int line, int column, int startPosition, String source, ExpressionNode expression, String typeName) {
        super(line, column, startPosition, source);
        this.expression = expression;
        this.typeName = typeName;
    }
    
    @Override
    public <R, C> R accept(ASTVisitor<R, C> visitor, C context)
        throws Exception {
        return visitor.visit(this, context);
    }
    
    public ExpressionNode getExpression() {
        return expression;
    }
    
    public String getTypeName() {
        return typeName;
    }
}
