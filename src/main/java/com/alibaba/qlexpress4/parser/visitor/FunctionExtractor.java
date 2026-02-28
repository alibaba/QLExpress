package com.alibaba.qlexpress4.parser.visitor;

import com.alibaba.qlexpress4.parser.ast.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * FunctionExtractor extracts function calls from an AST.
 * <p>
 * This visitor traverses the AST and collects information about all function calls,
 * including method calls, static calls, and constructor calls.
 * <p>
 * The extracted function information can be used for:
 * - Compilation validation
 * - Dependency analysis
 * - Optimizing function call handling
 *
 * @author QLExpress Team
 */
public class FunctionExtractor implements ASTVisitor<Void, FunctionExtractor.Context> {
    
    /**
     * Context for function extraction.
     * <p>
     * Holds the accumulated list of extracted function calls during traversal.
     * Also tracks the nesting depth of function definitions (0 = top-level).
     * And tracks function definitions at the top level.
     */
    public static class Context {
        private final List<FunctionCall> functionCalls = new ArrayList<>();
        
        private int functionDepth = 0; // 0 = top-level (not inside any function)
        
        private final Set<String> topLevelFunctionDefinitions = new HashSet<>();
        
        public List<FunctionCall> getFunctionCalls() {
            return Collections.unmodifiableList(functionCalls);
        }
        
        public void addFunctionCall(FunctionCall functionCall) {
            functionCalls.add(functionCall);
        }
        
        public void enterFunction() {
            functionDepth++;
        }
        
        public void exitFunction() {
            functionDepth--;
        }
        
        public boolean isTopLevel() {
            return functionDepth == 0;
        }
        
        public void addFunctionDefinition(String functionName) {
            topLevelFunctionDefinitions.add(functionName);
        }
        
        public Set<String> getTopLevelFunctionDefinitions() {
            return Collections.unmodifiableSet(topLevelFunctionDefinitions);
        }
    }
    
    /**
     * Represents a function call found in the AST.
     */
    public static class FunctionCall {
        private final FunctionCallType type;
        
        private final String name;
        
        private final int arity;
        
        private final int line;
        
        private final int column;
        
        private final boolean isTopLevel;
        
        public FunctionCall(FunctionCallType type, String name, int arity, int line, int column, boolean isTopLevel) {
            this.type = type;
            this.name = name;
            this.arity = arity;
            this.line = line;
            this.column = column;
            this.isTopLevel = isTopLevel;
        }
        
        public FunctionCallType getType() {
            return type;
        }
        
        public String getName() {
            return name;
        }
        
        public int getArity() {
            return arity;
        }
        
        public int getLine() {
            return line;
        }
        
        public int getColumn() {
            return column;
        }
        
        public boolean isTopLevel() {
            return isTopLevel;
        }
        
        @Override
        public String toString() {
            return String.format("%s '%s' (arity=%d) at %d:%d", type, name, arity, line, column);
        }
    }
    
    /**
     * Types of function calls.
     */
    public enum FunctionCallType {
        /** Direct function call (e.g., `myFunction(arg1, arg2)`) */
        DIRECT_CALL,
        /** Method call on an object (e.g., `obj.method(arg1, arg2)`) */
        METHOD_CALL,
        /** Static method call (e.g., `Class.staticMethod(arg1, arg2)`) */
        STATIC_CALL,
        /** Constructor call (e.g., `new MyClass(arg1, arg2)`) */
        CONSTRUCTOR_CALL
    }
    
    /**
     * Extracts all function calls from the given AST node.
     *
     * @param node the AST node to extract from
     * @return the list of function calls found
     */
    public List<FunctionCall> extract(ASTNode node)
        throws Exception {
        if (node == null) {
            return Collections.emptyList();
        }
        
        Context context = new Context();
        node.accept(this, context);
        return context.getFunctionCalls();
    }
    
    /**
     * Extracts the context from the given AST node, including both function calls and definitions.
     *
     * @param node the AST node to extract from
     * @return the context containing function calls and top-level function definitions
     */
    public Context extractWithContext(ASTNode node)
        throws Exception {
        if (node == null) {
            return new Context();
        }
        
        Context context = new Context();
        node.accept(this, context);
        return context;
    }
    
    // ==================== Statement Visitors ====================
    
    @Override
    public Void visit(ProgramNode node, Context context)
        throws Exception {
        for (StatementNode statement : node.getStatements()) {
            ((ASTNode)statement).accept(this, context);
        }
        return null;
    }
    
    @Override
    public Void visit(BlockNode node, Context context)
        throws Exception {
        for (StatementNode statement : node.getStatements()) {
            ((ASTNode)statement).accept(this, context);
        }
        return null;
    }
    
