package com.alibaba.qlexpress4.parser.ast;

import java.util.List;

public class MethodCallNode extends ASTNode implements ExpressionNode {
    private final ExpressionNode target;
    private final String methodName;
    private final List<ExpressionNode> arguments;

    public MethodCallNode(int line, int column, String source, ExpressionNode target, String methodName, List<ExpressionNode> arguments) {
        super(line, column, source);
        this.target = target;
        this.methodName = methodName;
        this.arguments = arguments;
    }

    @Override
    public <R, C> R accept(ASTVisitor<R, C> visitor, C context) throws Exception {
        return visitor.visit(this, context);
    }

    public ExpressionNode getTarget() { return target; }
    public String getMethodName() { return methodName; }
    public List<ExpressionNode> getArguments() { return arguments; }
}
