package com.alibaba.qlexpress4.parser.ast;

public class LiteralNode extends ASTNode implements ExpressionNode {
    private final Object value;
    
    public LiteralNode(int line, int column, String source, Object value) {
        super(line, column, source);
        this.value = value;
    }
    
    @Override
    public <R, C> R accept(ASTVisitor<R, C> visitor, C context)
        throws Exception {
        return visitor.visit(this, context);
    }
    
    public Object getValue() {
        return value;
    }
}