    @Override
    public Void visit(IfNode node, Context context)
        throws Exception {
        visitExpression(node.getCondition(), context);
        visitNode(node.getThenBody(), context);
        visitNode(node.getElseBody(), context);
        return null;
    }
    
    @Override
    public Void visit(WhileNode node, Context context)
        throws Exception {
        visitExpression(node.getCondition(), context);
        visitNode(node.getBody(), context);
        return null;
    }
    
    @Override
    public Void visit(ForNode node, Context context)
        throws Exception {
        visitNode(node.getInit(), context);
        visitExpression(node.getCondition(), context);
        visitExpression(node.getUpdate(), context);
        visitNode(node.getBody(), context);
        return null;
    }
    
    @Override
    public Void visit(SwitchNode node, Context context)
        throws Exception {
        visitExpression(node.getValue(), context);
        for (SwitchCaseNode caseNode : node.getCases()) {
            visitExpression(caseNode.getCondition(), context);
            for (StatementNode stmt : caseNode.getStatements()) {
                visitNode(stmt, context);
            }
        }
        return null;
    }
    
    @Override
    public Void visit(TryCatchNode node, Context context)
        throws Exception {
        visitNode(node.getTryBlock(), context);
        for (CatchClauseNode catchClause : node.getCatchClauses()) {
            visitNode(catchClause.getBody(), context);
        }
        visitNode(node.getFinallyBlock(), context);
        return null;
    }
    
    @Override
    public Void visit(ReturnNode node, Context context)
        throws Exception {
        visitNode(node.getValue(), context);
        return null;
    }
    
    @Override
    public Void visit(ThrowNode node, Context context)
        throws Exception {
        visitExpression(node.getException(), context);
        return null;
    }
    
    @Override
    public Void visit(BreakNode node, Context context)
        throws Exception {
        return null;
    }
    
    @Override
    public Void visit(ContinueNode node, Context context)
        throws Exception {
        return null;
    }
    
    @Override
    public Void visit(VariableDeclarationNode node, Context context)
        throws Exception {
        visitNode(node.getInitialValue(), context);
        return null;
    }
    
    @Override
    public Void visit(AssignmentNode node, Context context)
        throws Exception {
        visitNode(node.getTarget(), context);
        visitExpression(node.getValue(), context);
        return null;
    }
    
    @Override
    public Void visit(TypeDeclarationNode node, Context context)
        throws Exception {
        return null;
    }
    
    @Override
    public Void visit(ImportNode node, Context context)
        throws Exception {
        return null;
    }
    
    @Override
    public Void visit(FunctionDefinitionNode node, Context context)
        throws Exception {
        // Track top-level function definitions (not nested ones)
        if (context.isTopLevel()) {
            context.addFunctionDefinition(node.getFunctionName());
        }
        // Enter function scope
        context.enterFunction();
        // Visit the body
        visitNode(node.getBody(), context);
        // Exit function scope
        context.exitFunction();
        return null;
    }

    @Override
    public Void visit(MacroDefinitionNode node, Context context)
        throws Exception {
        visitNode(node.getBody(), context);
        return null;
    }

    @Override
    public Void visit(EmptyStatementNode node, Context context)
        throws Exception {
        // Empty statements don't contain any function calls
        return null;
    }

    // ==================== Expression Visitors ====================
    
    @Override
    public Void visit(LiteralNode node, Context context)
        throws Exception {
        return null;
    }
    
    @Override
    public Void visit(InterpolatedStringNode node, Context context)
        throws Exception {
        // Visit each expression segment in the interpolated string
        for (Object segment : node.getSegments()) {
            if (segment instanceof ExpressionNode) {
                visitExpression((ExpressionNode)segment, context);
            }
        }
        return null;
    }
    
    @Override
    public Void visit(IdentifierNode node, Context context)
        throws Exception {
        return null;
    }
    
    @Override
    public Void visit(BinaryOpNode node, Context context)
        throws Exception {
        visitExpression(node.getLeft(), context);
        visitExpression(node.getRight(), context);
        return null;
    }
    
    @Override
    public Void visit(UnaryOpNode node, Context context)
        throws Exception {
        visitExpression(node.getOperand(), context);
        return null;
    }
    
    @Override
    public Void visit(TernaryNode node, Context context)
        throws Exception {
        visitExpression(node.getCondition(), context);
        visitExpression(node.getThenExpr(), context);
        visitExpression(node.getElseExpr(), context);
        return null;
    }
    
    @Override
    public Void visit(LambdaNode node, Context context)
        throws Exception {
        // Enter lambda scope (lambdas are like anonymous functions)
        context.enterFunction();
        visitNode(node.getBody(), context);
        context.exitFunction();
        return null;
    }
    
