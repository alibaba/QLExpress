package com.alibaba.qlexpress4.parser.ast;

public class ReturnNode extends ASTNode implements StatementNode {
    private final ExpressionNode value;

    public ReturnNode(int line, int column, String source, ExpressionNode value) {
        super(line, column, source);
        this.value = value;
    }

    @Override
    public <R, C> R accept(ASTVisitor<R, C> visitor, C context) throws Exception {
        return visitor.visit(this, context);
    }

    public ExpressionNode getValue() { return value; }
}
