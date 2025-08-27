package com.alibaba.qlexpress4.aparser;

import com.alibaba.qlexpress4.aparser.QLParser.EmptyStatementContext;
import com.alibaba.qlexpress4.aparser.QLParser.ExpressionContext;
import com.alibaba.qlexpress4.aparser.QLParser.LocalVariableDeclarationStatementContext;
import com.alibaba.qlexpress4.aparser.QLParser.PathPartContext;
import com.alibaba.qlexpress4.aparser.QLParser.VarIdContext;
import com.alibaba.qlexpress4.runtime.trace.TracePointTree;
import com.alibaba.qlexpress4.runtime.trace.TraceType;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class TraceExpressionVisitor extends QLParserBaseVisitor<TracePointTree> {
    
    private final List<TracePointTree> expressionTracePoints = new ArrayList<>();
    
    public List<TracePointTree> getExpressionTracePoints() {
        return expressionTracePoints;
    }
    
    // ==================== Statement ====================
    
    @Override
    public TracePointTree visitThrowStatement(QLParser.ThrowStatementContext ctx) {
        TracePointTree tracePoint = newPoint(TraceType.STATEMENT, Collections.emptyList(), ctx.getStart());
        expressionTracePoints.add(tracePoint);
        return null;
    }
    
    @Override
    public TracePointTree visitLocalVariableDeclarationStatement(LocalVariableDeclarationStatementContext ctx) {
        TracePointTree tracePoint = newPoint(TraceType.STATEMENT, Collections.emptyList(), ctx.getStart());
        expressionTracePoints.add(tracePoint);
        return null;
    }
    
    @Override
    public TracePointTree visitExpressionStatement(QLParser.ExpressionStatementContext ctx) {
        TracePointTree expressionTrace = visitExpression(ctx.expression());
        expressionTracePoints.add(expressionTrace);
        return null;
    }
    
    @Override
    public TracePointTree visitWhileStatement(QLParser.WhileStatementContext ctx) {
        TracePointTree tracePoint = newPoint(TraceType.STATEMENT, Collections.emptyList(), ctx.getStart());
        expressionTracePoints.add(tracePoint);
        return null;
    }
    
    @Override
    public TracePointTree visitTraditionalForStatement(QLParser.TraditionalForStatementContext ctx) {
        TracePointTree tracePoint = newPoint(TraceType.STATEMENT, Collections.emptyList(), ctx.getStart());
        expressionTracePoints.add(tracePoint);
        return null;
    }
    
    @Override
    public TracePointTree visitForEachStatement(QLParser.ForEachStatementContext ctx) {
        TracePointTree tracePoint = newPoint(TraceType.STATEMENT, Collections.emptyList(), ctx.getStart());
        expressionTracePoints.add(tracePoint);
        return null;
    }
    
    @Override
    public TracePointTree visitFunctionStatement(QLParser.FunctionStatementContext ctx) {
        TracePointTree tracePoint =
            newPoint(TraceType.DEFINE_FUNCTION, Collections.emptyList(), ctx.varId().getStart());
        expressionTracePoints.add(tracePoint);
        return null;
    }
    
    @Override
    public TracePointTree visitMacroStatement(QLParser.MacroStatementContext ctx) {
        TracePointTree tracePoint = newPoint(TraceType.DEFINE_MACRO, Collections.emptyList(), ctx.varId().getStart());
        expressionTracePoints.add(tracePoint);
        return null;
    }
    
    @Override
    public TracePointTree visitBreakContinueStatement(QLParser.BreakContinueStatementContext ctx) {
        TracePointTree tracePoint = newPoint(TraceType.STATEMENT, Collections.emptyList(), ctx.getStart());
        expressionTracePoints.add(tracePoint);
        return null;
    }
    
    @Override
    public TracePointTree visitReturnStatement(QLParser.ReturnStatementContext ctx) {
        ExpressionContext returnExpressionContext = ctx.expression();
        if (returnExpressionContext != null) {
            TracePointTree returnExpressionTrace = returnExpressionContext.accept(this);
            TracePointTree tracePoint =
                newPoint(TraceType.RETURN, Collections.singletonList(returnExpressionTrace), ctx.getStart());
            expressionTracePoints.add(tracePoint);
        }
        else {
            TracePointTree tracePoint = newPoint(TraceType.RETURN, Collections.emptyList(), ctx.getStart());
            expressionTracePoints.add(tracePoint);
        }
        return null;
    }
    
    @Override
    public TracePointTree visitEmptyStatement(QLParser.EmptyStatementContext ctx) {
        expressionTracePoints.add(newPoint(TraceType.STATEMENT, Collections.emptyList(), ctx.getStart()));
        return null;
    }
    
    @Override
    public TracePointTree visitBlockStatements(QLParser.BlockStatementsContext ctx) {
        List<QLParser.BlockStatementContext> emptyChildren = ctx.blockStatement()
            .stream()
            .filter(bs -> bs instanceof EmptyStatementContext)
            .collect(Collectors.toList());
        if (emptyChildren.size() == ctx.blockStatement().size()) {
            // all emtpty
            emptyChildren.get(0).accept(this);
            return null;
        }
        
        for (QLParser.BlockStatementContext blockStatementContext : ctx.blockStatement()) {
            if (!(blockStatementContext instanceof EmptyStatementContext)) {
                blockStatementContext.accept(this);
            }
        }
        return null;
    }
    
    // ==================== Expression ====================
    
    @Override
    public TracePointTree visitExpression(QLParser.ExpressionContext ctx) {
        QLParser.TernaryExprContext ternaryExprContext = ctx.ternaryExpr();
        if (ternaryExprContext != null) {
            return visitTernaryExpr(ternaryExprContext);
        }
        
        TracePointTree leftChildTree = visitLeftHandSide(ctx.leftHandSide());
        TracePointTree rightChildTree = visitExpression(ctx.expression());
        return newPoint(TraceType.OPERATOR,
            Arrays.asList(leftChildTree, rightChildTree),
            ctx.assignOperator().getStart());
    }
    
    @Override
    public TracePointTree visitLeftHandSide(QLParser.LeftHandSideContext ctx) {
        VarIdContext varIdContext = ctx.varId();
        ;
        List<PathPartContext> pathParts = ctx.pathPart();
        TracePointTree leftChildTree = null;
        int start = 0;
        if (!pathParts.isEmpty() && pathParts.get(0) instanceof QLParser.CallExprContext) {
            QLParser.CallExprContext callExprContext = (QLParser.CallExprContext)pathParts.get(0);
            leftChildTree = newPoint(TraceType.FUNCTION,
                traceArgumentList(callExprContext.argumentList()),
                varIdContext.getStart());
            start = 1;
        }
        else {
            leftChildTree = newPoint(TraceType.VARIABLE, Collections.emptyList(), ctx.getStart());
        }
        return pathParts(leftChildTree, pathParts.subList(start, pathParts.size()));
    }
    
    @Override
    public TracePointTree visitTernaryExpr(QLParser.TernaryExprContext ctx) {
        if (ctx.thenExpr == null) {
            return ctx.condition.accept(this);
        }
        
        TracePointTree conditionPoint = ctx.condition.accept(this);
        TracePointTree thenPoint = ctx.thenExpr.accept(this);
        TracePointTree elsePoint = ctx.elseExpr.accept(this);
        
        Token keyToken = ctx.QUESTION().getSymbol();
        return newPoint(TraceType.OPERATOR, Arrays.asList(conditionPoint, thenPoint, elsePoint), keyToken);
    }
    
    @Override
    public TracePointTree visitBaseExpr(QLParser.BaseExprContext ctx) {
        TracePointTree leftChildTree = visitPrimary(ctx.primary());
        for (QLParser.LeftAssoContext leftAssoContext : ctx.leftAsso()) {
            Token keyToken = leftAssoContext.binaryop().getStart();
            TracePointTree rightChildTree = visitBaseExpr(leftAssoContext.baseExpr());
            leftChildTree = newPoint(TraceType.OPERATOR, Arrays.asList(leftChildTree, rightChildTree), keyToken);
        }
        return leftChildTree;
    }
    
    @Override
    public TracePointTree visitPrimary(QLParser.PrimaryContext ctx) {
        TracePointTree leftChildTree = primaryBaseTrace(ctx);
        
        // suffix
        QLParser.SuffixExpressContext suffixExpressContext = ctx.suffixExpress();
        if (suffixExpressContext != null) {
            leftChildTree =
                newPoint(TraceType.OPERATOR, Collections.singletonList(leftChildTree), suffixExpressContext.getStart());
        }
        
        // prefix
        QLParser.PrefixExpressContext prefixExpressContext = ctx.prefixExpress();
        if (prefixExpressContext != null) {
            leftChildTree =
                newPoint(TraceType.OPERATOR, Collections.singletonList(leftChildTree), prefixExpressContext.getStart());
        }
        
        return leftChildTree;
    }
    
    @Override
    public TracePointTree visitConstExpr(QLParser.ConstExprContext ctx) {
        return newPoint(TraceType.VALUE, Collections.emptyList(), ctx.getText(), ctx.getStart());
    }
    
    @Override
    public TracePointTree visitCastExpr(QLParser.CastExprContext ctx) {
        return ctx.primary().accept(this);
    }
    
    @Override
    public TracePointTree visitGroupExpr(QLParser.GroupExprContext ctx) {
        return ctx.expression().accept(this);
    }
    
    @Override
    public TracePointTree visitNewObjExpr(QLParser.NewObjExprContext ctx) {
        return newPoint(TraceType.PRIMARY, Collections.emptyList(), ctx.getText(), ctx.getStart());
    }
    
    @Override
    public TracePointTree visitNewEmptyArrExpr(QLParser.NewEmptyArrExprContext ctx) {
        return newPoint(TraceType.PRIMARY, Collections.emptyList(), ctx.getText(), ctx.getStart());
    }
    
    @Override
    public TracePointTree visitNewInitArrExpr(QLParser.NewInitArrExprContext ctx) {
        return newPoint(TraceType.PRIMARY, Collections.emptyList(), ctx.getText(), ctx.getStart());
    }
    
    @Override
    public TracePointTree visitLambdaExpr(QLParser.LambdaExprContext ctx) {
        return newPoint(TraceType.PRIMARY, Collections.emptyList(), ctx.ARROW().getSymbol());
    }
    
    @Override
    public TracePointTree visitVarIdExpr(QLParser.VarIdExprContext ctx) {
        return newPoint(TraceType.VARIABLE, Collections.emptyList(), ctx.getStart());
    }
    
    @Override
    public TracePointTree visitTypeExpr(QLParser.TypeExprContext ctx) {
        return newPoint(TraceType.VALUE, Collections.emptyList(), ctx.getStart());
    }
    
    @Override
    public TracePointTree visitListExpr(QLParser.ListExprContext ctx) {
        QLParser.ListItemsContext listItemsContext = ctx.listItems();
        if (listItemsContext == null) {
            return newPoint(TraceType.LIST, Collections.emptyList(), ctx.getStart());
        }
        List<TracePointTree> children = listItemsContext.expression()
            .stream()
            .map(expression -> expression.accept(this))
            .collect(Collectors.toList());
        return newPoint(TraceType.LIST, children, ctx.getStart());
    }
    
    @Override
    public TracePointTree visitMapExpr(QLParser.MapExprContext ctx) {
        return newPoint(TraceType.MAP, Collections.emptyList(), ctx.getStart());
    }
    
    @Override
    public TracePointTree visitBlockExpr(QLParser.BlockExprContext ctx) {
        TraceExpressionVisitor traceExpressionVisitor = new TraceExpressionVisitor();
        ctx.blockStatements().accept(traceExpressionVisitor);
        List<TracePointTree> children = traceExpressionVisitor.getExpressionTracePoints();
        return newPoint(TraceType.BLOCK, children, ctx.getStart());
    }
    
    @Override
    public TracePointTree visitQlIf(QLParser.QlIfContext ctx) {
        List<TracePointTree> children = new ArrayList<>(3);
        children.add(ctx.condition.accept(this));
        // thenBody
        children.add(visitThenBody(ctx.thenBody()));
        // elseBody
        if (ctx.elseBody() != null) {
            children.add(visitElseBody(ctx.elseBody()));
        }
        return newPoint(TraceType.IF, children, "if", ctx.getStart());
    }
    
    @Override
    public TracePointTree visitThenBody(QLParser.ThenBodyContext thenBody) {
        if (thenBody.blockStatements() != null) {
            TraceExpressionVisitor blockVisitor = new TraceExpressionVisitor();
            thenBody.blockStatements().accept(blockVisitor);
            return newPoint(TraceType.BLOCK, blockVisitor.getExpressionTracePoints(), thenBody.getStart());
        }
        else if (thenBody.expression() != null) {
            return thenBody.expression().accept(this);
        }
        else if (thenBody.blockStatement() != null) {
            TraceExpressionVisitor blockVisitor = new TraceExpressionVisitor();
            thenBody.blockStatement().accept(blockVisitor);
            return blockVisitor.getExpressionTracePoints().get(0);
        }
        return newPoint(TraceType.BLOCK, Collections.emptyList(), thenBody.getStart());
    }
    
    @Override
    public TracePointTree visitElseBody(QLParser.ElseBodyContext elseBody) {
        if (elseBody.blockStatements() != null) {
            TraceExpressionVisitor blockVisitor = new TraceExpressionVisitor();
            elseBody.blockStatements().accept(blockVisitor);
            return newPoint(TraceType.BLOCK, blockVisitor.getExpressionTracePoints(), elseBody.getStart());
        }
        else if (elseBody.expression() != null) {
            return elseBody.expression().accept(this);
        }
        else if (elseBody.blockStatement() != null) {
            TraceExpressionVisitor blockVisitor = new TraceExpressionVisitor();
            elseBody.blockStatement().accept(blockVisitor);
            return blockVisitor.getExpressionTracePoints().get(0);
        }
        else if (elseBody.qlIf() != null) {
            return elseBody.qlIf().accept(this);
        }
        return newPoint(TraceType.BLOCK, Collections.emptyList(), elseBody.getStart());
    }
    
    @Override
    public TracePointTree visitTryCatchExpr(QLParser.TryCatchExprContext ctx) {
        return newPoint(TraceType.PRIMARY, Collections.emptyList(), ctx.getStart());
    }
    
    @Override
    public TracePointTree visitContextSelectExpr(QLParser.ContextSelectExprContext ctx) {
        return newPoint(TraceType.PRIMARY, Collections.emptyList(), ctx.getStart());
    }
    
    // ==================== Private Helper ====================
    
    private TracePointTree primaryBaseTrace(QLParser.PrimaryContext ctx) {
        QLParser.PrimaryNoFixNonPathableContext primaryNoFixNonPathableContext = ctx.primaryNoFixNonPathable();
        if (primaryNoFixNonPathableContext != null) {
            return primaryNoFixNonPathableContext.accept(this);
        }
        
        QLParser.PrimaryNoFixPathableContext primaryNoFixPathableContext = ctx.primaryNoFixPathable();
        List<QLParser.PathPartContext> pathPartContexts = ctx.pathPart();
        TracePointTree leftChildTree = null;
        int start = 0;
        if (primaryNoFixPathableContext instanceof QLParser.VarIdExprContext && !pathPartContexts.isEmpty()
            && pathPartContexts.get(0) instanceof QLParser.CallExprContext) {
            // function call
            QLParser.VarIdExprContext functionNameContext = (QLParser.VarIdExprContext)primaryNoFixPathableContext;
            QLParser.CallExprContext callExprContext = (QLParser.CallExprContext)pathPartContexts.get(0);
            leftChildTree = newPoint(TraceType.FUNCTION,
                traceArgumentList(callExprContext.argumentList()),
                functionNameContext.getStart());
            start = 1;
        }
        else {
            leftChildTree = primaryNoFixPathableContext.accept(this);
        }
        
        return pathParts(leftChildTree, pathPartContexts.subList(start, pathPartContexts.size()));
    }
    
    private TracePointTree pathParts(TracePointTree pathRoot, List<QLParser.PathPartContext> pathPartContexts) {
        TracePointTree leftChildTree = pathRoot;
        for (QLParser.PathPartContext current : pathPartContexts) {
            if (current instanceof QLParser.MethodInvokeContext
                || current instanceof QLParser.OptionalMethodInvokeContext
                || current instanceof QLParser.SpreadMethodInvokeContext) {
                QLParser.ArgumentListContext argumentList = current.getChild(QLParser.ArgumentListContext.class, 0);
                List<TracePointTree> argumentsChildren = traceArgumentList(argumentList);
                List<TracePointTree> methodChildren = new ArrayList<>(1 + argumentsChildren.size());
                methodChildren.add(leftChildTree);
                methodChildren.addAll(argumentsChildren);
                Token keyToken = current.getChild(QLParser.VarIdContext.class, 0).getStart();
                leftChildTree = newPoint(TraceType.METHOD, methodChildren, keyToken);
            }
            else if (current instanceof QLParser.CallExprContext) {
                QLParser.ArgumentListContext argumentList = current.getChild(QLParser.ArgumentListContext.class, 0);
                List<TracePointTree> argumentsChildren = traceArgumentList(argumentList);
                List<TracePointTree> callChildren = new ArrayList<>(1 + argumentsChildren.size());
                callChildren.add(leftChildTree);
                callChildren.addAll(argumentsChildren);
                leftChildTree = newPoint(TraceType.OPERATOR, callChildren, current.getStart());
            }
            else if (current instanceof QLParser.IndexExprContext) {
                QLParser.IndexValueExprContext indexValueExprContext =
                    current.getChild(QLParser.IndexValueExprContext.class, 0);
                List<TracePointTree> indexArgChildren =
                    indexValueExprContext.getRuleContexts(QLParser.ExpressionContext.class)
                        .stream()
                        .map(expression -> expression.accept(this))
                        .collect(Collectors.toList());
                List<TracePointTree> indexChildren = new ArrayList<>(1 + indexArgChildren.size());
                indexChildren.add(leftChildTree);
                indexChildren.addAll(indexArgChildren);
                leftChildTree = newPoint(TraceType.OPERATOR, indexChildren, current.getStart());
            }
            else {
                // field
                leftChildTree = newPoint(TraceType.FIELD, Collections.singletonList(leftChildTree), current.getStop());
            }
        }
        return leftChildTree;
    }
    
    private List<TracePointTree> traceArgumentList(QLParser.ArgumentListContext argumentListContext) {
        if (argumentListContext == null || argumentListContext.isEmpty()) {
            return Collections.emptyList();
        }
        return argumentListContext.expression()
            .stream()
            .map(expression -> expression.accept(this))
            .collect(Collectors.toList());
    }
    
    private TracePointTree newPoint(TraceType traceType, List<TracePointTree> children, Token keyToken) {
        return new TracePointTree(traceType, keyToken.getText(), children, keyToken.getLine(),
            keyToken.getCharPositionInLine(), keyToken.getStartIndex());
    }
    
    private TracePointTree newPoint(TraceType traceType, List<TracePointTree> children, String text, Token keyToken) {
        return new TracePointTree(traceType, text, children, keyToken.getLine(), keyToken.getCharPositionInLine(),
            keyToken.getStartIndex());
    }
}
