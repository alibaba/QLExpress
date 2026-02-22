package com.alibaba.qlexpress4.parser.ast;

/**
 * AST node for array slice expressions like [start:end], [start:], [:end], or [:].
 * <p>
 * Examples:
 * <ul>
 *   <li>a[1:3] - elements from index 1 to 2 (end is exclusive)</li>
 *   <li>a[1:] - elements from index 1 to end</li>
 *   <li>a[:3] - elements from start to index 2</li>
 *   <li>a[:] - all elements (shallow copy)</li>
 * </ul>
 */
public class ArraySliceNode extends ASTNode implements ExpressionNode {
    private final ExpressionNode array;
    
    private final ExpressionNode start;
    
    private final ExpressionNode end;
    
    public ArraySliceNode(int line, int column, String source, ExpressionNode array, ExpressionNode start,
        ExpressionNode end) {
        super(line, column, source);
        this.array = array;
        this.start = start;
        this.end = end;
    }
    
    @Override
    public <R, C> R accept(ASTVisitor<R, C> visitor, C context)
        throws Exception {
        return visitor.visit(this, context);
    }
    
    public ExpressionNode getArray() {
        return array;
    }
    
    /**
     * Get the start index expression (may be null for [:end] or [:]).
     * @return the start index expression or null if not specified
     */
    public ExpressionNode getStart() {
        return start;
    }
    
    /**
     * Get the end index expression (may be null for [start:] or [:]).
     * @return the end index expression or null if not specified
     */
    public ExpressionNode getEnd() {
        return end;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(array).append("[");
        if (start != null) {
            sb.append(start);
        }
        sb.append(":");
        if (end != null) {
            sb.append(end);
        }
        sb.append("]");
        return sb.toString();
    }
}
