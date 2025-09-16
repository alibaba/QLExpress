package com.alibaba.qlexpress4.aparser;

import com.alibaba.qlexpress4.utils.QLStringUtils;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Author: DQinYuan
 */
public class OutVarNamesVisitor extends QLParserBaseVisitor<Void> {
    
    private final Set<String> outVars = new HashSet<>();
    
    private final ImportManager importManager;
    
    private ExistVarStack existVarStack = new ExistVarStack(null);
    
    public OutVarNamesVisitor(ImportManager importManager) {
        this.importManager = importManager;
    }
    
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
        
        QLParser.ElseBodyContext elseBodyContext = qlIfContext.elseBody();
        if (elseBodyContext != null) {
            this.existVarStack = this.existVarStack.push();
            elseBodyContext.accept(this);
            this.existVarStack = this.existVarStack.pop();
        }
        
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
    
    // handle import
    
    @Override
    public Void visitImportCls(QLParser.ImportClsContext ctx) {
        String importClsPath = ctx.varId()
            .stream()
            .map(QLParser.VarIdContext::getStart)
            .map(Token::getText)
            .collect(Collectors.joining("."));
        importManager.addImport(ImportManager.importCls(importClsPath));
        return null;
    }
    
    @Override
    public Void visitImportPack(QLParser.ImportPackContext ctx) {
        List<QLParser.VarIdContext> importPackPathTokens = ctx.varId();
        boolean isInnerCls =
            !Character.isLowerCase(importPackPathTokens.get(importPackPathTokens.size() - 1).getText().charAt(0));
        String importPath = importPackPathTokens.stream()
            .map(QLParser.VarIdContext::getStart)
            .map(Token::getText)
            .collect(Collectors.joining("."));
        importManager
            .addImport(isInnerCls ? ImportManager.importInnerCls(importPath) : ImportManager.importPack(importPath));
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
        }
        else if (!existVarStack.exist(leftVarName)) {
            outVars.add(leftVarName);
        }
        return null;
    }
    
    // selector
    
    @Override
    public Void visitContextSelectExpr(QLParser.ContextSelectExprContext ctx) {
        String variableName = ctx.SelectorVariable_VANME().getText().trim();
        if (!existVarStack.exist(variableName)) {
            outVars.add(variableName);
        }
        return null;
    }
    
    // exclude function call
    
    @Override
    public Void visitPrimary(QLParser.PrimaryContext ctx) {
        QLParser.PrimaryNoFixPathableContext primaryNoFixPathableContext = ctx.primaryNoFixPathable();
        if (primaryNoFixPathableContext != null) {
            List<QLParser.PathPartContext> pathPartContexts = ctx.pathPart();
            if (primaryNoFixPathableContext instanceof QLParser.VarIdExprContext && !pathPartContexts.isEmpty()
                && pathPartContexts.get(0) instanceof QLParser.CallExprContext) {
                // function call
                for (QLParser.PathPartContext pathPartContext : pathPartContexts) {
                    pathPartContext.accept(this);
                }
                return null;
            }
            if (primaryNoFixPathableContext instanceof QLParser.VarIdExprContext) {
                int restIndex = parseVarIdInPath(((QLParser.VarIdExprContext)primaryNoFixPathableContext).varId(),
                    pathPartContexts);
                for (int i = restIndex; i < pathPartContexts.size(); i++) {
                    pathPartContexts.get(i).accept(this);
                }
                return null;
            }
        }
        
        return super.visitPrimary(ctx);
    }
    
    private int parseVarIdInPath(QLParser.VarIdContext idContext, List<QLParser.PathPartContext> pathPartContexts) {
        List<String> headPartIds = new ArrayList<>();
        String primaryId = idContext.getText();
        headPartIds.add(primaryId);
        for (QLParser.PathPartContext pathPartContext : pathPartContexts) {
            if (pathPartContext instanceof QLParser.FieldAccessContext) {
                headPartIds.add(parseFieldId(((QLParser.FieldAccessContext)pathPartContext).fieldId()));
            }
            else {
                break;
            }
        }
        ImportManager.LoadPartQualifiedResult loadPartQualifiedResult = importManager.loadPartQualified(headPartIds);
        if (loadPartQualifiedResult.getCls() != null) {
            return loadPartQualifiedResult.getRestIndex() - 1;
        }
        else {
            if (!existVarStack.exist(primaryId)) {
                outVars.add(primaryId);
            }
            return 0;
        }
    }
    
    private String parseFieldId(QLParser.FieldIdContext ctx) {
        TerminalNode quoteStringLiteral = ctx.QuoteStringLiteral();
        if (quoteStringLiteral != null) {
            return QLStringUtils.parseStringEscape(quoteStringLiteral.getText());
        }
        return ctx.getStart().getText();
    }
    
    private boolean isClassName(String id) {
        char first = id.charAt(0);
        return first >= 'A' && first <= 'Z';
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
