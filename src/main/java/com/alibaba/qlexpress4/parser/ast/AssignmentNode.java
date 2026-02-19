package com.alibaba.qlexpress4.parser.ast;

public class AssignmentNode extends ASTNode implements StatementNode, ExpressionNode {
    private final ExpressionNode target;
    private final String operator;
    private final ExpressionNode value;

    public AssignmentNode(int line, int column, String source, ExpressionNode target, String operator, ExpressionNode value) {
        super(line, column, source);
        this.target = target;
        this.operator = operator;
        this.value = value;
    }

    @Override
    public <R, C> R accept(ASTVisitor<R, C> visitor, C context) throws Exception {
        return visitor.visit(this, context);
    }

    public ExpressionNode getTarget() { return target; }
    public String getOperator() { return operator; }
    public ExpressionNode getValue() { return value; }
}
