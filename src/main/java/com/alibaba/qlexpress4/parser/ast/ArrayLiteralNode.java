package com.alibaba.qlexpress4.parser.ast;

import java.util.List;

public class ArrayLiteralNode extends ASTNode implements ExpressionNode {
    private final List<ExpressionNode> elements;
    
    public ArrayLiteralNode(int line, int column, String source, List<ExpressionNode> elements) {
        super(line, column, source);
        this.elements = elements;
    }
    
    @Override
    public <R, C> R accept(ASTVisitor<R, C> visitor, C context)
        throws Exception {
        return visitor.visit(this, context);
    }
    
    public List<ExpressionNode> getElements() {
        return elements;
    }
}