    @Override
    public Void visit(MethodReferenceNode node, Context context)
        throws Exception {
        // Method reference is not a function call, just visit the target
        visitNode(node.getTarget(), context);
        return null;
    }
    
    @Override
    public Void visit(FieldAccessNode node, Context context)
        throws Exception {
        // Field access is not a function call, just visit the target
        visitNode(node.getTarget(), context);
        return null;
    }
    
    @Override
    public Void visit(MethodCallNode node, Context context)
        throws Exception {
        // Extract the function call information
        String functionName;
        FunctionCallType callType;
        
        if (node.getTarget() == null) {
            // Direct function call: myFunction(args)
            functionName = node.getMethodName();
            callType = FunctionCallType.DIRECT_CALL;
        }
        else if (node.getTarget() instanceof IdentifierNode) {
            // Static call: ClassName.staticMethod(args) or method call on variable
            IdentifierNode target = (IdentifierNode)node.getTarget();
            if (target.getName().isEmpty() || Character.isUpperCase(target.getName().charAt(0))) {
                // Likely a static call: Class.method(args)
                functionName = target.getName() + "." + node.getMethodName();
                callType = FunctionCallType.STATIC_CALL;
            }
            else {
                // Method call on variable: obj.method(args)
                functionName = node.getMethodName();
                callType = FunctionCallType.METHOD_CALL;
            }
        }
        else if (node.getTarget() instanceof TypeNode) {
            // Static call: Type.staticMethod(args)
            TypeNode typeNode = (TypeNode)node.getTarget();
            functionName = typeNode.getTypeName() + "." + node.getMethodName();
            callType = FunctionCallType.STATIC_CALL;
        }
        else {
            // Method call on expression result: expr().method(args)
            functionName = node.getMethodName();
            callType = FunctionCallType.METHOD_CALL;
        }
        
        int arity = node.getArguments().size();
        
        FunctionCall functionCall =
            new FunctionCall(callType, functionName, arity, node.getLine(), node.getColumn(), context.isTopLevel());
        context.addFunctionCall(functionCall);
        
        // Recursively visit the target and arguments
        visitNode(node.getTarget(), context);
        for (ExpressionNode arg : node.getArguments()) {
            visitExpression(arg, context);
        }
        
        return null;
    }
    
    @Override
    public Void visit(ConstructorCallNode node, Context context)
        throws Exception {
        String typeName = node.getTypeName();
        
        FunctionCall functionCall = new FunctionCall(FunctionCallType.CONSTRUCTOR_CALL, typeName,
            node.getArguments().size(), node.getLine(), node.getColumn(), context.isTopLevel());
        context.addFunctionCall(functionCall);
        
        // Recursively visit arguments
        for (ExpressionNode arg : node.getArguments()) {
            visitExpression(arg, context);
        }
        
        return null;
    }
    
    @Override
    public Void visit(CastNode node, Context context)
        throws Exception {
        visitExpression(node.getExpression(), context);
        return null;
    }
    
    @Override
    public Void visit(ArrayAccessNode node, Context context)
        throws Exception {
        visitNode(node.getArray(), context);
        visitExpression(node.getIndex(), context);
        return null;
    }
    
    @Override
    public Void visit(ArraySliceNode node, Context context)
        throws Exception {
        visitNode(node.getArray(), context);
        if (node.getStart() != null) {
            visitExpression(node.getStart(), context);
        }
        if (node.getEnd() != null) {
            visitExpression(node.getEnd(), context);
        }
        return null;
    }
    
    @Override
    public Void visit(ArrayLiteralNode node, Context context)
        throws Exception {
        for (ExpressionNode element : node.getElements()) {
            visitExpression(element, context);
        }
        return null;
    }
    
    @Override
    public Void visit(MapLiteralNode node, Context context)
        throws Exception {
        for (MapEntryNode entry : node.getEntries()) {
            visitExpression(entry.getKey(), context);
            visitExpression(entry.getValue(), context);
        }
        return null;
    }
    
    @Override
    public Void visit(ListLiteralNode node, Context context)
        throws Exception {
        for (ExpressionNode element : node.getElements()) {
            visitExpression(element, context);
        }
        return null;
    }
    
    @Override
    public Void visit(InstanceOfNode node, Context context)
        throws Exception {
        visitExpression(node.getExpression(), context);
        return null;
    }
    
    @Override
    public Void visit(TypeNode node, Context context)
        throws Exception {
        return null;
    }
    
    // ==================== Helper Methods ====================
    
    private void visitExpression(Node node, Context context)
        throws Exception {
        if (node instanceof ExpressionNode) {
            ((ASTNode)node).accept(this, context);
        }
    }
    
    private void visitNode(Node node, Context context)
        throws Exception {
        if (node instanceof ASTNode) {
            ((ASTNode)node).accept(this, context);
        }
    }
}
