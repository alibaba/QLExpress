package com.alibaba.qlexpress4.parser.visitor;

import com.alibaba.qlexpress4.common.BuiltInTypesSet;
import com.alibaba.qlexpress4.parser.ast.*;

import java.util.*;

/**
 * OutVariableDetector detects variables that are read but not declared before use.
 * <p>
 * This visitor tracks which variables have been declared at each point in the AST.
 * A variable is considered an "out variable" if it is read before being declared.
 *
 * @author QLExpress Team
 */
public class OutVariableDetector implements ASTVisitor<Void, OutVariableDetector.Context> {

    private static final Set<String> COMMON_JAVA_CLASSES = new HashSet<>(Arrays.asList(
        "Math", "String", "System", "Integer", "Long", "Double", "Float",
        "Boolean", "Character", "Byte", "Short", "Object", "Class",
        "List", "Map", "Set", "Collection", "ArrayList", "LinkedList",
        "HashMap", "LinkedHashMap", "TreeMap", "HashSet", "TreeSet",
        "Collections", "Arrays", "Objects", "Optional",
        "Runnable", "Callable", "Thread", "Exception", "RuntimeException",
        "Date", "Calendar", "SimpleDateFormat", "UUID",
        "StringBuilder", "StringBuffer", "Pattern", "Matcher"
    ));

    private static final Set<String> JAVA_PACKAGE_PREFIXES = new HashSet<>(Arrays.asList(
        "java", "javax", "org", "com"
    ));

    /**
     * Context for out variable detection.
     * <p>
     * Tracks declared variables and collects out variables.
     */
    public static class Context {
        // Stack of declared variable sets, one per scope level
        private final Deque<Set<String>> declaredVarsStack = new ArrayDeque<>();

        private final Set<String> outVars = new HashSet<>();

        public Context() {
            // Push root scope
            declaredVarsStack.push(new HashSet<>());
        }

        /**
         * Declare a variable (adds it to the current scope).
         *
         * @param varName the variable name to declare
         */
        public void declareVariable(String varName) {
            declaredVarsStack.peek().add(varName);
        }

        /**
         * Check if a variable has been declared in any accessible scope.
         *
         * @param varName the variable name to check
         * @return true if the variable has been declared
         */
        public boolean isDeclared(String varName) {
            for (Set<String> scope : declaredVarsStack) {
                if (scope.contains(varName)) {
                    return true;
                }
            }
            return false;
        }

        /**
         * Record a variable read. If the variable hasn't been declared yet,
         * it's added to the out variables set.
         *
         * @param varName the variable name being read
         */
        public void readVariable(String varName) {
            if (!isDeclared(varName)) {
                outVars.add(varName);
            }
        }

        /**
         * Push a new scope (for functions, lambdas, etc.).
         */
        public void pushScope() {
            declaredVarsStack.push(new HashSet<>());
        }

        /**
         * Pop the current scope.
         */
        public void popScope() {
            declaredVarsStack.pop();
        }

        /**
         * Get the set of out variables (variables read before being declared).
         *
         * @return the set of out variable names
         */
        public Set<String> getOutVars() {
            return Collections.unmodifiableSet(outVars);
        }

        // Package-private access for IfNode visitor
        Set<String> getDeclaredVars() {
            Set<String> allDeclared = new HashSet<>();
            for (Set<String> scope : declaredVarsStack) {
                allDeclared.addAll(scope);
            }
            return allDeclared;
        }

