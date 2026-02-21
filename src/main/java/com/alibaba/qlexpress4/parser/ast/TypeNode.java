package com.alibaba.qlexpress4.parser.ast;

public class TypeNode extends ASTNode implements ExpressionNode {
    private final String typeName;
    
    public TypeNode(int line, int column, String source, String typeName) {
        super(line, column, source);
        this.typeName = typeName;
    }
    
    @Override
    public <R, C> R accept(ASTVisitor<R, C> visitor, C context)
        throws Exception {
        return visitor.visit(this, context);
    }
    
    public String getTypeName() {
        return typeName;
    }
}
