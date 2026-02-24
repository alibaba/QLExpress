package com.alibaba.qlexpress4.parser.ast;

/**
 * AST node for field access expressions using the . operator.
 * For example: obj.field represents accessing the 'field' property of obj.
 * This handles regular field access (not optional chaining with ?.).
 */
public class FieldAccessNode extends ASTNode implements ExpressionNode {
    private final ExpressionNode target;

    private final String fieldName;

    private final boolean optional;

    private final boolean spread;

    public FieldAccessNode(int line, int column, String source, ExpressionNode target, String fieldName,
        boolean optional) {
        this(line, column, source, target, fieldName, optional, false);
    }

    public FieldAccessNode(int line, int column, String source, ExpressionNode target, String fieldName,
        boolean optional, boolean spread) {
        super(line, column, source);
        this.target = target;
        this.fieldName = fieldName;
        this.optional = optional;
        this.spread = spread;
    }

    @Override
    public <R, C> R accept(ASTVisitor<R, C> visitor, C context)
        throws Exception {
        return visitor.visit(this, context);
    }

    /**
     * Get the target object expression (left side of .).
     * For example, in "obj.field", this is the expression for "obj".
     */
    public ExpressionNode getTarget() {
        return target;
    }

    /**
     * Get the field name being accessed (right side of .).
     * For example, in "obj.field", this is "field".
     */
    public String getFieldName() {
        return fieldName;
    }

    /**
     * Check if this is optional field access (using ?. operator).
     * Optional field access returns null instead of throwing an error when the target is null.
     */
    public boolean isOptional() {
        return optional;
    }

    /**
     * Check if this is spread field access (using *. operator).
     * Spread field access applies the field access to each element in a collection.
     */
    public boolean isSpread() {
        return spread;
    }
}