        void setDeclaredVars(Set<String> vars) {
            // Clear all scopes and set the root scope to the given vars
            declaredVarsStack.clear();
            Set<String> rootScope = new HashSet<>(vars);
            declaredVarsStack.push(rootScope);
        }
    }

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
        // Blocks don't create new scope for out variable detection
        // (unlike actual execution where blocks create scopes)
        for (StatementNode statement : node.getStatements()) {
            ((ASTNode)statement).accept(this, context);
        }
        return null;
    }

    @Override
    public Void visit(IfNode node, Context context)
        throws Exception {
        // Visit condition first
        visitExpression(node.getCondition(), context);

        // For out variable detection, we need to consider both branches
        // Variables used in either branch that aren't declared before are out vars
        // Track declared vars before visiting branches
        Set<String> declaredBeforeIf = context.getDeclaredVars();

        // Visit then branch
        visitNode(node.getThenBody(), context);

        // Restore declared vars to state before if, then visit else branch
        // (this ensures variables declared only in then branch don't affect else branch)
        Set<String> declaredAfterThen = context.getDeclaredVars();
        context.setDeclaredVars(declaredBeforeIf);

        visitNode(node.getElseBody(), context);

        // Merge: any variable declared in either branch is now declared
        Set<String> declaredAfterElse = context.getDeclaredVars();
        Set<String> merged = new HashSet<>(declaredBeforeIf);
        merged.addAll(declaredAfterThen);
        merged.addAll(declaredAfterElse);
        context.setDeclaredVars(merged);

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
            // Exception parameter is a declaration
            String exceptionVar = catchClause.getVariableName();
            if (exceptionVar != null && !exceptionVar.isEmpty()) {
                context.declareVariable(exceptionVar);
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
        // Declare the variable before visiting the initializer
        context.declareVariable(node.getVariableName());
        visitNode(node.getInitialValue(), context);
        return null;
    }

    @Override
    public Void visit(AssignmentNode node, Context context)
        throws Exception {
        if (node.getTarget() instanceof IdentifierNode) {
            IdentifierNode target = (IdentifierNode)node.getTarget();
            // For compound assignments (+=, -=, etc.), we need to read the variable first
            String operator = node.getOperator();
            if (!operator.equals("=")) {
                context.readVariable(target.getName());
            }
            // Then declare it (assignment is a declaration)
            context.declareVariable(target.getName());
        }
        else {
            visitNode(node.getTarget(), context);
        }
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
        // Function parameters create a new scope
        context.pushScope();
        try {
            // Parameters are local to the function
            for (ParameterNode param : node.getParameters()) {
                context.declareVariable(param.getParameterName());
            }
            visitNode(node.getBody(), context);
            return null;
        }
        finally {
            context.popScope();
        }
    }

    @Override
    public Void visit(MacroDefinitionNode node, Context context)
        throws Exception {
        // Macro definitions create a new scope
        context.pushScope();
        try {
            // Visit the body (macros don't have explicit parameters in the AST)
            visitNode(node.getBody(), context);
            return null;
        }
        finally {
            context.popScope();
        }
    }

    @Override
    public Void visit(LiteralNode node, Context context)
        throws Exception {
        return null;
    }

    @Override
    public Void visit(IdentifierNode node, Context context)
        throws Exception {
        String varName = node.getName();

        // Check if this identifier is a class reference (not a variable)
        // Class references like Math, String, List, etc. should not be considered out variables
        if (isClassReference(varName)) {
            return null;
        }

        context.readVariable(varName);
        return null;
    }

    /**
     * Check if an identifier is a class reference.
     *
     * @param identifier the identifier to check
     * @return true if the identifier is a class reference
     */
    private boolean isClassReference(String identifier) {
        // Check built-in types (int, long, etc. and their wrapper classes)
        if (BuiltInTypesSet.getCls(identifier) != null) {
            return true;
        }

        // Check common Java classes that are typically used in QLExpress scripts
        if (COMMON_JAVA_CLASSES.contains(identifier)) {
            return true;
        }

        // Check if identifier is a Java package prefix
        // These are used in qualified class names like java.lang.Math
        return JAVA_PACKAGE_PREFIXES.contains(identifier);
    }

    @Override
    public Void visit(BinaryOpNode node, Context context)
        throws Exception {
        String operator = node.getOperator();
        boolean isAssignment =
            operator.equals("=") || operator.equals("+=") || operator.equals("-=") || operator.equals("*=")
                || operator.equals("/=") || operator.equals("%=") || operator.equals("&=") || operator.equals("|=")
                || operator.equals("^=") || operator.equals("<<=") || operator.equals(">>=") || operator.equals(">>>=");

        if (isAssignment && node.getLeft() instanceof IdentifierNode) {
            IdentifierNode target = (IdentifierNode)node.getLeft();
            // For compound assignments, read first
            if (!operator.equals("=")) {
                context.readVariable(target.getName());
            }
            // Then declare
            context.declareVariable(target.getName());
        }
        else {
            visitExpression(node.getLeft(), context);
        }
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
        // Lambda parameters create a new scope
        context.pushScope();
        try {
            for (ParameterNode param : node.getParameters()) {
                context.declareVariable(param.getParameterName());
            }
            visitNode(node.getBody(), context);
            return null;
        }
        finally {
            context.popScope();
        }
    }

    @Override
    public Void visit(MethodReferenceNode node, Context context)
        throws Exception {
        visitExpression(node.getTarget(), context);
        return null;
    }

    @Override
    public Void visit(FieldAccessNode node, Context context)
        throws Exception {
        visitExpression(node.getTarget(), context);
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

    /**
     * Detects out variables in the given AST node.
     *
     * @param node the AST node to analyze
     * @return the set of out variable names
     */
    public Set<String> detect(ASTNode node)
        throws Exception {
        if (node == null) {
            return Collections.emptySet();
        }

        Context context = new Context();
        node.accept(this, context);
        return context.getOutVars();
    }
}
