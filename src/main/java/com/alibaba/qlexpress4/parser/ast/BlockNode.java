package com.alibaba.qlexpress4.parser.ast;

import java.util.List;
import java.util.Collections;

public class BlockNode extends ASTNode implements StatementNode, ExpressionNode {
    private final List<StatementNode> statements;
    
    public BlockNode(int line, int column, String source, List<StatementNode> statements) {
        super(line, column, source);
        this.statements = statements != null ? statements : Collections.emptyList();
    }
    
    @Override
    public <R, C> R accept(ASTVisitor<R, C> visitor, C context)
        throws Exception {
        return visitor.visit(this, context);
    }
    
    public List<StatementNode> getStatements() {
        return statements;
    }
}
