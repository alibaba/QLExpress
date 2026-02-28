package com.alibaba.qlexpress4.parser.visitor;

import com.alibaba.qlexpress4.parser.ast.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * FunctionDefinitionExtractor extracts function definitions from an AST.
 * <p>
 * This visitor traverses the AST and collects information about all function definitions,
 * including nested function definitions.
 * <p>
 * The extracted function information can be used for:
 * - Determining which functions are defined locally vs. external
 * - Scope analysis for function calls
 * - Compilation validation
 *
 * @author QLExpress Team
 */
public class FunctionDefinitionExtractor implements ASTVisitor<Void, FunctionDefinitionExtractor.Context> {
    
    /**
     * Context for function definition extraction.
     * <p>
     * Holds the accumulated set of function definition names during traversal.
     * Also tracks the nesting depth of function definitions (0 = top-level).
     */
    public static class Context {
        private final Set<String> functionDefinitions = new HashSet<>();
        
        private int functionDepth = 0; // 0 = top-level (not inside any function)
        
        public Set<String> getFunctionDefinitions() {
            return Collections.unmodifiableSet(functionDefinitions);
        }
        
        public void addFunctionDefinition(String functionName) {
            functionDefinitions.add(functionName);
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
    }
    
    /**
     * Extracts all function definition names from the given AST node.
     *
     * @param node the AST node to extract from
     * @return the set of function definition names found
     */
    public Set<String> extract(ASTNode node)
        throws Exception {
        if (node == null) {
            return Collections.emptySet();
        }
        
        Context context = new Context();
        node.accept(this, context);
        return context.getFunctionDefinitions();
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
        visitNode(node.getThenBody(), context);
        visitNode(node.getElseBody(), context);
        return null;
    }
    
    @Override
    public Void visit(WhileNode node, Context context)
        throws Exception {
        visitNode(node.getBody(), context);
        return null;
    }
    
    @Override
    public Void visit(ForNode node, Context context)
        throws Exception {
        visitNode(node.getInit(), context);
        visitNode(node.getBody(), context);
        return null;
    }
    
    @Override
    public Void visit(SwitchNode node, Context context)
        throws Exception {
        for (SwitchCaseNode caseNode : node.getCases()) {
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
        // Add the function name to the set of defined functions
        context.addFunctionDefinition(node.getFunctionName());
        // Enter function scope
        context.enterFunction();
        // Visit the body to find nested function definitions
        visitNode(node.getBody(), context);
        // Exit function scope
        context.exitFunction();
        return null;
    }
    
    @Override
    public Void visit(MacroDefinitionNode node, Context context)
        throws Exception {
        // Macros are also function definitions in a sense
        // But for getOutFunctions purposes, we only care about functions
        // Visit the body to find nested function definitions
        visitNode(node.getBody(), context);
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
        visitNode(node.getBody(), context);
        return null;
    }
    
    @Override
    public Void visit(MethodReferenceNode node, Context context)
        throws Exception {
        visitNode(node.getTarget(), context);
        return null;
    }
    
    @Override
    public Void visit(FieldAccessNode node, Context context)
        throws Exception {
        visitNode(node.getTarget(), context);
        return null;
    }
    
    @Override
    public Void visit(MethodCallNode node, Context context)
        throws Exception {
        visitNode(node.getTarget(), context);
        for (ExpressionNode arg : node.getArguments()) {
            visitExpression(arg, context);
        }
        return null;
    }
    
    @Override
    public Void visit(ConstructorCallNode node, Context context)
        throws Exception {
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
