package com.alibaba.qlexpress4.parser.ast;

import java.util.List;

public class FunctionDefinitionNode extends ASTNode implements StatementNode {
    private final String functionName;
    
    private final List<ParameterNode> parameters;
    
    private final BlockNode body;
    
    public FunctionDefinitionNode(int line, int column, String source, String functionName,
        List<ParameterNode> parameters, BlockNode body) {
        super(line, column, source);
        this.functionName = functionName;
        this.parameters = parameters;
        this.body = body;
    }
    
    @Override
    public <R, C> R accept(ASTVisitor<R, C> visitor, C context)
        throws Exception {
        return visitor.visit(this, context);
    }
    
    public String getFunctionName() {
        return functionName;
    }
    
    public List<ParameterNode> getParameters() {
        return parameters;
    }
    
    public BlockNode getBody() {
        return body;
    }
}
