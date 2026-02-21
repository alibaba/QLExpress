package com.alibaba.qlexpress4.parser.ast;

public class IfNode extends ASTNode implements StatementNode {
    private final ExpressionNode condition;
    
    private final Node thenBody;
    
    private final Node elseBody;
    
    public IfNode(int line, int column, String source, ExpressionNode condition, Node thenBody, Node elseBody) {
        super(line, column, source);
        this.condition = condition;
        this.thenBody = thenBody;
        this.elseBody = elseBody;
    }
    
    @Override
    public <R, C> R accept(ASTVisitor<R, C> visitor, C context)
        throws Exception {
        return visitor.visit(this, context);
    }
    
    public ExpressionNode getCondition() {
        return condition;
    }
    
    public Node getThenBody() {
        return thenBody;
    }
    
    public Node getElseBody() {
        return elseBody;
    }
}
