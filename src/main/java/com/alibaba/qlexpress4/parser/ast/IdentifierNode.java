package com.alibaba.qlexpress4.parser.ast;

public class IdentifierNode extends ASTNode implements ExpressionNode {
    private final String name;
    
    public IdentifierNode(int line, int column, String source, String name) {
        super(line, column, source);
        this.name = name;
    }
    
    @Override
    public <R, C> R accept(ASTVisitor<R, C> visitor, C context)
        throws Exception {
        return visitor.visit(this, context);
    }
    
    public String getName() {
        return name;
    }
}
