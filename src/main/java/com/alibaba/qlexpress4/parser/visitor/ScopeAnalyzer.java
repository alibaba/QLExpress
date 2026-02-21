package com.alibaba.qlexpress4.parser.visitor;

import com.alibaba.qlexpress4.parser.ast.*;

import java.util.*;

/**
 * ScopeAnalyzer analyzes variable scopes in an AST.
 * <p>
 * This visitor traverses the AST and builds a hierarchical model of scopes,
 * tracking variable declarations across nested blocks, lambdas, and control structures.
 * It can detect shadowed variables (variables declared in an inner scope with the
 * same name as variables in outer scopes).
 * <p>
 * The scope analysis can be used for:
 * - Compilation validation (detecting duplicate variable declarations)
 * - Variable resolution (determining which declaration a reference refers to)
 * - Code optimization (analyzing variable lifetimes)
 * - IDE features (code completion, go-to-definition)
 *
 * @author QLExpress Team
 */
public class ScopeAnalyzer implements ASTVisitor<Void, ScopeAnalyzer.Context> {
    
    /**
     * Represents a single scope in the program.
     * <p>
     * Each scope contains variable declarations and optionally a parent scope.
     * Scopes form a tree structure reflecting the nesting of blocks in the program.
     */
    public static class Scope {
        private final ScopeType type;
        
        private final Scope parent;
        
        private final int depth;
        
        private final Map<String, VariableInfo> variables;
        
        private final List<Scope> children;
        
        private final int line;
        
        private final int column;
        
        public Scope(ScopeType type, Scope parent, int line, int column) {
            this.type = type;
            this.parent = parent;
            this.depth = parent == null ? 0 : parent.depth + 1;
            this.variables = new LinkedHashMap<>();
            this.children = new ArrayList<>();
            this.line = line;
            this.column = column;
        }
        
        public ScopeType getType() {
            return type;
        }
        
        public Scope getParent() {
            return parent;
        }
        
        public int getDepth() {
            return depth;
        }
        
        public Map<String, VariableInfo> getVariables() {
            return Collections.unmodifiableMap(variables);
        }
        
        public List<Scope> getChildren() {
            return Collections.unmodifiableList(children);
        }
        
        public int getLine() {
            return line;
        }
        
        public int getColumn() {
            return column;
        }
        
        /**
         * Adds a variable declaration to this scope.
         *
         * @param name the variable name
         * @param info the variable information
         * @return true if the variable was added, false if a variable with this name already exists
         */
        public boolean addVariable(String name, VariableInfo info) {
            if (variables.containsKey(name)) {
                return false;
            }
            variables.put(name, info);
            return true;
        }
        
        /**
         * Checks if this scope or any parent scope contains a variable with the given name.
         *
         * @param name the variable name
         * @return the VariableInfo if found, null otherwise
         */
        public VariableInfo findVariable(String name) {
            VariableInfo info = variables.get(name);
            if (info != null) {
                return info;
            }
            if (parent != null) {
                return parent.findVariable(name);
            }
            return null;
        }
        
        /**
         * Checks if this scope (not including parents) contains a variable with the given name.
         *
         * @param name the variable name
         * @return true if this scope directly contains the variable
         */
        public boolean containsVariable(String name) {
            return variables.containsKey(name);
        }
        
        /**
         * Finds all shadowed variables in this scope.
         * <p>
         * A variable is shadowed if it has the same name as a variable in a parent scope.
         *
         * @return list of shadowed variable information
         */
        public List<ShadowedVariable> findShadowedVariables() {
            List<ShadowedVariable> shadowed = new ArrayList<>();
            for (Map.Entry<String, VariableInfo> entry : variables.entrySet()) {
                String name = entry.getKey();
                VariableInfo parentInfo = null;
                Scope current = parent;
                while (current != null) {
                    if (current.containsVariable(name)) {
                        parentInfo = current.getVariables().get(name);
                        break;
                    }
                    current = current.parent;
                }
                if (parentInfo != null) {
                    shadowed.add(new ShadowedVariable(name, entry.getValue(), parentInfo));
                }
            }
            return shadowed;
        }
        
        public void addChild(Scope child) {
            children.add(child);
        }
        
        @Override
        public String toString() {
            return String.format("Scope[type=%s, depth=%d, vars=%d]", type, depth, variables.size());
        }
    }
    
