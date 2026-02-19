package com.alibaba.qlexpress4.parser.ast;

public class BreakNode extends ASTNode implements StatementNode {
    public BreakNode(int line, int column, String source) {
        super(line, column, source);
    }

    @Override
    public <R, C> R accept(ASTVisitor<R, C> visitor, C context) throws Exception {
        return visitor.visit(this, context);
    }
}
