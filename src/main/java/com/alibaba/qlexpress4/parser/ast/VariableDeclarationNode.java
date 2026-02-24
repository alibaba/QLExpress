package com.alibaba.qlexpress4.parser.ast;

public class VariableDeclarationNode extends ASTNode implements StatementNode {
    private final String typeName;
    
    private final String variableName;
    
    private final ExpressionNode initialValue;
    
    public VariableDeclarationNode(int line, int column, int startPosition, String source, String typeName, String variableName,
        ExpressionNode initialValue) {
        super(line, column, startPosition, source);
        this.typeName = typeName;
        this.variableName = variableName;
        this.initialValue = initialValue;
    }
    
    @Override
    public <R, C> R accept(ASTVisitor<R, C> visitor, C context)
        throws Exception {
        return visitor.visit(this, context);
    }
    
    public String getTypeName() {
        return typeName;
    }
    
    public String getVariableName() {
        return variableName;
    }
    
    public ExpressionNode getInitialValue() {
        return initialValue;
    }
}
