package com.alibaba.qlexpress4.parser.ast;

public class WhileNode extends ASTNode implements StatementNode {
    private final ExpressionNode condition;
    
    private final BlockNode body;
    
    public WhileNode(int line, int column, int startPosition, String source, ExpressionNode condition, BlockNode body) {
        super(line, column, startPosition, source);
        this.condition = condition;
        this.body = body;
    }
    
    @Override
    public <R, C> R accept(ASTVisitor<R, C> visitor, C context)
        throws Exception {
        return visitor.visit(this, context);
    }
    
    public ExpressionNode getCondition() {
        return condition;
    }
    
    public BlockNode getBody() {
        return body;
    }
}
