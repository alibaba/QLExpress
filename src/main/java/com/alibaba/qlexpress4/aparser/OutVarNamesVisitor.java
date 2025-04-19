package com.alibaba.qlexpress4.aparser;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Author: DQinYuan
 */
public class OutVarNamesVisitor extends QLParserBaseVisitor<Void> {

    private final Set<String> outVars = new HashSet<>();

    private ExistVarStack existVarStack = new ExistVarStack(null);

    private static class ExistVarStack {
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

    // scope
    @Override
    public Void visitBlockExpr(QLParser.BlockExprContext ctx) {
        this.existVarStack = this.existVarStack.push();
        super.visitBlockExpr(ctx);
        this.existVarStack = this.existVarStack.pop();
        return null;
    }

    @Override
    public Void visitQlIf(QLParser.QlIfContext qlIfContext) {
        qlIfContext.condition.accept(this);

        this.existVarStack = this.existVarStack.push();
        qlIfContext.thenBody().accept(this);
        this.existVarStack = this.existVarStack.pop();

        this.existVarStack = this.existVarStack.push();
        qlIfContext.elseBody().accept(this);
        this.existVarStack = this.existVarStack.pop();

        return null;
    }

    @Override
    public Void visitTryCatchExpr(QLParser.TryCatchExprContext ctx) {
        QLParser.BlockStatementsContext blockStatementsContext = ctx.blockStatements();
        if (blockStatementsContext != null) {
            this.existVarStack = this.existVarStack.push();
            blockStatementsContext.accept(this);
            this.existVarStack = this.existVarStack.pop();
        }

        QLParser.TryCatchesContext tryCatchesContext = ctx.tryCatches();
        if (tryCatchesContext != null) {
            tryCatchesContext.accept(this);
        }


        QLParser.TryFinallyContext tryFinallyContext = ctx.tryFinally();
        if (tryFinallyContext != null) {
            this.existVarStack = this.existVarStack.push();
            tryFinallyContext.accept(this);
            this.existVarStack = this.existVarStack.pop();
        }

        return null;
    }

    @Override
    public Void visitTryCatch(QLParser.TryCatchContext ctx) {
        this.existVarStack = this.existVarStack.push();
        super.visitTryCatch(ctx);
        this.existVarStack = this.existVarStack.pop();
        return null;
    }

    // collect exist variable name

    /**
     * @param ctx int a = 10;
     * @return a
     */
    @Override
    public Void visitVariableDeclaratorId(QLParser.VariableDeclaratorIdContext ctx) {
        QLParser.VarIdContext varIdContext = ctx.varId();
        existVarStack.add(varIdContext.getText());
        return null;
    }

    @Override
    public Void visitLeftHandSide(QLParser.LeftHandSideContext ctx) {
        List<QLParser.PathPartContext> pathPartContexts = ctx.pathPart();
        String leftVarName = ctx.varId().getText();
        if (pathPartContexts.isEmpty()) {
            existVarStack.add(leftVarName);
        } else if (!existVarStack.exist(leftVarName)) {
            outVars.add(leftVarName);
        }
        return null;
    }

    // collect out variables name

    @Override
    public Void visitVarIdExpr(QLParser.VarIdExprContext ctx) {
        String varName = ctx.varId().getText();
        if (!existVarStack.exist(varName)) {
            outVars.add(varName);
        }
        return null;
    }

    public Set<String> getOutVars() {
        return outVars;
    }
}
