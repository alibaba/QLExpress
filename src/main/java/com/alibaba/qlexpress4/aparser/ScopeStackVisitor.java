package com.alibaba.qlexpress4.aparser;

public abstract class ScopeStackVisitor extends QLParserBaseVisitor<Void> {
    
    private ExistStack existStack;
    
    protected ScopeStackVisitor(ExistStack existStack) {
        this.existStack = existStack;
    }
    
    // scope
    @Override
    public Void visitBlockExpr(QLParser.BlockExprContext ctx) {
        push();
        super.visitBlockExpr(ctx);
        pop();
        return null;
    }
    
    @Override
    public Void visitQlIf(QLParser.QlIfContext qlIfContext) {
        qlIfContext.condition.accept(this);
        
        push();
        qlIfContext.thenBody().accept(this);
        pop();
        
        QLParser.ElseBodyContext elseBodyContext = qlIfContext.elseBody();
        if (elseBodyContext != null) {
            push();
            elseBodyContext.accept(this);
            pop();
        }
        
        return null;
    }
    
    @Override
    public Void visitSwitchExpr(QLParser.SwitchExprContext ctx) {
        // Visit switch expression
        ctx.expression().accept(this);
        
        // Visit switch body with scope
        QLParser.SwitchCaseGroupsContext groupsContext = ctx.switchCaseGroups();
        if (groupsContext != null) {
            push();
            // Visit each case group
            for (QLParser.SwitchCaseGroupContext group : groupsContext.switchCaseGroup()) {
                if (group instanceof QLParser.SwitchStatementGroupContext) {
                    // Traditional switch statement group
                    QLParser.SwitchStatementGroupContext stmtGroup = (QLParser.SwitchStatementGroupContext)group;
                    // Visit case labels (they contain expressions)
                    if (stmtGroup.switchLabels() != null) {
                        for (QLParser.SwitchLabelContext label : stmtGroup.switchLabels().switchLabel()) {
                            if (label.expression() != null) {
                                label.expression().accept(this);
                            }
                        }
                    }
                    // Visit case body
                    if (stmtGroup.blockStatements() != null) {
                        stmtGroup.blockStatements().accept(this);
                    }
                }
                else if (group instanceof QLParser.SwitchExprGroupContext) {
                    // Switch expression group
                    QLParser.SwitchExprGroupContext exprGroup = (QLParser.SwitchExprGroupContext)group;
                    // Visit case labels (they contain expressions)
                    if (exprGroup.switchExpressionLabel() != null) {
                        QLParser.SwitchExpressionLabelContext label = exprGroup.switchExpressionLabel();
                        if (label.expressionList() != null) {
                            for (QLParser.ExpressionContext expr : label.expressionList().expression()) {
                                expr.accept(this);
                            }
                        }
                    }
                    // Visit result expression
                    if (exprGroup.expression() != null) {
                        exprGroup.expression().accept(this);
                    }
                }
            }
            pop();
        }
        
        return null;
    }
    
    @Override
    public Void visitTryCatchExpr(QLParser.TryCatchExprContext ctx) {
        QLParser.BlockStatementsContext blockStatementsContext = ctx.blockStatements();
        if (blockStatementsContext != null) {
            push();
            blockStatementsContext.accept(this);
            pop();
        }
        
        QLParser.TryCatchesContext tryCatchesContext = ctx.tryCatches();
        if (tryCatchesContext != null) {
            tryCatchesContext.accept(this);
        }
        
        QLParser.TryFinallyContext tryFinallyContext = ctx.tryFinally();
        if (tryFinallyContext != null) {
            push();
            tryFinallyContext.accept(this);
            pop();
        }
        
        return null;
    }
    
    @Override
    public Void visitForEachStatement(QLParser.ForEachStatementContext ctx) {
        ctx.expression().accept(this);
        push();
        getStack().add(ctx.varId().getText());
        ctx.blockStatements().accept(this);
        pop();
        return null;
    }
    
    @Override
    public Void visitLambdaExpr(QLParser.LambdaExprContext ctx) {
        push();
        QLParser.LambdaParametersContext params = ctx.lambdaParameters();
        if (params.varId() != null) {
            getStack().add(params.varId().getText());
        }
        else if (params.formalOrInferredParameterList() != null) {
            params.formalOrInferredParameterList().accept(this);
        }
        if (ctx.blockStatements() != null) {
            ctx.blockStatements().accept(this);
        }
        else if (ctx.expression() != null) {
            ctx.expression().accept(this);
        }
        pop();
        return null;
    }
    
    @Override
    public Void visitTryCatch(QLParser.TryCatchContext ctx) {
        push();
        if (ctx.catchParams() != null && ctx.catchParams().varId() != null) {
            getStack().add(ctx.catchParams().varId().getText());
        }
        ctx.blockStatements().accept(this);
        pop();
        return null;
    }
    
    @Override
    public Void visitTraditionalForStatement(QLParser.TraditionalForStatementContext ctx) {
        push();
        if (ctx.forInit() != null) {
            ctx.forInit().accept(this);
        }
        if (ctx.forCondition != null) {
            ctx.forCondition.accept(this);
        }
        if (ctx.forUpdate != null) {
            ctx.forUpdate.accept(this);
        }
        if (ctx.blockStatements() != null) {
            ctx.blockStatements().accept(this);
        }
        pop();
        return null;
    }
    
    @Override
    public Void visitFunctionStatement(QLParser.FunctionStatementContext ctx) {
        ctx.varId().accept(this);
        
        push();
        QLParser.FormalOrInferredParameterListContext paramList = ctx.formalOrInferredParameterList();
        if (paramList != null) {
            paramList.accept(this);
        }
        
        QLParser.BlockStatementsContext functionBlockStatements = ctx.blockStatements();
        if (functionBlockStatements != null) {
            functionBlockStatements.accept(this);
        }
        pop();
        
        return null;
    }
    
    public void push() {
        this.existStack = this.existStack.push();
    }
    
    public void pop() {
        this.existStack = this.existStack.pop();
    }
    
    public ExistStack getStack() {
        return existStack;
    }
}
