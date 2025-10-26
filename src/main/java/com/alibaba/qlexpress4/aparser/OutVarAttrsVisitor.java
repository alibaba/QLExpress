package com.alibaba.qlexpress4.aparser;

import com.alibaba.qlexpress4.utils.QLStringUtils;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class OutVarAttrsVisitor extends ScopeStackVisitor {
    
    private final Set<List<String>> outVarAttrs = new HashSet<>();
    
    private final ImportManager importManager;
    
    public OutVarAttrsVisitor(ImportManager importManager) {
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
            addAttrs(leftVarName, pathPartContexts);
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
                int restIndex = parseOutVarAttrInPath(((QLParser.VarIdExprContext)primaryNoFixPathableContext).varId(),
                    pathPartContexts);
                for (int i = restIndex; i < pathPartContexts.size(); i++) {
                    pathPartContexts.get(i).accept(this);
                }
                return null;
            }
        }
        
        return super.visitPrimary(ctx);
    }
    
    private int parseOutVarAttrInPath(QLParser.VarIdContext idContext,
        List<QLParser.PathPartContext> pathPartContexts) {
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
            return getStack().exist(primaryId) ? 0 : addAttrs(primaryId, pathPartContexts);
        }
    }
    
    private String parseFieldId(QLParser.FieldIdContext ctx) {
        TerminalNode quoteStringLiteral = ctx.QuoteStringLiteral();
        if (quoteStringLiteral != null) {
            return QLStringUtils.parseStringEscape(quoteStringLiteral.getText());
        }
        return ctx.getStart().getText();
    }
    
    private String getFieldId(QLParser.PathPartContext pathPartContext) {
        if (pathPartContext instanceof QLParser.FieldAccessContext) {
            return parseFieldId(((QLParser.FieldAccessContext)pathPartContext).fieldId());
        }
        else if (pathPartContext instanceof QLParser.OptionalFieldAccessContext) {
            return parseFieldId(((QLParser.OptionalFieldAccessContext)pathPartContext).fieldId());
        }
        else if (pathPartContext instanceof QLParser.SpreadFieldAccessContext) {
            return parseFieldId(((QLParser.SpreadFieldAccessContext)pathPartContext).fieldId());
        }
        else {
            return null;
        }
    }
    
    private int addAttrs(String primaryId, List<QLParser.PathPartContext> pathPartContexts) {
        List<String> attrs = new ArrayList<>();
        attrs.add(primaryId);
        
        int i = 0;
        for (; i < pathPartContexts.size(); i++) {
            String fieldId = getFieldId(pathPartContexts.get(i));
            if (fieldId == null) {
                break;
            }
            attrs.add(fieldId);
        }
        
        outVarAttrs.add(attrs);
        return i;
    }
    
    public Set<List<String>> getOutVarAttrs() {
        return outVarAttrs;
    }
}
