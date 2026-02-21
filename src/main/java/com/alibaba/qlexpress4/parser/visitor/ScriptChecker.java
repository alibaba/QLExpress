package com.alibaba.qlexpress4.parser.visitor;

import com.alibaba.qlexpress4.CheckOptions;
import com.alibaba.qlexpress4.exception.QLErrorCodes;
import com.alibaba.qlexpress4.exception.QLException;
import com.alibaba.qlexpress4.exception.QLSyntaxException;
import com.alibaba.qlexpress4.operator.OperatorCheckStrategy;
import com.alibaba.qlexpress4.parser.ast.*;

/**
 * Visitor for checking script validity based on security options.
 * <p>
 * This visitor validates operators and function calls according to
 * the configured check options, throwing exceptions when violations
 * are detected.
 *
 * @author QLExpress Team
 */
public class ScriptChecker implements ASTVisitor<Void, Void> {
    
    /**
     * Operator restriction strategy
     */
    private final OperatorCheckStrategy operatorCheckStrategy;
    
    /**
     * Whether to disable function calls
     */
    private final boolean disableFunctionCalls;
    
    /**
     * Script content for error reporting
     */
    private final String script;
    
    public ScriptChecker(CheckOptions checkOptions) {
        this(checkOptions, "");
    }
    
    public ScriptChecker(CheckOptions checkOptions, String script) {
        this.operatorCheckStrategy = checkOptions.getCheckStrategy();
        this.disableFunctionCalls = checkOptions.isDisableFunctionCalls();
        this.script = script;
    }
    
    /**
     * Checks a program node for violations.
     *
     * @param programNode the AST to check
     * @throws QLSyntaxException if a violation is detected
     */
    public void check(ProgramNode programNode)
        throws QLSyntaxException {
        try {
            ((ASTNode)programNode).accept(this, null);
        }
        catch (QLSyntaxException e) {
            throw e;
        }
        catch (Exception e) {
            // For non-QLSyntaxException exceptions, wrap in RuntimeException
            // (We can't create QLSyntaxException without a Diagnostic)
            throw new RuntimeException("Error during script checking: " + e.getMessage(), e);
        }
    }
    
    private void checkOperator(String operatorString, int line, int column, int position)
        throws QLSyntaxException {
        if (null != operatorCheckStrategy && !operatorCheckStrategy.isAllowed(operatorString)) {
            String reason = String.format(QLErrorCodes.OPERATOR_NOT_ALLOWED.getErrorMsg(),
                operatorString,
                operatorCheckStrategy.getOperators());
            throw QLException.reportScannerErr(script,
                position,
                line,
                column,
                operatorString,
                QLErrorCodes.OPERATOR_NOT_ALLOWED.name(),
                reason);
        }
    }
    
    private void checkFunctionCall(int line, int column, int position)
        throws QLSyntaxException {
        if (disableFunctionCalls) {
            String reason = "Function calls are not allowed in this context";
            throw QLException
                .reportScannerErr(script, position, line, column, "function call", "FUNCTION_CALL_NOT_ALLOWED", reason);
        }
    }
    
    private void visitNode(Node node, Void context)
        throws Exception {
        if (node instanceof ASTNode) {
            ((ASTNode)node).accept(this, context);
        }
    }
    
    // Statement visitors
    
    @Override
    public Void visit(ProgramNode node, Void context)
        throws Exception {
        for (StatementNode stmt : node.getStatements()) {
            visitNode(stmt, context);
        }
        return null;
    }
    
    @Override
    public Void visit(BlockNode node, Void context)
        throws Exception {
        for (StatementNode stmt : node.getStatements()) {
            visitNode(stmt, context);
        }
        return null;
    }
    
    @Override
    public Void visit(IfNode node, Void context)
        throws Exception {
        visitNode(node.getCondition(), context);
        visitNode(node.getThenBody(), context);
        visitNode(node.getElseBody(), context);
        return null;
    }
    
    @Override
    public Void visit(WhileNode node, Void context)
        throws Exception {
        visitNode(node.getCondition(), context);
        visitNode(node.getBody(), context);
        return null;
    }
    
    @Override
    public Void visit(ForNode node, Void context)
        throws Exception {
        visitNode(node.getInit(), context);
        visitNode(node.getCondition(), context);
        visitNode(node.getUpdate(), context);
        visitNode(node.getBody(), context);
        return null;
    }
    
    @Override
    public Void visit(SwitchNode node, Void context)
        throws Exception {
        visitNode(node.getValue(), context);
        for (SwitchCaseNode caseNode : node.getCases()) {
            visitNode(caseNode.getCondition(), context);
            for (StatementNode stmt : caseNode.getStatements()) {
                visitNode(stmt, context);
            }
        }
        return null;
    }
    
    @Override
    public Void visit(TryCatchNode node, Void context)
        throws Exception {
        visitNode(node.getTryBlock(), context);
        for (CatchClauseNode catchClause : node.getCatchClauses()) {
            visitNode(catchClause.getBody(), context);
        }
        visitNode(node.getFinallyBlock(), context);
        return null;
    }
    
