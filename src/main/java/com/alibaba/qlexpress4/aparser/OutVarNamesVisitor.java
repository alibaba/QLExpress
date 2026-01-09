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
public class OutVarNamesVisitor extends ScopeStackVisitor {
    
    private final Set<String> outVars = new HashSet<>();
    
    private final ImportManager importManager;
    
    public OutVarNamesVisitor(ImportManager importManager) {
        super(new ExistVarStack(null));
        this.importManager = importManager;
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
        getStack().add(varIdContext.getText());
        return null;
    }
    
    @Override
    public Void visitLeftHandSide(QLParser.LeftHandSideContext ctx) {
        List<QLParser.PathPartContext> pathPartContexts = ctx.pathPart();
        String leftVarName = ctx.varId().getText();
        if (pathPartContexts.isEmpty()) {
            getStack().add(leftVarName);
        }
        else if (!getStack().exist(leftVarName)) {
            outVars.add(leftVarName);
        }
        return null;
    }
    
    // selector
    
    @Override
    public Void visitContextSelectExpr(QLParser.ContextSelectExprContext ctx) {
        String variableName = ctx.SelectorVariable_VANME().getText().trim();
        if (!getStack().exist(variableName)) {
            outVars.add(variableName);
        }
        return null;
    }
    
    // exclude function call
    
    @Override
    public Void visitPrimary(QLParser.PrimaryContext ctx) {
        QLParser.PrimaryNoFixPathableContext primaryNoFixPathableContext = ctx.primaryNoFixPathable();
        if (primaryNoFixPathableContext != null) {
            if (primaryNoFixPathableContext instanceof QLParser.VarIdExprContext) {
                List<QLParser.PathPartContext> pathPartContexts = ctx.pathPart();
                int restIndex =
                    parseVarIdInPath((QLParser.VarIdExprContext)primaryNoFixPathableContext, pathPartContexts);
                for (int i = restIndex; i < pathPartContexts.size(); i++) {
                    pathPartContexts.get(i).accept(this);
                }
                return null;
            }
        }
        
        return super.visitPrimary(ctx);
    }
    
    private int parseVarIdInPath(QLParser.VarIdExprContext idContext, List<QLParser.PathPartContext> pathPartContexts) {
        if (idContext.LPAREN() != null) {
            if (idContext.argumentList() != null) {
                idContext.argumentList().accept(this);
            }
            return 0;
        }
        
        List<String> headPartIds = new ArrayList<>();
        String primaryId = idContext.varId().getText();
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
            if (!getStack().exist(primaryId)) {
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
    
    // collect out variables name
    
    @Override
    public Void visitVarIdExpr(QLParser.VarIdExprContext ctx) {
        String varName = ctx.varId().getText();
        if (!getStack().exist(varName)) {
            outVars.add(varName);
        }
        return null;
    }
    
    public Set<String> getOutVars() {
        return outVars;
    }
}
