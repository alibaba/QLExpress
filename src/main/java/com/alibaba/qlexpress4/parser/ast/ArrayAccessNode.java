package com.alibaba.qlexpress4.parser.ast;

public class ArrayAccessNode extends ASTNode implements ExpressionNode {
    private final ExpressionNode array;
    
    private final ExpressionNode index;
    
    public ArrayAccessNode(int line, int column, String source, ExpressionNode array, ExpressionNode index) {
        super(line, column, source);
        this.array = array;
        this.index = index;
    }
    
    @Override
    public <R, C> R accept(ASTVisitor<R, C> visitor, C context)
        throws Exception {
        return visitor.visit(this, context);
    }
    
    public ExpressionNode getArray() {
        return array;
    }
    
    public ExpressionNode getIndex() {
        return index;
    }
}
