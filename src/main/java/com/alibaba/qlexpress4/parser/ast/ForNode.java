package com.alibaba.qlexpress4.parser.ast;

public class ForNode extends ASTNode implements StatementNode {
    private final Node init;
    
    private final ExpressionNode condition;
    
    private final ExpressionNode update;
    
    private final BlockNode body;
    
    public ForNode(int line, int column, String source, Node init, ExpressionNode condition, ExpressionNode update,
        BlockNode body) {
        super(line, column, source);
        this.init = init;
        this.condition = condition;
        this.update = update;
        this.body = body;
    }
    
    @Override
    public <R, C> R accept(ASTVisitor<R, C> visitor, C context)
        throws Exception {
        return visitor.visit(this, context);
    }
    
    public Node getInit() {
        return init;
    }
    
    public ExpressionNode getCondition() {
        return condition;
    }
    
    public ExpressionNode getUpdate() {
        return update;
    }
    
    public BlockNode getBody() {
        return body;
    }
}
