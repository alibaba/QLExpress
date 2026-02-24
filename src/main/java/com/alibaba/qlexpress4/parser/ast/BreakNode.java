package com.alibaba.qlexpress4.parser.ast;

public class BreakNode extends ASTNode implements StatementNode {
    public BreakNode(int line, int column, int startPosition, String source) {
        super(line, column, startPosition, source);
    }
    
    @Override
    public <R, C> R accept(ASTVisitor<R, C> visitor, C context)
        throws Exception {
        return visitor.visit(this, context);
    }
}
