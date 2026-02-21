package com.alibaba.qlexpress4.parser.ast;

import java.util.List;

public class LambdaNode extends ASTNode implements ExpressionNode {
    private final List<ParameterNode> parameters;
    
    private final Node body;
    
    public LambdaNode(int line, int column, String source, List<ParameterNode> parameters, Node body) {
        super(line, column, source);
        this.parameters = parameters;
        this.body = body;
    }
    
    @Override
    public <R, C> R accept(ASTVisitor<R, C> visitor, C context)
        throws Exception {
        return visitor.visit(this, context);
    }
    
    public List<ParameterNode> getParameters() {
        return parameters;
    }
    
    public Node getBody() {
        return body;
    }
}