    @Override
    public Void visit(ReturnNode node, Void context)
        throws Exception {
        visitNode(node.getValue(), context);
        return null;
    }
    
    @Override
    public Void visit(BreakNode node, Void context)
        throws Exception {
        return null;
    }
    
    @Override
    public Void visit(ContinueNode node, Void context)
        throws Exception {
        return null;
    }
    
    @Override
    public Void visit(ThrowNode node, Void context)
        throws Exception {
        visitNode(node.getException(), context);
        return null;
    }
    
    @Override
    public Void visit(VariableDeclarationNode node, Void context)
        throws Exception {
        visitNode(node.getInitialValue(), context);
        return null;
    }
    
    @Override
    public Void visit(TypeDeclarationNode node, Void context)
        throws Exception {
        return null;
    }
    
    @Override
    public Void visit(ImportNode node, Void context)
        throws Exception {
        return null;
    }
    
    @Override
    public Void visit(FunctionDefinitionNode node, Void context)
        throws Exception {
        visitNode(node.getBody(), context);
        return null;
    }
    
    @Override
    public Void visit(MacroDefinitionNode node, Void context)
        throws Exception {
        visitNode(node.getBody(), context);
        return null;
    }
    
    // Expression visitors
    
    @Override
    public Void visit(LiteralNode node, Void context)
        throws Exception {
        return null;
    }
    
    @Override
    public Void visit(IdentifierNode node, Void context)
        throws Exception {
        return null;
    }
    
    @Override
    public Void visit(BinaryOpNode node, Void context)
        throws Exception {
        // Check binary operator
        checkOperator(node.getOperator(), node.getLine(), node.getColumn(), 0);
        
        // Continue visiting children
        visitNode(node.getLeft(), context);
        visitNode(node.getRight(), context);
        return null;
    }
    
    @Override
    public Void visit(UnaryOpNode node, Void context)
        throws Exception {
        // Check unary operator
        checkOperator(node.getOperator(), node.getLine(), node.getColumn(), 0);
        
        // Continue visiting children
        visitNode(node.getOperand(), context);
        return null;
    }
    
    @Override
    public Void visit(TernaryNode node, Void context)
        throws Exception {
        visitNode(node.getCondition(), context);
        visitNode(node.getThenExpr(), context);
        visitNode(node.getElseExpr(), context);
        return null;
    }
    
    @Override
    public Void visit(LambdaNode node, Void context)
        throws Exception {
        visitNode(node.getBody(), context);
        return null;
    }
    
    @Override
    public Void visit(MethodCallNode node, Void context)
        throws Exception {
        // Check function call
        checkFunctionCall(node.getLine(), node.getColumn(), 0);
        
        // Continue visiting children
        visitNode(node.getTarget(), context);
        for (ExpressionNode arg : node.getArguments()) {
            visitNode(arg, context);
        }
        return null;
    }
    
    @Override
    public Void visit(ConstructorCallNode node, Void context)
        throws Exception {
        for (ExpressionNode arg : node.getArguments()) {
            visitNode(arg, context);
        }
        return null;
    }
    
    @Override
    public Void visit(AssignmentNode node, Void context)
        throws Exception {
        // Check assignment operator
        checkOperator(node.getOperator(), node.getLine(), node.getColumn(), 0);
        
        // Continue visiting children
        visitNode(node.getTarget(), context);
        visitNode(node.getValue(), context);
        return null;
    }
    
    @Override
    public Void visit(CastNode node, Void context)
        throws Exception {
        visitNode(node.getExpression(), context);
        return null;
    }

    @Override
    public Void visit(ArrayAccessNode node, Void context)
        throws Exception {
        visitNode(node.getArray(), context);
        visitNode(node.getIndex(), context);
        return null;
    }

    @Override
    public Void visit(ArraySliceNode node, Void context)
        throws Exception {
        visitNode(node.getArray(), context);
        if (node.getStart() != null) {
            visitNode(node.getStart(), context);
        }
        if (node.getEnd() != null) {
            visitNode(node.getEnd(), context);
        }
        return null;
    }

    @Override
    public Void visit(ArrayLiteralNode node, Void context)
        throws Exception {
        for (ExpressionNode element : node.getElements()) {
            visitNode(element, context);
        }
        return null;
    }
    
    @Override
    public Void visit(ListLiteralNode node, Void context)
        throws Exception {
        for (ExpressionNode element : node.getElements()) {
            visitNode(element, context);
        }
        return null;
    }
    
    @Override
    public Void visit(MapLiteralNode node, Void context)
        throws Exception {
        for (MapEntryNode entry : node.getEntries()) {
            visitNode(entry.getKey(), context);
            visitNode(entry.getValue(), context);
        }
        return null;
    }
    
    @Override
    public Void visit(InstanceOfNode node, Void context)
        throws Exception {
        visitNode(node.getExpression(), context);
        return null;
    }
    
    @Override
    public Void visit(TypeNode node, Void context)
        throws Exception {
        return null;
    }
}