    /**
     * Types of scopes in the program.
     */
    public enum ScopeType {
        /** Root program scope */
        PROGRAM,
        /** Function or method scope */
        FUNCTION,
        /** Block scope (braced statements) */
        BLOCK,
        /** Lambda expression scope */
        LAMBDA,
        /** For loop scope */
        FOR_LOOP,
        /** Catch clause scope */
        CATCH_CLAUSE,
        /** Switch case scope */
        SWITCH_CASE
    }
    
    /**
     * Information about a variable in a scope.
     */
    public static class VariableInfo {
        private final String name;
        
        private final String typeName;
        
        private final int line;
        
        private final int column;
        
        private final Scope declaredIn;
        
        public VariableInfo(String name, String typeName, int line, int column, Scope declaredIn) {
            this.name = name;
            this.typeName = typeName;
            this.line = line;
            this.column = column;
            this.declaredIn = declaredIn;
        }
        
        public String getName() {
            return name;
        }
        
        public String getTypeName() {
            return typeName;
        }
        
        public int getLine() {
            return line;
        }
        
        public int getColumn() {
            return column;
        }
        
        public Scope getDeclaredIn() {
            return declaredIn;
        }
        
        @Override
        public String toString() {
            return String.format("'%s:%s' at %d:%d", typeName, name, line, column);
        }
    }
    
    /**
     * Information about a shadowed variable.
     */
    public static class ShadowedVariable {
        private final String name;
        
        private final VariableInfo shadowing;
        
        private final VariableInfo shadowed;
        
        public ShadowedVariable(String name, VariableInfo shadowing, VariableInfo shadowed) {
            this.name = name;
            this.shadowing = shadowing;
            this.shadowed = shadowed;
        }
        
        public String getName() {
            return name;
        }
        
        public VariableInfo getShadowing() {
            return shadowing;
        }
        
        public VariableInfo getShadowed() {
            return shadowed;
        }
        
        @Override
        public String toString() {
            return String.format("'%s' declared at %d:%d shadows declaration at %d:%d",
                name,
                shadowing.getLine(),
                shadowing.getColumn(),
                shadowed.getLine(),
                shadowed.getColumn());
        }
    }
    
    /**
     * Context for scope analysis.
     * <p>
     * Holds the current scope during traversal and the root scope after analysis.
     */
    public static class Context {
        private Scope rootScope;
        
        private Scope currentScope;
        
        private final List<ShadowedVariable> allShadowedVariables;
        
        public Context() {
            this.allShadowedVariables = new ArrayList<>();
        }
        
        public Scope getRootScope() {
            return rootScope;
        }
        
        public void setRootScope(Scope rootScope) {
            this.rootScope = rootScope;
        }
        
        public Scope getCurrentScope() {
            return currentScope;
        }
        
        public void setCurrentScope(Scope currentScope) {
            this.currentScope = currentScope;
        }
        
        public List<ShadowedVariable> getAllShadowedVariables() {
            return Collections.unmodifiableList(allShadowedVariables);
        }
        
        public void addShadowedVariable(ShadowedVariable shadowed) {
            allShadowedVariables.add(shadowed);
        }
        
        /**
         * Finds all shadowed variables in the entire scope tree.
         *
         * @return list of all shadowed variables
         */
        public List<ShadowedVariable> findAllShadowedVariables() {
            List<ShadowedVariable> result = new ArrayList<>();
            if (rootScope != null) {
                collectShadowedVariables(rootScope, result);
            }
            return result;
        }
        
        private void collectShadowedVariables(Scope scope, List<ShadowedVariable> result) {
            result.addAll(scope.findShadowedVariables());
            for (Scope child : scope.getChildren()) {
                collectShadowedVariables(child, result);
            }
        }
    }
    
    /**
     * Analyzes the scope structure of the given AST node.
     *
     * @param node the AST node to analyze
     * @return the context containing the root scope and all shadowed variables
     */
    public Context analyze(ASTNode node)
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
        // Create root scope
        Scope rootScope = new Scope(ScopeType.PROGRAM, null, node.getLine(), node.getColumn());
        context.setRootScope(rootScope);
        context.setCurrentScope(rootScope);
        
        // Visit all statements in the program
        for (StatementNode statement : node.getStatements()) {
            ((ASTNode)statement).accept(this, context);
        }
        
