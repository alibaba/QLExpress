package com.alibaba.qlexpress4.parser.ast;

/**
 * AST node for method reference expressions using the :: operator.
 * For example: obj::methodName represents a reference to the methodName method of obj.
 * This is different from a lambda - it's a direct method reference that should
 * be handled specially at runtime with GetMethodInstruction.
 */
public class MethodReferenceNode extends ASTNode implements ExpressionNode {
    private final ExpressionNode target;
    private final String methodName;

    public MethodReferenceNode(int line, int column, String source, ExpressionNode target, String methodName) {
        super(line, column, source);
        this.target = target;
        this.methodName = methodName;
    }

    @Override
    public <R, C> R accept(ASTVisitor<R, C> visitor, C context)
        throws Exception {
        return visitor.visit(this, context);
    }

    /**
     * Get the target object expression (left side of ::).
     * For example, in "obj::method", this is the expression for "obj".
     */
    public ExpressionNode getTarget() {
        return target;
    }

    /**
     * Get the method name being referenced (right side of ::).
     * For example, in "obj::method", this is "method".
     */
    public String getMethodName() {
        return methodName;
    }
}
