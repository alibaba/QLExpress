package com.alibaba.qlexpress4.aparser;

import com.alibaba.qlexpress4.runtime.trace.TracePointTree;
import com.alibaba.qlexpress4.runtime.trace.TraceType;
import org.antlr.v4.runtime.Token;

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

    @Override
    public TracePointTree visitExpressionStatement(QLParser.ExpressionStatementContext ctx) {
        TracePointTree expressionTrace = visitExpression(ctx.expression());
        expressionTracePoints.add(expressionTrace);
        return null;
    }

    @Override
    public TracePointTree visitExpression(QLParser.ExpressionContext ctx) {
        QLParser.TernaryExprContext ternaryExprContext = ctx.ternaryExpr();
        if (ternaryExprContext != null) {
            return visitTernaryExpr(ternaryExprContext);
        }

        return visitExpression(ctx.expression());
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
            leftChildTree = newPoint(TraceType.OPERATOR, Collections.singletonList(leftChildTree), suffixExpressContext.getStart());
        }

        // prefix
        QLParser.PrefixExpressContext prefixExpressContext = ctx.prefixExpress();
        if (prefixExpressContext != null) {
            leftChildTree = newPoint(TraceType.OPERATOR, Collections.singletonList(leftChildTree), prefixExpressContext.getStart());
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
        return newPoint(TraceType.VALUE, Collections.emptyList(), ctx.getStart());
    }

    @Override
    public TracePointTree visitMapExpr(QLParser.MapExprContext ctx) {
        return newPoint(TraceType.VALUE, Collections.emptyList(), ctx.getStart());
    }

    @Override
    public TracePointTree visitBlockExpr(QLParser.BlockExprContext ctx) {
        return newPoint(TraceType.PRIMARY, Collections.emptyList(), ctx.getStart());
    }

    @Override
    public TracePointTree visitIfExpr(QLParser.IfExprContext ctx) {
        return newPoint(TraceType.PRIMARY, Collections.emptyList(), ctx.getStart());
    }

    @Override
    public TracePointTree visitTryCatchExpr(QLParser.TryCatchExprContext ctx) {
        return newPoint(TraceType.PRIMARY, Collections.emptyList(), ctx.getStart());
    }

    @Override
    public TracePointTree visitContextSelectExpr(QLParser.ContextSelectExprContext ctx) {
        return newPoint(TraceType.PRIMARY, Collections.emptyList(), ctx.getStart());
    }

    private TracePointTree primaryBaseTrace(QLParser.PrimaryContext ctx) {
        QLParser.PrimaryNoFixContext primaryNoFixContext = ctx.primaryNoFix();
        List<QLParser.PathPartContext> pathPartContexts = ctx.pathPart();
        TracePointTree leftChildTree = null;
        int start = 0;
        if (primaryNoFixContext instanceof QLParser.VarIdExprContext && !pathPartContexts.isEmpty() &&
                pathPartContexts.get(0) instanceof QLParser.CallExprContext) {
            // function call
            QLParser.VarIdExprContext functionNameContext = (QLParser.VarIdExprContext) primaryNoFixContext;
            QLParser.CallExprContext callExprContext = (QLParser.CallExprContext) pathPartContexts.get(0);
            leftChildTree = newPoint(TraceType.FUNCTION, traceArgumentList(callExprContext.argumentList()),
                    functionNameContext.getStart());
            start = 1;
        } else {
            leftChildTree = primaryNoFixContext.accept(this);
        }

        for (int i = start; i < pathPartContexts.size(); i++) {
            QLParser.PathPartContext current = pathPartContexts.get(i);
            if (current instanceof QLParser.MethodInvokeContext ||
                    current instanceof QLParser.OptionalMethodInvokeContext ||
                    current instanceof QLParser.SpreadMethodInvokeContext) {
                QLParser.ArgumentListContext argumentList = current.getChild(QLParser.ArgumentListContext.class, 0);
                List<TracePointTree> argumentsChildren = traceArgumentList(argumentList);
                List<TracePointTree> methodChildren = new ArrayList<>(1 + argumentsChildren.size());
                methodChildren.add(leftChildTree);
                methodChildren.addAll(argumentsChildren);
                Token keyToken = current.getChild(QLParser.VarIdContext.class, 0).getStart();
                leftChildTree = newPoint(TraceType.METHOD, methodChildren, keyToken);
            } else if (current instanceof QLParser.CallExprContext) {
                QLParser.ArgumentListContext argumentList = current.getChild(QLParser.ArgumentListContext.class, 0);
                List<TracePointTree> argumentsChildren = traceArgumentList(argumentList);
                List<TracePointTree> callChildren = new ArrayList<>(1 + argumentsChildren.size());
                callChildren.add(leftChildTree);
                callChildren.addAll(argumentsChildren);
                leftChildTree = newPoint(TraceType.OPERATOR, callChildren, current.getStart());
            } else if (current instanceof QLParser.IndexExprContext) {
                QLParser.IndexValueExprContext indexValueExprContext = current.getChild(
                        QLParser.IndexValueExprContext.class, 0
                );
                List<TracePointTree> indexArgChildren = indexValueExprContext
                        .getRuleContexts(QLParser.ExpressionContext.class).stream()
                        .map(expression -> expression.accept(this))
                        .collect(Collectors.toList());
                List<TracePointTree> indexChildren = new ArrayList<>(1 + indexArgChildren.size());
                indexChildren.add(leftChildTree);
                indexChildren.addAll(indexArgChildren);
                leftChildTree = newPoint(TraceType.OPERATOR, indexChildren, current.getStart());
            } else {
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
        return argumentListContext.expression().stream()
                .map(expression -> expression.accept(this))
                .collect(Collectors.toList());
    }

    private TracePointTree newPoint(TraceType traceType, List<TracePointTree> children, Token keyToken) {
        return new TracePointTree(
                traceType, keyToken.getText(), children,
                keyToken.getLine(), keyToken.getCharPositionInLine(), keyToken.getStartIndex()
        );
    }

    private TracePointTree newPoint(TraceType traceType, List<TracePointTree> children, String text, Token keyToken) {
        return new TracePointTree(
                traceType, text, children,
                keyToken.getLine(), keyToken.getCharPositionInLine(), keyToken.getStartIndex()
        );
    }
}
