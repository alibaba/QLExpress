package com.alibaba.qlexpress4.aparser;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Visitor that collects all variable names that are assigned (created or updated) in a script,
 * excluding locally-scoped variables such as typed declarations and for-each loop variables.
 * <p>
 * This is the counterpart to {@link OutVarNamesVisitor}: while {@code OutVarNamesVisitor} collects
 * variables that are read but never defined in the script (external inputs), this visitor collects
 * variables that are written to (outputs/side-effects on the execution context).
 * <p>
 * A variable is considered "assigned" if it appears on the left-hand side of an assignment expression
 * (both plain {@code =} and compound operators like {@code +=}), or is modified by an increment/decrement
 * operator ({@code a++}, {@code --b}).
 * <p>
 * Typed variable declarations (e.g. {@code int a = 10}) and for-each loop iteration variables
 * (e.g. {@code for(i : list)}) are NOT included because they create locally-scoped variables
 * that do not modify the execution context.
 *
 * @author chenjunwenhao
 */
public class AssignVarNamesVisitor extends ScopeStackVisitor {

    private final Set<String> assignVars = new HashSet<>();

    public AssignVarNamesVisitor() {
        super(new ExistVarStack(null));
    }

    private static class ExistVarStack implements ExistStack {
        private final ExistVarStack parent;

        private final Set<String> existVars = new HashSet<>();

        private ExistVarStack(ExistVarStack parent) {
            this.parent = parent;
        }

        public void add(String varName) {
            existVars.add(varName);
        }

        public boolean exist(String varName) {
            if (existVars.contains(varName)) {
                return true;
            }
            return parent != null && parent.exist(varName);
        }

        public ExistVarStack push() {
            return new ExistVarStack(this);
        }

        public ExistVarStack pop() {
            return parent;
        }
    }

    // --- typed variable declarations ---

    @Override
    public Void visitVariableDeclarator(QLParser.VariableDeclaratorContext ctx) {
        QLParser.VariableInitializerContext variableInitializerContext = ctx.variableInitializer();
        if (variableInitializerContext != null) {
            variableInitializerContext.accept(this);
        }
        ctx.variableDeclaratorId().accept(this);
        return null;
    }

    /**
     * Handle typed variable declarations like {@code int a = 10} or {@code String name = "hello"}.
     * The declared variable is added to the exist stack (for scope tracking) but NOT to assignVars,
     * because typed declarations create locally-scoped variables rather than assignments to
     * context variables.
     */
    @Override
    public Void visitVariableDeclaratorId(QLParser.VariableDeclaratorIdContext ctx) {
        QLParser.VarIdContext varIdContext = ctx.varId();
        String varName = varIdContext.getText();
        getStack().add(varName);
        return null;
    }

    // --- for-each loop variable ---

    @Override
    public Void visitForEachStatement(QLParser.ForEachStatementContext ctx) {
        ctx.expression().accept(this);
        push();
        getStack().add(ctx.varId().getText());
        ctx.blockStatements().accept(this);
        pop();
        return null;
    }

    // --- assignment expressions ---

    @Override
    public Void visitExpression(QLParser.ExpressionContext ctx) {
        QLParser.TernaryExprContext ternaryExprContext = ctx.ternaryExpr();
        if (ternaryExprContext != null) {
            ternaryExprContext.accept(this);
            return null;
        }

        QLParser.LeftHandSideContext leftHandSideContext = ctx.leftHandSide();
        if (isSimpleVariableLeftHandSide(leftHandSideContext)) {
            String leftVarName = leftHandSideContext.varId().getText();
            // Visit RHS first to collect any assigned vars in nested expressions
            ctx.expression().accept(this);
            assignVars.add(leftVarName);
            getStack().add(leftVarName);
            return null;
        }

        leftHandSideContext.accept(this);
        ctx.expression().accept(this);
        return null;
    }

    private boolean isSimpleVariableLeftHandSide(QLParser.LeftHandSideContext ctx) {
        return ctx.LPAREN() == null && ctx.pathPart().isEmpty();
    }

    /**
     * Handle path-based assignments like {@code x.field = value}.
     * The base variable is added to the exist stack (it must exist to set a field on it),
     * but NOT to assignVars since the variable itself is not being reassigned.
     */
    @Override
    public Void visitLeftHandSide(QLParser.LeftHandSideContext ctx) {
        List<QLParser.PathPartContext> pathPartContexts = ctx.pathPart();
        String leftVarName = ctx.varId().getText();
        if (pathPartContexts.isEmpty()) {
            getStack().add(leftVarName);
        }
        return null;
    }

    // --- suffix/prefix increment/decrement (e.g. a++, --b) as sub-expressions ---

    @Override
    public Void visitPrimary(QLParser.PrimaryContext ctx) {
        QLParser.PrimaryNoFixPathableContext primaryNoFixPathableContext = ctx.primaryNoFixPathable();
        if (primaryNoFixPathableContext instanceof QLParser.VarIdExprContext) {
            String varName = ((QLParser.VarIdExprContext)primaryNoFixPathableContext).varId().getText();
            QLParser.SuffixExpressContext suffix = ctx.suffixExpress();
            QLParser.PrefixExpressContext prefix = ctx.prefixExpress();
            if (suffix != null && isModifyOperator(suffix.getText())) {
                assignVars.add(varName);
                getStack().add(varName);
            }
            if (prefix != null && isModifyOperator(prefix.getText())) {
                assignVars.add(varName);
                getStack().add(varName);
            }
        }
        return super.visitPrimary(ctx);
    }

    private static boolean isModifyOperator(String op) {
        return "++".equals(op) || "--".equals(op);
    }

    public Set<String> getAssignVars() {
        return assignVars;
    }
}