        return null;
    }
    
    @Override
    public Void visit(BlockNode node, Context context)
        throws Exception {
        // Create a new block scope
        Scope blockScope = new Scope(ScopeType.BLOCK, context.getCurrentScope(), node.getLine(), node.getColumn());
        context.getCurrentScope().addChild(blockScope);
        context.setCurrentScope(blockScope);
        
        // Visit all statements in the block
        for (StatementNode statement : node.getStatements()) {
            ((ASTNode)statement).accept(this, context);
        }
        
        // Restore parent scope
        context.setCurrentScope(blockScope.getParent());
        
        return null;
    }
    
    @Override
    public Void visit(IfNode node, Context context)
        throws Exception {
        // Visit condition (no new scope)
        visitExpression(node.getCondition(), context);
        
        // Visit then body (creates new scope if it's a block)
        visitNode(node.getThenBody(), context);
        
        // Visit else body (creates new scope if it's a block)
        visitNode(node.getElseBody(), context);
        
        return null;
    }
    
    @Override
    public Void visit(WhileNode node, Context context)
        throws Exception {
        // Visit condition (no new scope)
        visitExpression(node.getCondition(), context);
        
        // Visit body (creates new scope if it's a block)
        visitNode(node.getBody(), context);
        
        return null;
    }
    
    @Override
    public Void visit(ForNode node, Context context)
        throws Exception {
        // Create a new for-loop scope
        Scope forScope = new Scope(ScopeType.FOR_LOOP, context.getCurrentScope(), node.getLine(), node.getColumn());
        context.getCurrentScope().addChild(forScope);
        context.setCurrentScope(forScope);
        
        // Visit init (may contain variable declarations)
        visitNode(node.getInit(), context);
        
        // Visit condition
        visitExpression(node.getCondition(), context);
        
        // Visit update
        visitExpression(node.getUpdate(), context);
        
        // Visit body (creates nested scope if it's a block)
        visitNode(node.getBody(), context);
        
        // Restore parent scope
        context.setCurrentScope(forScope.getParent());
        
        return null;
    }
    
    @Override
    public Void visit(SwitchNode node, Context context)
        throws Exception {
        // Visit value (no new scope)
        visitExpression(node.getValue(), context);
        
        // Visit each case (each case creates its own scope)
        for (SwitchCaseNode caseNode : node.getCases()) {
            visitSwitchCase(caseNode, context);
        }
        
        return null;
    }
    
    private Void visitSwitchCase(SwitchCaseNode caseNode, Context context)
        throws Exception {
        // Create a new switch-case scope
        int line = 0, column = 0;
        if (!caseNode.getStatements().isEmpty()) {
            Node firstStmt = caseNode.getStatements().get(0);
            if (firstStmt instanceof ASTNode) {
                line = ((ASTNode)firstStmt).getLine();
                column = ((ASTNode)firstStmt).getColumn();
            }
        }
        Scope caseScope = new Scope(ScopeType.SWITCH_CASE, context.getCurrentScope(), line, column);
        context.getCurrentScope().addChild(caseScope);
        context.setCurrentScope(caseScope);
        
        // Visit condition (if present)
        visitExpression(caseNode.getCondition(), context);
        
        // Visit all statements in the case
        for (StatementNode stmt : caseNode.getStatements()) {
            visitNode(stmt, context);
        }
        
        // Restore parent scope
        context.setCurrentScope(caseScope.getParent());
        
        return null;
    }
    
    @Override
    public Void visit(TryCatchNode node, Context context)
        throws Exception {
        // Visit try block (creates new scope)
        visitNode(node.getTryBlock(), context);
        
        // Visit each catch clause (each creates its own scope)
        for (CatchClauseNode catchClause : node.getCatchClauses()) {
            visitCatchClause(catchClause, context);
        }
        
        // Visit finally block (creates new scope)
        visitNode(node.getFinallyBlock(), context);
        
        return null;
    }
    
    private Void visitCatchClause(CatchClauseNode catchClause, Context context)
        throws Exception {
        // Create a new catch clause scope
        Scope catchScope = new Scope(ScopeType.CATCH_CLAUSE, context.getCurrentScope(), catchClause.getBody().getLine(),
            catchClause.getBody().getColumn());
        context.getCurrentScope().addChild(catchScope);
        context.setCurrentScope(catchScope);
        
        // Add exception variable to the catch scope
        String exceptionVar = catchClause.getVariableName();
        if (exceptionVar != null && !exceptionVar.isEmpty()) {
            List<String> exceptionTypes = catchClause.getExceptionTypes();
            String typeName = exceptionTypes.isEmpty() ? "Exception" : exceptionTypes.get(0);
            
            // Check for shadowing
            VariableInfo existing = context.getCurrentScope().findVariable(exceptionVar);
            if (existing != null && existing != context.getCurrentScope().getVariables().get(exceptionVar)) {
                // Variable is shadowed
                VariableInfo newVar = new VariableInfo(exceptionVar, typeName, catchClause.getBody().getLine(),
                    catchClause.getBody().getColumn(), context.getCurrentScope());
                context.addShadowedVariable(new ShadowedVariable(exceptionVar, newVar, existing));
            }
            
            context.getCurrentScope()
                .addVariable(exceptionVar,
                    new VariableInfo(exceptionVar, typeName, catchClause.getBody().getLine(),
                        catchClause.getBody().getColumn(), context.getCurrentScope()));
        }
        
        // Visit catch body
        visitNode(catchClause.getBody(), context);
        
        // Restore parent scope
        context.setCurrentScope(catchScope.getParent());
        
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
        // Add variable declaration to current scope
        String varName = node.getVariableName();
        String typeName = node.getTypeName();
        
        // Check for shadowing
        VariableInfo existing = context.getCurrentScope().findVariable(varName);
        if (existing != null && !context.getCurrentScope().containsVariable(varName)) {
            // Variable is shadowed (defined in parent scope)
            VariableInfo newVar =
                new VariableInfo(varName, typeName, node.getLine(), node.getColumn(), context.getCurrentScope());
            context.addShadowedVariable(new ShadowedVariable(varName, newVar, existing));
        }
        
        context.getCurrentScope()
            .addVariable(varName,
                new VariableInfo(varName, typeName, node.getLine(), node.getColumn(), context.getCurrentScope()));
        
        // Visit the initializer expression
        visitNode(node.getInitialValue(), context);
        
        return null;
    }
    
    @Override
    public Void visit(AssignmentNode node, Context context)
        throws Exception {
        // Visit target and value
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
        // Create a new function scope
        Scope functionScope =
            new Scope(ScopeType.FUNCTION, context.getCurrentScope(), node.getLine(), node.getColumn());
        context.getCurrentScope().addChild(functionScope);
        context.setCurrentScope(functionScope);
        
        // Add function parameters to the function scope
        // Note: FunctionDefinitionNode structure may vary - adjust as needed
        visitNode(node.getBody(), context);
        
        // Restore parent scope
        context.setCurrentScope(functionScope.getParent());
        
        return null;
    }
    
    @Override
    public Void visit(MacroDefinitionNode node, Context context)
        throws Exception {
        // Visit macro body
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
        // Variable reference - could be resolved to a declaration
        // This is useful for building symbol tables but not required for basic scope analysis
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
        // Create a new lambda scope
        Scope lambdaScope = new Scope(ScopeType.LAMBDA, context.getCurrentScope(), node.getLine(), node.getColumn());
        context.getCurrentScope().addChild(lambdaScope);
        context.setCurrentScope(lambdaScope);
        
        // Add lambda parameters to the lambda scope
        for (ParameterNode param : node.getParameters()) {
            String paramName = param.getParameterName();
            String typeName = param.getTypeName();
            
            // Check for shadowing
            VariableInfo existing = context.getCurrentScope().findVariable(paramName);
            if (existing != null && !context.getCurrentScope().containsVariable(paramName)) {
                // Parameter is shadowed
                VariableInfo newParam =
                    new VariableInfo(paramName, typeName, node.getLine(), node.getColumn(), context.getCurrentScope());
                context.addShadowedVariable(new ShadowedVariable(paramName, newParam, existing));
            }
            
            context.getCurrentScope()
                .addVariable(paramName,
                    new VariableInfo(paramName, typeName, node.getLine(), node.getColumn(), context.getCurrentScope()));
        }
        
        // Visit lambda body (creates nested scope if it's a block)
        visitNode(node.getBody(), context);
        
        // Restore parent scope
        context.setCurrentScope(lambdaScope.getParent());
        
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
