package com.alibaba.qlexpress4.aparser;

public class QLParserBaseVisitor<T> {
    
    public T visitTerminal(TerminalNode node) {
        return null;
    }
    
    public T visitChildren(RuleContext ctx) {
        T result = null;
        for (ParseTree child : ctx.children()) {
            result = child.accept(this);
        }
        return result;
    }
    
    public T visitProgram(QLParser.ProgramContext ctx) {
        return visitChildren(ctx);
    }
    
    public T visitBlockStatements(QLParser.BlockStatementsContext ctx) {
        return visitChildren(ctx);
    }
    
    public T visitLocalVariableDeclarationStatement(QLParser.LocalVariableDeclarationStatementContext ctx) {
        return visitChildren(ctx);
    }
    
    public T visitThrowStatement(QLParser.ThrowStatementContext ctx) {
        return visitChildren(ctx);
    }
    
    public T visitWhileStatement(QLParser.WhileStatementContext ctx) {
        return visitChildren(ctx);
    }
    
    public T visitTraditionalForStatement(QLParser.TraditionalForStatementContext ctx) {
        return visitChildren(ctx);
    }
    
    public T visitForEachStatement(QLParser.ForEachStatementContext ctx) {
        return visitChildren(ctx);
    }
    
    public T visitFunctionStatement(QLParser.FunctionStatementContext ctx) {
        return visitChildren(ctx);
    }
    
    public T visitMacroStatement(QLParser.MacroStatementContext ctx) {
        return visitChildren(ctx);
    }
    
    public T visitBreakContinueStatement(QLParser.BreakContinueStatementContext ctx) {
        return visitChildren(ctx);
    }
    
    public T visitReturnStatement(QLParser.ReturnStatementContext ctx) {
        return visitChildren(ctx);
    }
    
    public T visitEmptyStatement(QLParser.EmptyStatementContext ctx) {
        return visitChildren(ctx);
    }
    
    public T visitExpressionStatement(QLParser.ExpressionStatementContext ctx) {
        return visitChildren(ctx);
    }
    
    public T visitLocalVariableDeclaration(QLParser.LocalVariableDeclarationContext ctx) {
        return visitChildren(ctx);
    }
    
    public T visitForInit(QLParser.ForInitContext ctx) {
        return visitChildren(ctx);
    }
    
    public T visitVariableDeclaratorList(QLParser.VariableDeclaratorListContext ctx) {
        return visitChildren(ctx);
    }
    
    public T visitVariableDeclarator(QLParser.VariableDeclaratorContext ctx) {
        return visitChildren(ctx);
    }
    
    public T visitVariableDeclaratorId(QLParser.VariableDeclaratorIdContext ctx) {
        return visitChildren(ctx);
    }
    
    public T visitVariableInitializer(QLParser.VariableInitializerContext ctx) {
        return visitChildren(ctx);
    }
    
    public T visitArrayInitializer(QLParser.ArrayInitializerContext ctx) {
        return visitChildren(ctx);
    }
    
    public T visitVariableInitializerList(QLParser.VariableInitializerListContext ctx) {
        return visitChildren(ctx);
    }
    
    public T visitDeclType(QLParser.DeclTypeContext ctx) {
        return visitChildren(ctx);
    }
    
    public T visitDeclTypeNoArr(QLParser.DeclTypeNoArrContext ctx) {
        return visitChildren(ctx);
    }
    
    public T visitPrimitiveType(QLParser.PrimitiveTypeContext ctx) {
        return visitChildren(ctx);
    }
    
    public T visitClsType(QLParser.ClsTypeContext ctx) {
        return visitChildren(ctx);
    }
    
    public T visitDims(QLParser.DimsContext ctx) {
        return visitChildren(ctx);
    }
    
    public T visitDimExprs(QLParser.DimExprsContext ctx) {
        return visitChildren(ctx);
    }
    
    public T visitExpression(QLParser.ExpressionContext ctx) {
        return visitChildren(ctx);
    }
    
    public T visitLeftHandSide(QLParser.LeftHandSideContext ctx) {
        return visitChildren(ctx);
    }
    
    public T visitAssignOperator(QLParser.AssignOperatorContext ctx) {
        return visitChildren(ctx);
    }
    
    public T visitTernaryExpr(QLParser.TernaryExprContext ctx) {
        return visitChildren(ctx);
    }
    
