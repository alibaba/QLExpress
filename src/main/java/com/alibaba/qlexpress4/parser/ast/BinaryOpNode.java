package com.alibaba.qlexpress4.parser.ast;

public class BinaryOpNode extends ASTNode implements ExpressionNode {
    private final ExpressionNode left;
    private final String operator;
    private final ExpressionNode right;

    public BinaryOpNode(int line, int column, String source, ExpressionNode left, String operator, ExpressionNode right) {
        super(line, column, source);
        this.left = left;
        this.operator = operator;
        this.right = right;
    }

    @Override
    public <R, C> R accept(ASTVisitor<R, C> visitor, C context) throws Exception {
        return visitor.visit(this, context);
    }

    public ExpressionNode getLeft() { return left; }
    public String getOperator() { return operator; }
    public ExpressionNode getRight() { return right; }
}
