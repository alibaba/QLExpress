package com.alibaba.qlexpress4.aparser;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class OutFunctionVisitor extends ScopeStackVisitor {
    
    private final Set<String> outFunctions = new HashSet<>();
    
    public OutFunctionVisitor() {
        super(new ExistFunctionStack(null));
    }
    
    private static class ExistFunctionStack implements ExistStack {
        private final ExistFunctionStack parent;
        
        private final Set<String> existVars = new HashSet<>();
        
        private ExistFunctionStack(ExistFunctionStack parent) {
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
        
        public ExistFunctionStack push() {
            return new ExistFunctionStack(this);
        }
        
        public ExistFunctionStack pop() {
            return parent;
        }
    }
    
    @Override
    public Void visitBlockStatements(QLParser.BlockStatementsContext ctx) {
        List<QLParser.BlockStatementContext> nonEmptyChildren = ctx.blockStatement()
            .stream()
            .filter(bs -> !(bs instanceof QLParser.EmptyStatementContext))
            .collect(Collectors.toList());
        // process all function definitions to support forward references
        for (QLParser.BlockStatementContext child : nonEmptyChildren) {
            if (child instanceof QLParser.FunctionStatementContext) {
                child.accept(this);
            }
        }
        
        for (QLParser.BlockStatementContext child : nonEmptyChildren) {
            if (!(child instanceof QLParser.FunctionStatementContext)) {
                child.accept(this);
            }
        }
        
        return null;
    }
    
    @Override
    public Void visitVarIdExpr(QLParser.VarIdExprContext ctx) {
        if (ctx.LPAREN() != null) {
            String functionName = ctx.getStart().getText();
            if (!getStack().exist(functionName)) {
                outFunctions.add(functionName);
            }
        }
        return super.visitVarIdExpr(ctx);
    }
    
    @Override
    public Void visitLeftHandSide(QLParser.LeftHandSideContext ctx) {
        if (ctx.LPAREN() != null) {
            String functionName = ctx.getStart().getText();
            if (!getStack().exist(functionName)) {
                outFunctions.add(functionName);
            }
        }
        return super.visitLeftHandSide(ctx);
    }
    
    @Override
    public Void visitFunctionStatement(QLParser.FunctionStatementContext ctx) {
        String functionName = ctx.varId().getText();
        getStack().add(functionName);
        
        QLParser.FormalOrInferredParameterListContext paramList = ctx.formalOrInferredParameterList();
        if (paramList != null) {
            paramList.accept(this);
        }
        
        QLParser.BlockStatementsContext functionBlockStatements = ctx.blockStatements();
        if (functionBlockStatements != null) {
            push();
            // recur scene
            getStack().add(functionName);
            functionBlockStatements.accept(this);
            pop();
        }
        
        return null;
    }
    
    public Set<String> getOutFunctions() {
        return outFunctions;
    }
}
