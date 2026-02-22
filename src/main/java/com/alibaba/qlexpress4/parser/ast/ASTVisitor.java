package com.alibaba.qlexpress4.parser.ast;

/**
 * Visitor interface for AST nodes.
 *
 * <p>This interface defines the contract for visiting all types of AST nodes.
 * Implementations can perform operations on the AST such as:
 * <ul>
 *   <li>Code generation (generating QVM instructions)</li>
 *   <li>Analysis (scope checking, variable detection)</li>
 *   <li>Transformation (optimization, rewriting)</li>
 *   <li>Execution tracing</li>
 * </ul>
 *
 * @param <R> the return type of visit methods
 * @param <C> the context type passed to visit methods
 */
public interface ASTVisitor<R, C> {
    
    // ==================== Statement Visitors ====================
    
    /**
     * Visits a block node (statement block).
     */
    R visit(BlockNode node, C context)
        throws Exception;
    
    /**
     * Visits an if-else statement node.
     */
    R visit(IfNode node, C context)
        throws Exception;
    
    /**
     * Visits a while loop node.
     */
    R visit(WhileNode node, C context)
        throws Exception;
    
    /**
     * Visits a for loop node.
     */
    R visit(ForNode node, C context)
        throws Exception;
    
    /**
     * Visits a switch statement node.
     */
    R visit(SwitchNode node, C context)
        throws Exception;
    
    /**
     * Visits a try-catch-finally node.
     */
    R visit(TryCatchNode node, C context)
        throws Exception;
    
    /**
     * Visits a return statement node.
     */
    R visit(ReturnNode node, C context)
        throws Exception;
    
    /**
     * Visits a break statement node.
     */
    R visit(BreakNode node, C context)
        throws Exception;
    
    /**
     * Visits a continue statement node.
     */
    R visit(ContinueNode node, C context)
        throws Exception;
    
    /**
     * Visits a throw statement node.
     */
    R visit(ThrowNode node, C context)
        throws Exception;
    
    /**
     * Visits a variable declaration node.
     */
    R visit(VariableDeclarationNode node, C context)
        throws Exception;
    
    /**
     * Visits an assignment node.
     */
    R visit(AssignmentNode node, C context)
        throws Exception;
    
    /**
     * Visits a type declaration node.
     */
    R visit(TypeDeclarationNode node, C context)
        throws Exception;
    
    /**
     * Visits an import node.
     */
    R visit(ImportNode node, C context)
        throws Exception;
    
    /**
     * Visits a function definition node.
     */
    R visit(FunctionDefinitionNode node, C context)
        throws Exception;
    
    /**
     * Visits a macro definition node.
     */
    R visit(MacroDefinitionNode node, C context)
        throws Exception;
    
    // ==================== Expression Visitors ====================
    
    /**
     * Visits a literal node.
     */
    R visit(LiteralNode node, C context)
        throws Exception;
    
    /**
     * Visits an identifier node.
     */
    R visit(IdentifierNode node, C context)
        throws Exception;
    
    /**
     * Visits a binary operation node.
     */
    R visit(BinaryOpNode node, C context)
        throws Exception;
    
    /**
     * Visits a unary operation node.
     */
    R visit(UnaryOpNode node, C context)
        throws Exception;
    
    /**
     * Visits a ternary expression node.
     */
    R visit(TernaryNode node, C context)
        throws Exception;
    
    /**
     * Visits a lambda expression node.
     */
    R visit(LambdaNode node, C context)
        throws Exception;

    /**
     * Visits a method reference node (:: operator).
     */
    R visit(MethodReferenceNode node, C context)
        throws Exception;

    /**
     * Visits a field access node (. operator).
     */
    R visit(FieldAccessNode node, C context)
        throws Exception;

    /**
     * Visits a method call node.
     */
    R visit(MethodCallNode node, C context)
        throws Exception;
    
    /**
     * Visits a constructor call node.
     */
    R visit(ConstructorCallNode node, C context)
        throws Exception;
    
    /**
     * Visits a type cast node.
     */
    R visit(CastNode node, C context)
        throws Exception;
    
    /**
     * Visits an array access node.
     */
    R visit(ArrayAccessNode node, C context)
        throws Exception;
    
    /**
     * Visits an array slice node.
     */
    R visit(ArraySliceNode node, C context)
        throws Exception;
    
    /**
     * Visits an array literal node.
     */
    R visit(ArrayLiteralNode node, C context)
        throws Exception;
    
    /**
     * Visits a map literal node.
     */
    R visit(MapLiteralNode node, C context)
        throws Exception;
    
    /**
     * Visits a list literal node.
     */
    R visit(ListLiteralNode node, C context)
        throws Exception;
    
    /**
     * Visits an instanceof check node.
     */
    R visit(InstanceOfNode node, C context)
        throws Exception;
    
    /**
     * Visits a type reference node.
     */
    R visit(TypeNode node, C context)
        throws Exception;
    
    // ==================== Other Visitors ====================
    
    /**
     * Visits a program node (root of AST).
     */
    R visit(ProgramNode node, C context)
        throws Exception;
}
