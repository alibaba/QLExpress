package com.alibaba.qlexpress4.parser.ast;

public class UnaryOpNode extends ASTNode implements ExpressionNode {
    private final String operator;
    private final ExpressionNode operand;
    private final boolean prefix;

    public UnaryOpNode(int line, int column, String source, String operator, ExpressionNode operand, boolean prefix) {
        super(line, column, source);
        this.operator = operator;
        this.operand = operand;
        this.prefix = prefix;
    }

    @Override
    public <R, C> R accept(ASTVisitor<R, C> visitor, C context) throws Exception {
        return visitor.visit(this, context);
    }

    public String getOperator() { return operator; }
    public ExpressionNode getOperand() { return operand; }
    public boolean isPrefix() { return prefix; }
}
