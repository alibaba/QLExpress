package com.alibaba.qlexpress4.parser.ast;

public class ThrowNode extends ASTNode implements StatementNode {
    private final ExpressionNode exception;
    
    public ThrowNode(int line, int column, String source, ExpressionNode exception) {
        super(line, column, source);
        this.exception = exception;
    }
    
    @Override
    public <R, C> R accept(ASTVisitor<R, C> visitor, C context)
        throws Exception {
        return visitor.visit(this, context);
    }
    
    public ExpressionNode getException() {
        return exception;
    }
}
