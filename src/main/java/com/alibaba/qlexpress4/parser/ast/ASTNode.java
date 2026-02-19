package com.alibaba.qlexpress4.parser.ast;

/**
 * Base class for all Abstract Syntax Tree (AST) nodes in QLExpress.
 *
 * <p>AST nodes represent syntactic constructs in the parsed source code.
 * Each node has:
 * <ul>
 *   <li>Source location information (line, column)</li>
 *   <li>Optional source file identifier</li>
 *   <li>Visitor pattern support via accept() method</li>
 * </ul>
 *
 * <p>The AST hierarchy consists of:
 * <ul>
 *   <li>Statements (nodes that perform actions)</li>
 *   <li>Expressions (nodes that produce values)</li>
 *   <li>Declarations (nodes that declare types, variables, functions)</li>
 * </ul>
 */
public abstract class ASTNode {
    private final int line;
    private final int column;
    private final String source;

    /**
     * Creates a new AST node with source location information.
     *
     * @param line the starting line number (1-based)
     * @param column the starting column number (1-based)
     * @param source the source file or string identifier (may be null)
     */
    protected ASTNode(int line, int column, String source) {
        this.line = line;
        this.column = column;
        this.source = source;
    }

    /**
     * Creates a new AST node without source information.
     *
     * @param line the starting line number (1-based)
     * @param column the starting column number (1-based)
     */
    protected ASTNode(int line, int column) {
        this(line, column, null);
    }

    /**
     * Returns the starting line number of this node.
     *
     * @return the starting line number (1-based)
     */
    public int getLine() {
        return line;
    }

    /**
     * Returns the starting column number of this node.
     *
     * @return the starting column number (1-based)
     */
    public int getColumn() {
        return column;
    }

    /**
     * Returns the source file or string identifier.
     *
     * @return the source identifier, or null if not available
     */
    public String getSource() {
        return source;
    }

    /**
     * Returns a human-readable location string for this node.
     *
     * @return a string like "source:1:5" or "1:5" if no source is set
     */
    public String getLocationString() {
        if (source != null && !source.isEmpty()) {
            return source + ":" + line + ":" + column;
        }
        return line + ":" + column;
    }

    /**
     * Accepts a visitor, implementing the Visitor pattern.
     *
     * <p>This method should be overridden by concrete node types to call
     * the appropriate visit method on the visitor.
     *
     * @param visitor the visitor to accept
     * @param <R> the return type of the visitor
     * @param <C> the context type passed to the visitor
     * @return the result of visiting this node
     * @throws Exception if the visitation fails
     */
    public abstract <R, C> R accept(ASTVisitor<R, C> visitor, C context) throws Exception;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ASTNode astNode = (ASTNode) o;

        if (line != astNode.line) return false;
        if (column != astNode.column) return false;
        return source != null ? source.equals(astNode.source) : astNode.source == null;
    }

    @Override
    public int hashCode() {
        int result = line;
        result = 31 * result + column;
        result = 31 * result + (source != null ? source.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + getLocationString();
    }

    /**
     * Returns a human-readable representation of this node for debugging.
     *
     * @return a detailed string representation
     */
    public String toDebugString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append("[");
        sb.append("location=").append(getLocationString());
        sb.append("]");
        return sb.toString();
    }
}