    public T visitBaseExpr(QLParser.BaseExprContext ctx) {
        return visitChildren(ctx);
    }
    
    public T visitLeftAsso(QLParser.LeftAssoContext ctx) {
        return visitChildren(ctx);
    }
    
    public T visitBinaryop(QLParser.BinaryopContext ctx) {
        return visitChildren(ctx);
    }
    
    public T visitPrimary(QLParser.PrimaryContext ctx) {
        return visitChildren(ctx);
    }
    
    public T visitPrefixExpress(QLParser.PrefixExpressContext ctx) {
        return visitChildren(ctx);
    }
    
    public T visitSuffixExpress(QLParser.SuffixExpressContext ctx) {
        return visitChildren(ctx);
    }
    
    public T visitConstExpr(QLParser.ConstExprContext ctx) {
        return visitChildren(ctx);
    }
    
    public T visitCastExpr(QLParser.CastExprContext ctx) {
        return visitChildren(ctx);
    }
    
    public T visitGroupExpr(QLParser.GroupExprContext ctx) {
        return visitChildren(ctx);
    }
    
    public T visitNewObjExpr(QLParser.NewObjExprContext ctx) {
        return visitChildren(ctx);
    }
    
    public T visitNewEmptyArrExpr(QLParser.NewEmptyArrExprContext ctx) {
        return visitChildren(ctx);
    }
    
    public T visitNewInitArrExpr(QLParser.NewInitArrExprContext ctx) {
        return visitChildren(ctx);
    }
    
    public T visitVarIdExpr(QLParser.VarIdExprContext ctx) {
        return visitChildren(ctx);
    }
    
    public T visitTypeExpr(QLParser.TypeExprContext ctx) {
        return visitChildren(ctx);
    }
    
    public T visitListExpr(QLParser.ListExprContext ctx) {
        return visitChildren(ctx);
    }
    
    public T visitMapExpr(QLParser.MapExprContext ctx) {
        return visitChildren(ctx);
    }
    
    public T visitBlockExpr(QLParser.BlockExprContext ctx) {
        return visitChildren(ctx);
    }
    
    public T visitContextSelectExpr(QLParser.ContextSelectExprContext ctx) {
        return visitChildren(ctx);
    }
    
    public T visitQlIf(QLParser.QlIfContext ctx) {
        return visitChildren(ctx);
    }
    
    public T visitThenBody(QLParser.ThenBodyContext ctx) {
        return visitChildren(ctx);
    }
    
    public T visitElseBody(QLParser.ElseBodyContext ctx) {
        return visitChildren(ctx);
    }
    
    public T visitNonExpressionStatement(QLParser.NonExpressionStatementContext ctx) {
        return visitChildren(ctx);
    }
    
    public T visitListItems(QLParser.ListItemsContext ctx) {
        return visitChildren(ctx);
    }
    
    public T visitTryCatches(QLParser.TryCatchesContext ctx) {
        return visitChildren(ctx);
    }
    
    public T visitTryCatch(QLParser.TryCatchContext ctx) {
        return visitChildren(ctx);
    }
    
    public T visitCatchParams(QLParser.CatchParamsContext ctx) {
        return visitChildren(ctx);
    }
    
    public T visitTryFinally(QLParser.TryFinallyContext ctx) {
        return visitChildren(ctx);
    }
    
    public T visitMapEntries(QLParser.MapEntriesContext ctx) {
        return visitChildren(ctx);
    }
    
    public T visitMapEntry(QLParser.MapEntryContext ctx) {
        return visitChildren(ctx);
    }
    
    public T visitClsValue(QLParser.ClsValueContext ctx) {
        return visitChildren(ctx);
    }
    
    public T visitEValue(QLParser.EValueContext ctx) {
        return visitChildren(ctx);
    }
    
    public T visitIdKey(QLParser.IdKeyContext ctx) {
        return visitChildren(ctx);
    }
    
    public T visitStringKey(QLParser.StringKeyContext ctx) {
        return visitChildren(ctx);
    }
    
    public T visitQuoteStringKey(QLParser.QuoteStringKeyContext ctx) {
        return visitChildren(ctx);
    }
    
    public T visitMethodInvoke(QLParser.MethodInvokeContext ctx) {
        return visitChildren(ctx);
    }
    
