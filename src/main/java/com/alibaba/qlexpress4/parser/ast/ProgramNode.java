package com.alibaba.qlexpress4.parser.ast;

import java.util.List;
import java.util.Collections;

/**
 * Root node of the AST representing a complete program.
 *
 * <p>A program consists of a sequence of statements (imports, declarations, and executable code).
 */
public class ProgramNode extends ASTNode {
    private final List<StatementNode> statements;
    
    public ProgramNode(int line, int column, String source, List<StatementNode> statements) {
        super(line, column, source);
        this.statements = statements != null ? statements : Collections.emptyList();
    }
    
    public ProgramNode(int line, int column, List<StatementNode> statements) {
        this(line, column, null, statements);
    }
    
    public List<StatementNode> getStatements() {
        return statements;
    }
    
    @Override
    public <R, C> R accept(ASTVisitor<R, C> visitor, C context)
        throws Exception {
        return visitor.visit(this, context);
    }
    
    @Override
    public String toString() {
        return "ProgramNode{" + "statements=" + statements.size() + ", location=" + getLocationString() + '}';
    }
}
