package com.alibaba.qlexpress4.parser.ast;

import java.util.List;

public class ConstructorCallNode extends ASTNode implements ExpressionNode {
    private final String typeName;
    
    private final List<ExpressionNode> arguments;
    
    public ConstructorCallNode(int line, int column, String source, String typeName, List<ExpressionNode> arguments) {
        super(line, column, source);
        this.typeName = typeName;
        this.arguments = arguments;
    }
    
    @Override
    public <R, C> R accept(ASTVisitor<R, C> visitor, C context)
        throws Exception {
        return visitor.visit(this, context);
    }
    
    public String getTypeName() {
        return typeName;
    }
    
    public List<ExpressionNode> getArguments() {
        return arguments;
    }
}
