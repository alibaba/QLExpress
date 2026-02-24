package com.alibaba.qlexpress4.parser.visitor;

import com.alibaba.qlexpress4.parser.ast.*;

import java.util.*;

/**
 * VariableDetector detects variable reads and writes in an AST.
 * <p>
 * This visitor traverses the AST and collects information about variable usage,
 * including variable reads (identifier references), variable writes (assignments),
 * and variable declarations.
 * <p>
 * The detected variable information can be used for:
 * - Compilation validation
 * - Dependency analysis
 * - Optimization opportunities
 *
 * @author QLExpress Team
 */
public class VariableDetector implements ASTVisitor<Void, VariableDetector.Context> {
    
    /**
     * Context for variable detection.
     * <p>
     * Holds the accumulated list of variable accesses and declarations during traversal.
     */
    public static class Context {
        private final List<VariableAccess> variableReads = new ArrayList<>();
        
        private final List<VariableAccess> variableWrites = new ArrayList<>();
        
        private final List<VariableDeclaration> variableDeclarations = new ArrayList<>();
        
        public List<VariableAccess> getVariableReads() {
            return Collections.unmodifiableList(variableReads);
        }
        
        public List<VariableAccess> getVariableWrites() {
            return Collections.unmodifiableList(variableWrites);
        }
        
        public List<VariableDeclaration> getVariableDeclarations() {
            return Collections.unmodifiableList(variableDeclarations);
        }
        
        public void addVariableRead(VariableAccess access) {
            variableReads.add(access);
        }
        
        public void addVariableWrite(VariableAccess access) {
            variableWrites.add(access);
        }
        
        public void addVariableDeclaration(VariableDeclaration declaration) {
            variableDeclarations.add(declaration);
        }
        
        public Set<String> getAllVariableNames() {
            Set<String> names = new HashSet<>();
            for (VariableAccess read : variableReads) {
                names.add(read.getVariableName());
            }
            for (VariableAccess write : variableWrites) {
                names.add(write.getVariableName());
            }
            for (VariableDeclaration decl : variableDeclarations) {
                names.add(decl.getVariableName());
            }
            return names;
        }
        
        /**
         * Get variables that are only written to (not read).
         * These should be excluded from "out variables".
         */
        public Set<String> getWriteOnlyVariables() {
            Set<String> writeOnly = new HashSet<>();
            Set<String> readVars = new HashSet<>();
            for (VariableAccess read : variableReads) {
                readVars.add(read.getVariableName());
            }
            for (VariableAccess write : variableWrites) {
                if (!readVars.contains(write.getVariableName())) {
                    writeOnly.add(write.getVariableName());
                }
            }
            return writeOnly;
        }
    }
    
    /**
     * Represents a variable access (read or write).
     */
    public static class VariableAccess {
        private final VariableAccessType type;
        
        private final String variableName;
        
        private final int line;
        
        private final int column;
        
        public VariableAccess(VariableAccessType type, String variableName, int line, int column) {
            this.type = type;
            this.variableName = variableName;
            this.line = line;
            this.column = column;
        }
        
        public VariableAccessType getType() {
            return type;
        }
        
        public String getVariableName() {
            return variableName;
        }
        
        public int getLine() {
            return line;
        }
        
        public int getColumn() {
            return column;
        }
        
        @Override
        public String toString() {
            return String.format("%s '%s' at %d:%d", type, variableName, line, column);
        }
    }
    
    /**
     * Types of variable access.
     */
    public enum VariableAccessType {
        /** Variable read (identifier reference) */
        READ,
        /** Variable write (assignment target) */
        WRITE
    }
    
    /**
     * Represents a variable declaration.
     */
    public static class VariableDeclaration {
        private final String typeName;
        
        private final String variableName;
        
        private final int line;
        
        private final int column;
        
        public VariableDeclaration(String typeName, String variableName, int line, int column) {
            this.typeName = typeName;
            this.variableName = variableName;
            this.line = line;
            this.column = column;
        }
        
        public String getTypeName() {
            return typeName;
        }
        
        public String getVariableName() {
            return variableName;
        }
        
        public int getLine() {
            return line;
        }
        
        public int getColumn() {
            return column;
        }
        
        @Override
        public String toString() {
            return String.format("DECLARATION '%s' of type '%s' at %d:%d", variableName, typeName, line, column);
        }
    }
    
