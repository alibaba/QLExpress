package com.alibaba.qlexpress4.parser.ast;

/**
 * Empty statement node (just a semicolon).
 * <p>
 * This node represents an empty statement, which consists of only a semicolon.
 * Empty statements are used as placeholders in control flow structures.
 */
public class EmptyStatementNode extends ASTNode implements StatementNode {
    public EmptyStatementNode(int line, int column, int startPosition, String source) {
        super(line, column, startPosition, source);
    }

    @Override
    public <R, C> R accept(ASTVisitor<R, C> visitor, C context)
        throws Exception {
        return visitor.visit(this, context);
    }
}