    public T visitOptionalMethodInvoke(QLParser.OptionalMethodInvokeContext ctx) {
        return visitChildren(ctx);
    }
    
    public T visitSpreadMethodInvoke(QLParser.SpreadMethodInvokeContext ctx) {
        return visitChildren(ctx);
    }
    
    public T visitFieldAccess(QLParser.FieldAccessContext ctx) {
        return visitChildren(ctx);
    }
    
    public T visitOptionalFieldAccess(QLParser.OptionalFieldAccessContext ctx) {
        return visitChildren(ctx);
    }
    
    public T visitSpreadFieldAccess(QLParser.SpreadFieldAccessContext ctx) {
        return visitChildren(ctx);
    }
    
    public T visitMethodAccess(QLParser.MethodAccessContext ctx) {
        return visitChildren(ctx);
    }
    
    public T visitIndexExpr(QLParser.IndexExprContext ctx) {
        return visitChildren(ctx);
    }
    
    public T visitCustomPath(QLParser.CustomPathContext ctx) {
        return visitChildren(ctx);
    }
    
    public T visitFieldId(QLParser.FieldIdContext ctx) {
        return visitChildren(ctx);
    }
    
    public T visitSingleIndex(QLParser.SingleIndexContext ctx) {
        return visitChildren(ctx);
    }
    
    public T visitSliceIndex(QLParser.SliceIndexContext ctx) {
        return visitChildren(ctx);
    }
    
    public T visitArgumentList(QLParser.ArgumentListContext ctx) {
        return visitChildren(ctx);
    }
    
    public T visitLiteral(QLParser.LiteralContext ctx) {
        return visitChildren(ctx);
    }
    
    public T visitDoubleQuoteStringLiteral(QLParser.DoubleQuoteStringLiteralContext ctx) {
        return visitChildren(ctx);
    }
    
    public T visitStringExpression(QLParser.StringExpressionContext ctx) {
        return visitChildren(ctx);
    }
    
    public T visitBoolenLiteral(QLParser.BoolenLiteralContext ctx) {
        return visitChildren(ctx);
    }
    
    public T visitLambdaExpr(QLParser.LambdaExprContext ctx) {
        return visitChildren(ctx);
    }
    
    public T visitLambdaParameters(QLParser.LambdaParametersContext ctx) {
        return visitChildren(ctx);
    }
    
    public T visitFormalOrInferredParameterList(QLParser.FormalOrInferredParameterListContext ctx) {
        return visitChildren(ctx);
    }
    
    public T visitFormalOrInferredParameter(QLParser.FormalOrInferredParameterContext ctx) {
        return visitChildren(ctx);
    }
    
    public T visitImportCls(QLParser.ImportClsContext ctx) {
        return visitChildren(ctx);
    }
    
    public T visitImportPack(QLParser.ImportPackContext ctx) {
        return visitChildren(ctx);
    }
    
    public T visitOpId(QLParser.OpIdContext ctx) {
        return visitChildren(ctx);
    }
    
    public T visitVarId(QLParser.VarIdContext ctx) {
        return visitChildren(ctx);
    }
    
    public T visitSwitchExpr(QLParser.SwitchExprContext ctx) {
        return visitChildren(ctx);
    }
    
    public T visitSwitchCaseGroups(QLParser.SwitchCaseGroupsContext ctx) {
        return visitChildren(ctx);
    }
    
    public T visitSwitchStatementGroup(QLParser.SwitchStatementGroupContext ctx) {
        return visitChildren(ctx);
    }
    
    public T visitSwitchExprGroup(QLParser.SwitchExprGroupContext ctx) {
        return visitChildren(ctx);
    }
    
    public T visitSwitchLabels(QLParser.SwitchLabelsContext ctx) {
        return visitChildren(ctx);
    }
    
    public T visitSwitchLabel(QLParser.SwitchLabelContext ctx) {
        return visitChildren(ctx);
    }
    
    public T visitSwitchExpressionLabel(QLParser.SwitchExpressionLabelContext ctx) {
        return visitChildren(ctx);
    }
    
    public T visitExpressionList(QLParser.ExpressionListContext ctx) {
        return visitChildren(ctx);
    }
    
    public T visitTryCatchExpr(QLParser.TryCatchExprContext ctx) {
        return visitChildren(ctx);
    }
}