    /**
     * Detects all variable usage in the given AST node.
     *
     * @param node the AST node to analyze
     * @return the context containing all detected variable information
     */
    public Context detect(ASTNode node)
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
            // Exception parameter in catch clause is a variable declaration
            String exceptionVar = catchClause.getVariableName();
            if (exceptionVar != null && !exceptionVar.isEmpty()) {
                List<String> exceptionTypes = catchClause.getExceptionTypes();
                String typeName = exceptionTypes.isEmpty() ? "Exception" : exceptionTypes.get(0);
                context.addVariableDeclaration(new VariableDeclaration(typeName, exceptionVar,
                    catchClause.getBody().getLine(), catchClause.getBody().getColumn()));
            }
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
        // Record the variable declaration
        context.addVariableDeclaration(
            new VariableDeclaration(node.getTypeName(), node.getVariableName(), node.getLine(), node.getColumn()));
        
        // Visit the initializer expression
        visitNode(node.getInitialValue(), context);
        return null;
    }
    
    @Override
    public Void visit(AssignmentNode node, Context context)
        throws Exception {
        // Record the variable write
        if (node.getTarget() instanceof IdentifierNode) {
            IdentifierNode target = (IdentifierNode)node.getTarget();
            context.addVariableWrite(
                new VariableAccess(VariableAccessType.WRITE, target.getName(), node.getLine(), node.getColumn()));
        }
        else {
            // For complex targets (e.g., array[index], obj.field), visit them
            visitNode(node.getTarget(), context);
        }
        
        // Visit the value expression
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
        visitNode(node.getBody(), context);
        return null;
    }
    
    @Override
    public Void visit(MacroDefinitionNode node, Context context)
        throws Exception {
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
    public Void visit(IdentifierNode node, Context context)
        throws Exception {
        // Record the variable read
        context.addVariableRead(
            new VariableAccess(VariableAccessType.READ, node.getName(), node.getLine(), node.getColumn()));
        return null;
    }
    
    @Override
    public Void visit(BinaryOpNode node, Context context)
        throws Exception {
        // Check if this is an assignment operation (=, +=, -=, etc.)
        String operator = node.getOperator();
        boolean isAssignment =
            operator.equals("=") || operator.equals("+=") || operator.equals("-=") || operator.equals("*=")
                || operator.equals("/=") || operator.equals("%=") || operator.equals("&=") || operator.equals("|=")
                || operator.equals("^=") || operator.equals("<<=") || operator.equals(">>=") || operator.equals(">>>=");
        
        if (isAssignment && node.getLeft() instanceof IdentifierNode) {
            // Record the variable write
            IdentifierNode target = (IdentifierNode)node.getLeft();
            context.addVariableWrite(
                new VariableAccess(VariableAccessType.WRITE, target.getName(), node.getLine(), node.getColumn()));
            
            // For compound assignments (+=, -=, etc.), the left operand is also read
            if (!operator.equals("=")) {
                context.addVariableRead(
                    new VariableAccess(VariableAccessType.READ, target.getName(), node.getLine(), node.getColumn()));
            }
        }
        else {
            // Visit the left operand normally for non-assignment expressions
            visitExpression(node.getLeft(), context);
        }
        
        // Visit the right operand (the value being assigned)
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
        // Lambda parameters are variable declarations
        for (ParameterNode param : node.getParameters()) {
            context.addVariableDeclaration(new VariableDeclaration(param.getTypeName(), param.getParameterName(),
                node.getLine(), node.getColumn()));
        }
        
        // Visit the lambda body
        visitNode(node.getBody(), context);
        return null;
    }
    
    @Override
    public Void visit(MethodReferenceNode node, Context context)
        throws Exception {
        // Visit the target (method reference doesn't introduce new variables)
        visitExpression(node.getTarget(), context);
        return null;
    }
    
    @Override
    public Void visit(FieldAccessNode node, Context context)
        throws Exception {
        // Field access reads the target and writes the field (conceptually)
        // But we only track the target as a read
        visitExpression(node.getTarget(), context);
        return null;
    }
    
    @Override
    public Void visit(MethodCallNode node, Context context)
        throws Exception {
        // Visit the target and arguments
        visitNode(node.getTarget(), context);
        for (ExpressionNode arg : node.getArguments()) {
            visitExpression(arg, context);
        }
        return null;
    }
    
    @Override
    public Void visit(ConstructorCallNode node, Context context)
        throws Exception {
        // Visit the arguments
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
