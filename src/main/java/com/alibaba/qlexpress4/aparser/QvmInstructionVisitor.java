package com.alibaba.qlexpress4.aparser;

import com.alibaba.qlexpress4.DefaultClassSupplier;
import com.alibaba.qlexpress4.InitOptions;
import com.alibaba.qlexpress4.aparser.compiletimefunction.CodeGenerator;
import com.alibaba.qlexpress4.aparser.compiletimefunction.CompileTimeFunction;
import com.alibaba.qlexpress4.exception.*;
import com.alibaba.qlexpress4.runtime.*;
import com.alibaba.qlexpress4.runtime.instruction.*;
import com.alibaba.qlexpress4.runtime.operator.BinaryOperator;
import com.alibaba.qlexpress4.runtime.operator.OperatorManager;
import com.alibaba.qlexpress4.runtime.operator.unary.UnaryOperator;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;


import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

import static com.alibaba.qlexpress4.aparser.QLParser.*;

/**
 * Author: DQinYuan
 */
public class QvmInstructionVisitor extends QLParserBaseVisitor<Void> {
    private static final String SCOPE_SEPARATOR = "$";
    private static final String BLOCK_LAMBDA_NAME_PREFIX = "BLOCK_";
    private static final String IF_PREFIX = "IF_";
    private static final String THEN_SUFFIX = "_THEN";
    private static final String ELSE_SUFFIX = "_ELSE";
    private static final String MACRO_PREFIX = "MACRO_";
    private static final String LAMBDA_PREFIX = "LAMBDA_";
    private static final String TRY_PREFIX = "TRY_";
    private static final String CATCH_SUFFIX = "_CATCH";
    private static final String FINAL_SUFFIX = "_FINAL";
    private static final String FOR_PREFIX = "FOR_";
    private static final String INIT_SUFFIX = "_INIT";
    private static final String CONDITION_SUFFIX = "_CONDITION";
    private static final String UPDATE_SUFFIX = "_UPDATE";
    private static final String BODY_SUFFIX = "_BODY";
    private static final String WHILE_PREFIX = "WHILE_";

    private static final String LBRACE = "{";
    private static final String RBRACE = "}";

    private static final BigInteger MAX_LONG = BigInteger.valueOf(Long.MAX_VALUE);

    private static final BigInteger MAX_INTEGER = BigInteger.valueOf(Integer.MAX_VALUE);

    private static final BigDecimal MAX_DOUBLE = new BigDecimal(String.valueOf(Double.MAX_VALUE));

    private static final int TIMEOUT_CHECK_GAP = 5;

    public enum Context {
        BLOCK, MACRO
    }

    private final String script;

    private final ImportManager importManager;

    private final GeneratorScope generatorScope;

    private final OperatorFactory operatorFactory;

    private final Map<String, CompileTimeFunction> compileTimeFunctions;

    private final InitOptions initOptions;

    private final Context context;

    private final List<QLInstruction> instructionList = new ArrayList<>();

    private int stackSize;

    private int maxStackSize;

    private int ifCounter = 0;
    private int blockCounter = 0;
    private int macroCounter = 0;
    private int lambdaCounter = 0;
    private int tryCounter = 0;
    private int forCounter = 0;
    private int whileCounter = 0;

    private int timeoutCheckPoint = -1;

    /*
     * main constructor
     */
    public QvmInstructionVisitor(String script, ImportManager importManager,
                                 GeneratorScope globalScope,
                                 OperatorFactory operatorFactory,
                                 Map<String, CompileTimeFunction> compileTimeFunctions,
                                 InitOptions initOptions) {
        this.script = script;
        this.importManager = importManager;
        this.generatorScope = new GeneratorScope("main", globalScope);
        this.operatorFactory = operatorFactory;
        this.context = Context.BLOCK;
        this.compileTimeFunctions = compileTimeFunctions;
        this.initOptions = initOptions;
    }

    /*
     *  for recursion
     */
    public QvmInstructionVisitor(String script, ImportManager importManager,
                                 GeneratorScope generatorScope, OperatorFactory operatorFactory,
                                 Context context, Map<String, CompileTimeFunction> compileTimeFunctions,
                                 InitOptions initOptions) {
        this.script = script;
        this.importManager = importManager;
        this.generatorScope = generatorScope;
        this.operatorFactory = operatorFactory;
        this.context = context;
        this.compileTimeFunctions = compileTimeFunctions;
        this.initOptions = initOptions;
    }

    /*
     * visible for testing
     */
    public QvmInstructionVisitor(String script) {
        this.script = script;
        this.importManager = new ImportManager(DefaultClassSupplier.getInstance(),
                new ArrayList<>(), new HashMap<>());
        this.generatorScope = new GeneratorScope("test-main", null);
        this.operatorFactory = new OperatorManager();
        this.context = Context.BLOCK;
        this.compileTimeFunctions = new HashMap<>();
        this.initOptions = InitOptions.DEFAULT_OPTIONS;
    }

    @Override
    public Void visitImportCls(ImportClsContext ctx) {
        String importClsPath = ctx.varId().stream()
                .map(VarIdContext::getStart)
                .map(Token::getText)
                .collect(Collectors.joining("."));
        importManager.addImport(ImportManager.importCls(importClsPath));
        return null;
    }

    @Override
    public Void visitImportPack(ImportPackContext ctx) {
        List<VarIdContext> importPackPathTokens = ctx.varId();
        boolean isInnerCls = !Character.isLowerCase(importPackPathTokens.get(importPackPathTokens.size() - 1)
                .getText().charAt(0));
        String importPath = importPackPathTokens.stream()
                .map(VarIdContext::getStart)
                .map(Token::getText)
                .collect(Collectors.joining("."));
        importManager.addImport(isInnerCls ? ImportManager.importInnerCls(importPath) :
                ImportManager.importPack(importPath));
        return null;
    }

    @Override
    public Void visitBlockStatements(BlockStatementsContext blockStatementsContext) {
        boolean isPreExpress = false;
        List<BlockStatementContext> nonEmptyChildren = blockStatementsContext.blockStatement()
                .stream().filter(bs -> !(bs instanceof EmptyStatementContext))
                .collect(Collectors.toList());
        for (BlockStatementContext child: nonEmptyChildren) {
            if (isPreExpress) {
                // pop if expression without acceptor
                addInstruction(new PopInstruction(PureErrReporter.INSTANCE));
            }
            isPreExpress = handleStmt(child);
        }

        if (context == Context.BLOCK) {
            if (isPreExpress) {
                addInstruction(new ReturnInstruction(
                        PureErrReporter.INSTANCE,
                        QResult.ResultType.CONTINUE,
                        null)
                );
            }
        }
        return null;
    }

    private void visitBodyExpression(ExpressionContext expressionContext) {
        BlockExprContext blockExprContext = blockExpr(expressionContext);
        if (blockExprContext != null) {
            BlockStatementsContext blockStatementsContext = blockExprContext.blockStatements();
            if (blockStatementsContext == null) {
                return;
            }
            blockStatementsContext.accept(this);
            return;
        }
        expressionContext.accept(this);
        addInstruction(new ReturnInstruction(
                newReporterWithToken(expressionContext.getStart()), QResult.ResultType.RETURN, null));
    }

    private BlockExprContext blockExpr(ExpressionContext expressionContext) {
        Token startToken = expressionContext.getStart();
        Token stopToken = expressionContext.getStop();
        if (!(
                LBRACE.equals(startToken.getText()) && RBRACE.equals(stopToken.getText())
        )) {
            // fast fail
            return null;
        }
        TernaryExprContext ternaryExprContext = expressionContext.ternaryExpr();
        if (ternaryExprContext == null) {
            return null;
        }
        if (ternaryExprContext.QUESTION() != null) {
            return null;
        }
        BaseExprContext baseExprContext = ternaryExprContext.baseExpr(0);
        if (!baseExprContext.leftAsso().isEmpty()) {
            return null;
        }
        PrimaryNoFixContext primaryNoFixContext = baseExprContext.primary().primaryNoFix();
        return primaryNoFixContext instanceof BlockExprContext ? (BlockExprContext) primaryNoFixContext : null;
    }

    @Override
    public Void visitExpression(ExpressionContext ctx) {
        TernaryExprContext ternaryExprContext = ctx.ternaryExpr();
        if (ternaryExprContext != null) {
            ternaryExprContext.accept(this);
            return null;
        }

        ctx.leftHandSide().accept(this);
        ctx.expression().accept(this);

        AssignOperatorContext assignOperatorContext = ctx.assignOperator();
        BinaryOperator assignOperator = operatorFactory.getBinaryOperator(assignOperatorContext.getText());
        addInstruction(new OperatorInstruction(newReporterWithToken(assignOperatorContext.getStart()),
                assignOperator, assignOperatorContext.getStart().getStartIndex()));
        return null;
    }

    @Override
    public Void visitTraditionalForStatement(TraditionalForStatementContext ctx) {
        int forCount = forCount();
        ErrorReporter forErrReporter = newReporterWithToken(ctx.FOR().getSymbol());

        // for init
        ForInitContext forInitContext = ctx.forInit();
        QLambdaDefinitionInner forInitLambda = forInitContext == null ? null :
                generateForInitLambda(forCount, forInitContext);

        // condition
        ExpressionContext forConditionContext = ctx.forCondition;
        QLambdaDefinitionInner forConditionLambda = forConditionContext == null ? null :
                generateForExpressLambda(forCount, CONDITION_SUFFIX, forConditionContext);

        // for update
        ExpressionContext forUpdateContext = ctx.forUpdate;
        QLambdaDefinitionInner forUpdateLambda = forUpdateContext == null ? null :
                generateForExpressLambda(forCount, UPDATE_SUFFIX, forUpdateContext);

        // for body
        QLambdaDefinition forBodyLambda = loopBodyVisitorDefinition(ctx.blockStatements(),
                generatorScope.getName() + SCOPE_SEPARATOR + FOR_PREFIX + forCount + BODY_SUFFIX,
                Collections.emptyList(), forErrReporter
        );

        int forInitSize = forInitLambda == null ? 0 : forInitLambda.getMaxStackSize();
        int forConditionSize = forConditionLambda == null ? 0 : forConditionLambda.getMaxStackSize();
        int forUpdateSize = forUpdateLambda == null ? 0 : forUpdateLambda.getMaxStackSize();
        int forScopeMaxStackSize = Math.max(forInitSize, Math.max(forConditionSize, forUpdateSize));

        if (initOptions.isTraceExpression()) {
            pureAddInstruction(new TraceEvaludatedInstruction(forErrReporter, ctx.FOR().getSymbol().getStartIndex()));
        }

        addInstruction(new ForInstruction(forErrReporter, forInitLambda, forConditionLambda,
                forConditionContext != null ? newReporterWithToken(forConditionContext.getStart()) : null,
                forUpdateLambda, forScopeMaxStackSize,
                forBodyLambda));
        return null;
    }

    private QLambdaDefinitionInner generateForInitLambda(int forCount, ForInitContext forInitContext) {
        if (forInitContext.localVariableDeclaration() != null) {
            String scopeName = generatorScope.getName() + SCOPE_SEPARATOR + FOR_PREFIX + forCount + INIT_SUFFIX;
            QvmInstructionVisitor subVisitor = parseWithSubVisitor(forInitContext.localVariableDeclaration(),
                    new GeneratorScope(scopeName, generatorScope), Context.MACRO);
            return new QLambdaDefinitionInner(scopeName, subVisitor.getInstructions(),
                    Collections.emptyList(), subVisitor.getMaxStackSize());
        } else if (forInitContext.expression() != null) {
            return generateForExpressLambda(forCount, INIT_SUFFIX, forInitContext.expression());
        } else {
            return null;
        }
    }

    private QLambdaDefinitionInner generateForExpressLambda(int forCount, String scopeSuffix, ExpressionContext expressionContext) {
        String scopeName = generatorScope.getName() + SCOPE_SEPARATOR + FOR_PREFIX + forCount + scopeSuffix;
        QvmInstructionVisitor subVisitor = parseExprBodyWithSubVisitor(expressionContext,
                new GeneratorScope(scopeName, generatorScope), Context.BLOCK);
        return new QLambdaDefinitionInner(scopeName, subVisitor.getInstructions(),
                Collections.emptyList(), subVisitor.getMaxStackSize());
    }

    @Override
    public Void visitForEachStatement(ForEachStatementContext ctx) {
        ExpressionContext targetExprContext = ctx.expression();
        targetExprContext.accept(this);

        DeclTypeContext declTypeContext = ctx.declType();
        Class<?> itVarCls = declTypeContext == null ? Object.class : parseDeclType(declTypeContext);

        ErrorReporter forEachErrReporter = newReporterWithToken(ctx.FOR().getSymbol());
        QLambdaDefinition bodyDefinition = loopBodyVisitorDefinition(ctx.blockStatements(),
                generatorScope.getName() + SCOPE_SEPARATOR + FOR_PREFIX + forCount() + BODY_SUFFIX,
                Collections.singletonList(
                        new QLambdaDefinitionInner.Param(ctx.varId().getText(), itVarCls)
                ),
                forEachErrReporter
        );

        if (initOptions.isTraceExpression()) {
            pureAddInstruction(new TraceEvaludatedInstruction(forEachErrReporter, ctx.FOR().getSymbol().getStartIndex()));
        }

        addInstruction(new ForEachInstruction(
                        forEachErrReporter, bodyDefinition,
                        itVarCls, newReporterWithToken(targetExprContext.getStart())
                )
        );

        return null;
    }

    @Override
    public Void visitWhileStatement(WhileStatementContext ctx) {
        int whileCount = whileCount();

        String whileConditionScope = generatorScope.getName() + SCOPE_SEPARATOR + WHILE_PREFIX +
                whileCount + CONDITION_SUFFIX;
        QvmInstructionVisitor conditionSubVisitor = parseExprBodyWithSubVisitor(ctx.expression(),
                new GeneratorScope(whileConditionScope, generatorScope), Context.BLOCK);
        QLambdaDefinitionInner conditionLambda = new QLambdaDefinitionInner(whileConditionScope, conditionSubVisitor.getInstructions(),
                Collections.emptyList(), conditionSubVisitor.getMaxStackSize());

        ErrorReporter whileErrReporter = newReporterWithToken(ctx.WHILE().getSymbol());
        QLambdaDefinition whileBodyLambda = loopBodyVisitorDefinition(ctx.blockStatements(),
                generatorScope.getName() + SCOPE_SEPARATOR + WHILE_PREFIX + whileCount + BODY_SUFFIX,
                Collections.emptyList(),
                whileErrReporter
        );

        if (initOptions.isTraceExpression()) {
            pureAddInstruction(new TraceEvaludatedInstruction(whileErrReporter, ctx.WHILE().getSymbol().getStartIndex()));
        }

        addInstruction(new WhileInstruction(
                whileErrReporter, conditionLambda, whileBodyLambda,
                whileBodyLambda instanceof QLambdaDefinitionInner?
                        Math.max(conditionLambda.getMaxStackSize(), ((QLambdaDefinitionInner) whileBodyLambda).getMaxStackSize()):
                        conditionLambda.getMaxStackSize()
        ));
        return null;
    }

    private QLambdaDefinition loopBodyVisitorDefinition(BlockStatementsContext bodyCtx, String scopeName,
                                                             List<QLambdaDefinitionInner.Param> paramsType,
                                                             ErrorReporter errorReporter) {
        if (bodyCtx == null) {
            return QLambdaDefinitionEmpty.INSTANCE;
        }
        QvmInstructionVisitor bodyVisitor = parseWithSubVisitor(bodyCtx, new GeneratorScope(scopeName, generatorScope), Context.MACRO);
        List<QLInstruction> bodyInstructions = bodyVisitor.getInstructions();

        List<QLInstruction> resultInstructions = new ArrayList<>();
        resultInstructions.add(new CheckTimeOutInstruction(errorReporter));
        resultInstructions.addAll(bodyInstructions);

        return new QLambdaDefinitionInner(scopeName, resultInstructions, paramsType, bodyVisitor.getMaxStackSize());
    }

    @Override
    public Void visitThrowStatement(ThrowStatementContext ctx) {
        ctx.expression().accept(this);
        addInstruction(new ThrowInstruction(newReporterWithToken(ctx.THROW().getSymbol())));
        return null;
    }

    @Override
    public Void visitReturnStatement(ReturnStatementContext ctx) {
        ErrorReporter errorReporter = newReporterWithToken(ctx.getStart());
        ExpressionContext expression = ctx.expression();
        if (expression == null) {
            addInstruction(new ConstInstruction(errorReporter, null, null));
        } else {
            expression.accept(this);
        }

        addInstruction(new ReturnInstruction(errorReporter, QResult.ResultType.RETURN, ctx.getStart().getStartIndex()));
        return null;
    }

    @Override
    public Void visitFunctionStatement(FunctionStatementContext ctx) {
        FormalOrInferredParameterListContext formalOrInferredParameterList = ctx.formalOrInferredParameterList();
        List<QLambdaDefinitionInner.Param> params = formalOrInferredParameterList == null ?
                Collections.emptyList() :
                parseFormalOrInferredParameterList(formalOrInferredParameterList);
        VarIdContext functionNameCtx = ctx.varId();
        QLambdaDefinition functionDefinition = parseFunctionDefinition(functionNameCtx.getText(), ctx, params);

        ErrorReporter errorReporter = newReporterWithToken(functionNameCtx.getStart());
        
        if (initOptions.isTraceExpression()) {
            pureAddInstruction(new TraceEvaludatedInstruction(errorReporter, functionNameCtx.getStart().getStartIndex()));
        }
        
        addInstruction(new DefineFunctionInstruction(errorReporter, functionDefinition.getName(), functionDefinition));
        return null;
    }

    private QLambdaDefinition parseFunctionDefinition(String functionName, FunctionStatementContext ctx,
                                                      List<QLambdaDefinitionInner.Param> params) {
        BlockStatementsContext blockStatementsContext = ctx.blockStatements();
        if (blockStatementsContext == null) {
            return new QLambdaDefinitionInner(functionName, Collections.emptyList(), params, 0);
        }

        QvmInstructionVisitor functionSubVisitor = parseWithSubVisitor(blockStatementsContext,
                new GeneratorScope(functionName, generatorScope), Context.BLOCK);
        return new QLambdaDefinitionInner(functionName, functionSubVisitor.getInstructions(),
                params, functionSubVisitor.getMaxStackSize());
    }

    @Override
    public Void visitCastExpr(CastExprContext ctx) {
        DeclTypeContext castDeclTypeContext = ctx.declType();
        Class<?> castCls = parseDeclType(castDeclTypeContext);
        ErrorReporter errorReporter = newReporterWithToken(castDeclTypeContext.getStart());
        addInstruction(new ConstInstruction(errorReporter, castCls, null));
        ctx.primary().accept(this);
        addInstruction(new CastInstruction(errorReporter));
        return null;
    }

    @Override
    public Void visitTernaryExpr(TernaryExprContext ctx) {
        ctx.condition.accept(this);

        if (ctx.QUESTION() != null) {
            QvmInstructionVisitor thenVisitor = parseWithSubVisitor(ctx.thenExpr, generatorScope, Context.MACRO);
            QvmInstructionVisitor elseVisitor = parseWithSubVisitor(ctx.elseExpr, generatorScope, Context.MACRO);
            ifElseInstructions(
                    newReporterWithToken(ctx.QUESTION().getSymbol()),
                    thenVisitor.getInstructions(), null, elseVisitor.getInstructions(), null,
                    ctx.QUESTION().getSymbol().getStartIndex()
            );
        }

        return null;
    }

    @Override
    public Void visitBlockExpr(BlockExprContext ctx) {
        ErrorReporter blockErrReporter = newReporterWithToken(ctx.getStart());
        BlockStatementsContext blockStatementsContext = ctx.blockStatements();
        if (blockStatementsContext == null) {
            addInstruction(new ConstInstruction(blockErrReporter, null, null));
            return null;
        }

        String blockScopeName = blockScopeName();
        QvmInstructionVisitor blockSubVisitor = parseWithSubVisitor(blockStatementsContext,
                new GeneratorScope(blockScopeName, generatorScope), Context.MACRO);

        addInstruction(new NewScopeInstruction(blockErrReporter, blockScopeName));
        blockSubVisitor.getInstructions().forEach(this::addInstruction);
        addInstruction(new CloseScopeInstruction(blockErrReporter, blockScopeName));
        if (initOptions.isTraceExpression()) {
            pureAddInstruction(new TracePeekInstruction(blockErrReporter, ctx.getStart().getStartIndex()));
        }
        return null;
    }

    @Override
    public Void visitQlIf(QlIfContext qlIfContext) {
        qlIfContext.condition.accept(this);

        int ifCount = ifCount();
        ErrorReporter ifErrorReporter = newReporterWithToken(qlIfContext.IF().getSymbol());
        String ifScopeName = generatorScope.getName() + SCOPE_SEPARATOR + IF_PREFIX + ifCount;
        addInstruction(new NewScopeInstruction(ifErrorReporter, ifScopeName));

        String thenScopeName = generatorScope.getName() + SCOPE_SEPARATOR + IF_PREFIX + ifCount + THEN_SUFFIX;
        ThenBodyContext thenBodyContext = qlIfContext.thenBody();
        List<QLInstruction> thenInstructions = parseWithSubVisitor(thenBodyContext,
                new GeneratorScope(thenScopeName, generatorScope), Context.MACRO).getInstructions();
        if (ifBodyFillConst(thenBodyContext.expression(), thenBodyContext.blockStatement(), thenBodyContext.blockStatements())) {
            thenInstructions.add(new ConstInstruction(ifErrorReporter, null, null));
        }
        Integer thenTraceKey = thenBodyContext.LBRACE() == null ? null : thenBodyContext.getStart().getStartIndex();

        String elseScopeName = generatorScope.getName() + SCOPE_SEPARATOR + IF_PREFIX + ifCount + ELSE_SUFFIX;
        ElseBodyContext elseBodyContext = qlIfContext.elseBody();
        List<QLInstruction> elseInstructions = elseBodyContext == null ?
                Collections.singletonList(new ConstInstruction(ifErrorReporter, null, null)) :
                parseWithSubVisitor(elseBodyContext, new GeneratorScope(elseScopeName, generatorScope),
                        Context.MACRO).getInstructions();
        if (elseBodyContext != null && elseBodyContext.qlIf() == null && ifBodyFillConst(
                elseBodyContext.expression(), elseBodyContext.blockStatement(), elseBodyContext.blockStatements())) {
            elseInstructions.add(new ConstInstruction(ifErrorReporter, null, null));
        }
        Integer elseTraceKey = elseBodyContext == null ? null :
                (elseBodyContext.LBRACE() == null ? null : elseBodyContext.getStart().getStartIndex());

        ifElseInstructions(ifErrorReporter, thenInstructions, thenTraceKey, elseInstructions, elseTraceKey, qlIfContext.getStart().getStartIndex());

        addInstruction(new CloseScopeInstruction(ifErrorReporter, ifScopeName));
        return null;
    }

    private boolean ifBodyFillConst(ExpressionContext expressionContext,
                                    BlockStatementContext blockStatementContext,
                                    BlockStatementsContext blockStatementsContext) {
        if (expressionContext != null) {
            return false;
        }
        if (blockStatementContext != null) {
            return stmtFillConst(blockStatementContext);
        }
        if (blockStatementsContext != null) {
            List<BlockStatementContext> statementList = blockStatementsContext.blockStatement().stream()
                    .filter(bs -> !(bs instanceof EmptyStatementContext))
                    .collect(Collectors.toList());
            return statementList.isEmpty() || stmtFillConst(statementList.get(statementList.size() - 1));
        }
        return true;
    }

    private boolean stmtFillConst(BlockStatementContext blockStatementContext) {
        return !(blockStatementContext instanceof ExpressionStatementContext) && !(blockStatementContext instanceof ReturnStatementContext);
    }

    @Override
    public Void visitBreakContinueStatement(BreakContinueStatementContext ctx) {
        TerminalNode aBreak = ctx.BREAK();
        QResult qResult = aBreak == null ? QResult.LOOP_CONTINUE_RESULT : QResult.LOOP_BREAK_RESULT;
        
        if (initOptions.isTraceExpression()) {
            pureAddInstruction(new TraceEvaludatedInstruction(newReporterWithToken(ctx.getStart()), ctx.getStart().getStartIndex()));
        }
        
        addInstruction(
                new BreakContinueInstruction(newReporterWithToken(ctx.getStart()), qResult)
        );
        return null;
    }

    @Override
    public Void visitListExpr(ListExprContext ctx) {
        visitListExprInner(ctx.listItems(), newReporterWithToken(ctx.getStart()));
        return null;
    }

    private void visitListExprInner(ListItemsContext listItemsContext, ErrorReporter listErrorReporter) {
        if (listItemsContext == null) {
            addInstruction(new NewListInstruction(listErrorReporter, 0));
            return;
        }
        List<ExpressionContext> expressions = listItemsContext.expression();
        for (ExpressionContext expression : expressions) {
            expression.accept(this);
        }
        addInstruction(new NewListInstruction(listErrorReporter, expressions.size()));
    }

    @Override
    public Void visitMapExpr(MapExprContext ctx) {
        MapEntriesContext mapEntriesContext = ctx.mapEntries();
        List<MapEntryContext> mapEntryContexts = mapEntriesContext.mapEntry();
        List<String> keys = new ArrayList<>(mapEntryContexts.size());
        Class<?> cls = null;
        for (MapEntryContext mapEntryContext : mapEntryContexts) {
            MapValueContext valueContext = mapEntryContext.mapValue();
            if (valueContext instanceof EValueContext) {
                EValueContext eValueContext = (EValueContext) valueContext;
                keys.add(parseMapKey(mapEntryContext.mapKey()));
                eValueContext.expression().accept(this);
                continue;
            }
            if (valueContext instanceof ClsValueContext) {
                ClsValueContext clsValueContext = (ClsValueContext) valueContext;
                TerminalNode clsLiteral = clsValueContext.QuoteStringLiteral();
                String clsText = clsLiteral.getText();
                String clsName = clsText.substring(1, clsText.length() - 1);
                Class<?> mayBeCls = importManager.loadQualified(clsName);
                if (mayBeCls == null) {
                    String clsKeyText = mapEntryContext.mapKey().getText();
                    keys.add(clsKeyText.substring(1, clsKeyText.length() - 1));
                    addInstruction(new ConstInstruction(newReporterWithToken(clsLiteral.getSymbol()),
                            parseStringEscape(clsText), null));
                    // @class override
                    cls = null;
                } else {
                    cls = mayBeCls;
                }
            }
        }
        if (cls == null) {
            addInstruction(new NewMapInstruction(newReporterWithToken(ctx.getStart()), keys));
        } else {
            addInstruction(new NewFilledInstanceInstruction(newReporterWithToken(ctx.getStart()), cls, keys));
        }
        return null;
    }

    private String parseMapKey(MapKeyContext mapKeyContext) {
        if (mapKeyContext instanceof IdKeyContext) {
            return mapKeyContext.getText();
        } else if (mapKeyContext instanceof StringKeyContext || mapKeyContext instanceof QuoteStringKeyContext) {
            return parseStringEscape(mapKeyContext.getText());
        }
        // shouldn't run here
        throw new IllegalStateException();
    }

    @Override
    public Void visitNewObjExpr(NewObjExprContext ctx) {
        Class<?> newCls = parseClsIds(ctx.varId());
        ArgumentListContext argumentListContext = ctx.argumentList();
        if (argumentListContext != null) {
            argumentListContext.accept(this);
        }
        int argNum = argumentListContext == null ? 0 : argumentListContext.expression().size();
        addInstruction(new NewInstanceInstruction(newReporterWithToken(ctx.NEW().getSymbol()), newCls, argNum));
        return null;
    }

    @Override
    public Void visitNewEmptyArrExpr(NewEmptyArrExprContext ctx) {
        ctx.dimExprs().accept(this);
        int dims = ctx.dimExprs().expression().size();
        Class<?> arrCls = parseDeclTypeNoArr(ctx.declTypeNoArr());
        addInstruction(new MultiNewArrayInstruction(
                        newReporterWithToken(ctx.NEW().getSymbol()), arrCls, dims
                )
        );
        return null;
    }

    @Override
    public Void visitNewInitArrExpr(NewInitArrExprContext ctx) {
        Class<?> cls = parseDeclTypeNoArr(ctx.declTypeNoArr());
        ArrayInitializerContext arrayInitializerContext = ctx.arrayInitializer();
        newArrWithInitializers(embedClsInDims(cls, ctx.dims().LBRACK().size() - 1), arrayInitializerContext);
        return null;
    }

    private Class<?> embedClsInDims(Class<?> cls, int dims) {
        for (int i = 0; i < dims; i++) {
            cls = Array.newInstance(cls, 0).getClass();
        }
        return cls;
    }

    @Override
    public Void visitLambdaExpr(LambdaExprContext ctx) {
        List<QLambdaDefinitionInner.Param> lambdaParams = parseLambdaParams(ctx.lambdaParameters());
        String lambdaScopeName = lambdaScopeName();

        QvmInstructionVisitor subVisitor = null;
        ExpressionContext expression = ctx.expression();
        ErrorReporter arrowErrorReporter = newReporterWithToken(ctx.ARROW().getSymbol());
        if (expression != null) {
            subVisitor = parseExprBodyWithSubVisitor(expression, new GeneratorScope(lambdaScopeName, generatorScope), Context.BLOCK);
        } else {
            BlockStatementsContext blockStatementsContext = ctx.blockStatements();
            if (blockStatementsContext != null) {
                subVisitor = parseWithSubVisitor(blockStatementsContext, new GeneratorScope(lambdaScopeName, generatorScope), Context.BLOCK);
            }
        }

        if (subVisitor == null) {
            addInstruction(new LoadLambdaInstruction(arrowErrorReporter, QLambdaDefinitionEmpty.INSTANCE));
        } else {
            QLambdaDefinition lambdaDefinition = new QLambdaDefinitionInner(lambdaScopeName,
                    subVisitor.getInstructions(), lambdaParams, subVisitor.getMaxStackSize());
            addInstruction(new LoadLambdaInstruction(arrowErrorReporter, lambdaDefinition));
        }
        return null;
    }

    private List<QLambdaDefinitionInner.Param> parseLambdaParams(LambdaParametersContext lambdaParametersContext) {
        VarIdContext varIdContext = lambdaParametersContext.varId();
        if (varIdContext != null) {
            return Collections.singletonList(new QLambdaDefinitionInner.Param(varIdContext.getText(), Object.class));
        }
        FormalOrInferredParameterListContext formalOrInferredParameterList = lambdaParametersContext
                .formalOrInferredParameterList();
        if (formalOrInferredParameterList == null) {
            return Collections.emptyList();
        }
        return formalOrInferredParameterList.formalOrInferredParameter().stream()
                .map(this::formalOrInferredParameter2Param)
                .collect(Collectors.toList());
    }

    private List<QLambdaDefinitionInner.Param> parseFormalOrInferredParameterList(
            FormalOrInferredParameterListContext formalOrInferredParameterList) {
        return formalOrInferredParameterList.formalOrInferredParameter().stream()
                .map(this::formalOrInferredParameter2Param)
                .collect(Collectors.toList());
    }

    private QLambdaDefinitionInner.Param formalOrInferredParameter2Param(
            FormalOrInferredParameterContext formalOrInferredParameterContext) {
        String paramName = formalOrInferredParameterContext.varId().getText();
        DeclTypeContext declTypeContext = formalOrInferredParameterContext.declType();
        Class<?> paramCls = declTypeContext == null ? Object.class : parseDeclType(declTypeContext);
        return new QLambdaDefinitionInner.Param(paramName, paramCls);
    }

    @Override
    public Void visitTryCatchExpr(TryCatchExprContext ctx) {
        BlockStatementsContext blockStatementsContext = ctx.blockStatements();
        if (blockStatementsContext == null) {
            addInstruction(new ConstInstruction(
                    newReporterWithToken(ctx.TRY().getSymbol()),
                    null, ctx.getStart().getStartIndex()
            ));
            return null;
        }

        int tryCount = tryCount();
        String tryScopeName = generatorScope.getName() + SCOPE_SEPARATOR + TRY_PREFIX + tryCount;
        QvmInstructionVisitor bodySubVisitor = parseWithSubVisitor(blockStatementsContext,
                new GeneratorScope(tryScopeName, generatorScope), Context.BLOCK);

        QLambdaDefinition bodyLambdaDefinition = new QLambdaDefinitionInner(tryScopeName,
                bodySubVisitor.getInstructions(), Collections.emptyList(), bodySubVisitor.getMaxStackSize());
        List<Map.Entry<Class<?>, QLambdaDefinition>> exceptionTable = parseExceptionTable(tryCount, ctx);
        QLambdaDefinition finalBodyDefinition = parseFinalBodyDefinition(tryCount, ctx);

        addInstruction(new TryCatchInstruction(newReporterWithToken(ctx.TRY().getSymbol()), bodyLambdaDefinition,
                exceptionTable, finalBodyDefinition));
        return null;
    }

    private QLambdaDefinition parseFinalBodyDefinition(int tryCount, TryCatchExprContext ctx) {
        TryFinallyContext tryFinallyContext = ctx.tryFinally();
        if (tryFinallyContext == null) {
            return null;
        }

        BlockStatementsContext blockStatementsContext = tryFinallyContext.blockStatements();
        if (blockStatementsContext == null) {
            return null;
        }

        String finalScopeName = generatorScope.getName() + SCOPE_SEPARATOR + TRY_PREFIX + tryCount + FINAL_SUFFIX;
        QvmInstructionVisitor finalBodySubVisitor = parseWithSubVisitor(blockStatementsContext,
                new GeneratorScope(finalScopeName, generatorScope), Context.BLOCK);
        return new QLambdaDefinitionInner(finalScopeName, finalBodySubVisitor.getInstructions(),
                Collections.emptyList(), finalBodySubVisitor.getMaxStackSize());
    }

    private List<Map.Entry<Class<?>, QLambdaDefinition>> parseExceptionTable(int tryCount, TryCatchExprContext ctx) {
        TryCatchesContext tryCatchesContext = ctx.tryCatches();
        if (tryCatchesContext == null) {
            return Collections.emptyList();
        }
        List<TryCatchContext> tryCatchContexts = tryCatchesContext.tryCatch();
        int catchSize = tryCatchContexts.size();
        List<Map.Entry<Class<?>, QLambdaDefinition>> exceptionTable = new ArrayList<>(catchSize);
        for (TryCatchContext tryCatchContext : tryCatchContexts) {
            CatchParamsContext catchParamsContext = tryCatchContext.catchParams();
            String eName = catchParamsContext.varId().getText();
            String catchBodyName = generatorScope.getName() + SCOPE_SEPARATOR +
                    TRY_PREFIX + tryCount + CATCH_SUFFIX;
            QvmInstructionVisitor catchSubVisitor = tryCatchContext.blockStatements() == null? null:
                    parseWithSubVisitor(tryCatchContext.blockStatements(), new GeneratorScope(catchBodyName, generatorScope), Context.BLOCK);

            List<DeclTypeContext> catchDeclTypes = catchParamsContext.declType();
            if (catchDeclTypes.isEmpty()) {
                QLambdaDefinitionInner.Param param = new QLambdaDefinitionInner.Param(eName, Object.class);
                QLambdaDefinition exceptionHandlerDefinition = catchSubVisitor == null?
                        QLambdaDefinitionEmpty.INSTANCE:
                        new QLambdaDefinitionInner(
                                catchBodyName, catchSubVisitor.getInstructions(),
                                Collections.singletonList(param), catchSubVisitor.getMaxStackSize()
                        );
                exceptionTable.add(new AbstractMap.SimpleEntry<>(Object.class, exceptionHandlerDefinition));
            }
            for (DeclTypeContext declTypeContext : catchDeclTypes) {
                Class<?> exceptionType = parseDeclType(declTypeContext);
                QLambdaDefinitionInner.Param param = new QLambdaDefinitionInner.Param(
                        eName, exceptionType
                );
                QLambdaDefinition exceptionHandlerDefinition = catchSubVisitor == null? QLambdaDefinitionEmpty.INSTANCE:
                        new QLambdaDefinitionInner(
                                catchBodyName, catchSubVisitor.getInstructions(),
                                Collections.singletonList(param), catchSubVisitor.getMaxStackSize()
                        );
                exceptionTable.add(new AbstractMap.SimpleEntry<>(exceptionType, exceptionHandlerDefinition));
            }
        }
        return exceptionTable;
    }

    @Override
    public Void visitVarIdExpr(VarIdExprContext ctx) {
        addInstruction(new LoadInstruction(
                newReporterWithToken(ctx.getStart()), ctx.varId().getText(), ctx.getStart().getStartIndex()
            )
        );
        return null;
    }

    private int parsePathHeadPart(PrimaryNoFixContext primaryNoFixContext, List<PathPartContext> pathPartContexts) {
        if (primaryNoFixContext instanceof TypeExprContext) {
            Class<?> cls = BuiltInTypesSet.getCls(primaryNoFixContext.getStart().getText());
            int dimPartNum = parseDimParts(0, pathPartContexts);
            addInstruction(new ConstInstruction(
                    newReporterWithToken(primaryNoFixContext.getStart()),
                    new MetaClass(dimPartNum > 0 ? wrapInArray(cls, dimPartNum) : cls), null)
            );
            return dimPartNum;
        } else if (!(primaryNoFixContext instanceof VarIdExprContext)) {
            primaryNoFixContext.accept(this);
            return 0;
        } else {
            VarIdExprContext idContext = (VarIdExprContext) primaryNoFixContext;
            return parseIdHeadPart(idContext.varId(), pathPartContexts);
        }
    }

    private int parseIdHeadPart(VarIdContext idContext, List<PathPartContext> pathPartContexts) {
        if (!pathPartContexts.isEmpty() && pathPartContexts.get(0) instanceof CallExprContext) {
            // call function
            CallExprContext callExprContext = (CallExprContext) pathPartContexts.get(0);
            ArgumentListContext argumentListContext = callExprContext.argumentList();
            visitCallFunction(idContext, argumentListContext);
            return 1;
        }
        List<String> headPartIds = new ArrayList<>();
        headPartIds.add(idContext.getText());
        for (PathPartContext pathPartContext : pathPartContexts) {
            if (pathPartContext instanceof FieldAccessContext) {
                headPartIds.add(parseFieldId(
                        ((FieldAccessContext) pathPartContext).fieldId()
                ));
            } else {
                break;
            }
        }
        ImportManager.LoadPartQualifiedResult loadPartQualifiedResult = importManager.loadPartQualified(headPartIds);
        if (loadPartQualifiedResult.getCls() != null) {
            int restIndex = loadPartQualifiedResult.getRestIndex() - 1;
            Token clsReportToken = restIndex == 0 ? idContext.getStart() : pathPartContexts.get(restIndex - 1).getStop();
            int dimPartNum = parseDimParts(restIndex, pathPartContexts);
            Class<?> cls = dimPartNum > 0 ? wrapInArray(loadPartQualifiedResult.getCls(), dimPartNum) :
                    loadPartQualifiedResult.getCls();

            addInstruction(new ConstInstruction(newReporterWithToken(clsReportToken), new MetaClass(cls), null));
            return restIndex + dimPartNum;
        } else {
            addInstruction(new LoadInstruction(
                    newReporterWithToken(idContext.getStart()), idContext.getText(), idContext.getStart().getStartIndex()
                )
            );
            return 0;
        }
    }

    private int parseDimParts(int start, List<PathPartContext> pathPartContexts) {
        int i = start;
        for (; i < pathPartContexts.size(); i++) {
            PathPartContext pathPartContext = pathPartContexts.get(i);
            if (!isEmptyIndex(pathPartContext)) {
                break;
            }
        }
        return i - start;
    }

    private boolean isEmptyIndex(PathPartContext pathPartContext) {
        if (!(pathPartContext instanceof IndexExprContext)) {
            return false;
        }
        IndexExprContext indexExprContext = (IndexExprContext) pathPartContext;
        return indexExprContext.indexValueExpr() == null;
    }

    @Override
    public Void visitMethodInvoke(MethodInvokeContext ctx) {
        visitMethodInvokeInner(ctx.argumentList(), ctx.varId(), false);
        return null;
    }

    @Override
    public Void visitOptionalMethodInvoke(OptionalMethodInvokeContext ctx) {
        visitMethodInvokeInner(ctx.argumentList(), ctx.varId(), true);
        return null;
    }

    @Override
    public Void visitSpreadMethodInvoke(SpreadMethodInvokeContext ctx) {
        ArgumentListContext argumentListContext = ctx.argumentList();
        if (argumentListContext != null) {
            argumentListContext.accept(this);
        }
        VarIdContext methodName = ctx.varId();
        int argNum = argumentListContext == null ? 0 : argumentListContext.expression().size();
        addInstruction(new SpreadMethodInvokeInstruction(
                newReporterWithToken(methodName.getStart()), methodName.getText(), argNum
        ));
        return null;
    }

    private void visitMethodInvokeInner(ArgumentListContext argumentListContext, VarIdContext methodName,
                                        boolean optional) {
        if (argumentListContext != null) {
            argumentListContext.accept(this);
        }
        int argNum = argumentListContext == null ? 0 : argumentListContext.expression().size();
        addInstruction(new MethodInvokeInstruction(
                newReporterWithToken(methodName.getStart()), methodName.getText(), argNum, optional
        ));
    }

    @Override
    public Void visitFieldAccess(FieldAccessContext ctx) {
        Token fieldNameToken = ctx.getStop();
        addInstruction(new GetFieldInstruction(
                newReporterWithToken(fieldNameToken), parseFieldId(ctx.fieldId()), false
        ));
        return null;
    }

    @Override
    public Void visitOptionalFieldAccess(OptionalFieldAccessContext ctx) {
        Token fieldNameToken = ctx.getStop();
        addInstruction(new GetFieldInstruction(
                newReporterWithToken(fieldNameToken), parseFieldId(ctx.fieldId()), true
        ));
        return null;
    }

    @Override
    public Void visitSpreadFieldAccess(SpreadFieldAccessContext ctx) {
        Token fieldNameToken = ctx.getStop();
        addInstruction(new SpreadGetFieldInstruction(
                newReporterWithToken(fieldNameToken), parseFieldId(ctx.fieldId())
        ));
        return null;
    }

    private String parseFieldId(FieldIdContext ctx) {
        TerminalNode quoteStringLiteral = ctx.QuoteStringLiteral();
        if (quoteStringLiteral != null) {
            return parseStringEscape(quoteStringLiteral.getText());
        }
        return ctx.getStart().getText();
    }

    @Override
    public Void visitMethodAccess(MethodAccessContext ctx) {
        VarIdContext methodName = ctx.varId();
        addInstruction(new GetMethodInstruction(
                        newReporterWithToken(ctx.DCOLON().getSymbol()), methodName.getText()
                )
        );
        return null;
    }

    @Override
    public Void visitCallExpr(CallExprContext ctx) {
        ArgumentListContext argumentListContext = ctx.argumentList();
        if (argumentListContext != null) {
            argumentListContext.accept(this);
        }
        int argNum = argumentListContext == null ? 0 : argumentListContext.expression().size();
        addInstruction(new CallInstruction(newReporterWithToken(ctx.getStart()), argNum));
        return null;
    }

    @Override
    public Void visitIndexExpr(IndexExprContext ctx) {
        IndexValueExprContext indexValueExprContext = ctx.indexValueExpr();
        if (indexValueExprContext == null) {
            throw reportParseErr(ctx.getStop(), QLErrorCodes.MISSING_INDEX.name(), QLErrorCodes.MISSING_INDEX.getErrorMsg());
        }
        ErrorReporter errorReporter = newReporterWithToken(ctx.getStart());
        if (indexValueExprContext instanceof SingleIndexContext) {
            ((SingleIndexContext) indexValueExprContext).expression().accept(this);
            addInstruction(new IndexInstruction(errorReporter));
        } else if (indexValueExprContext instanceof SliceIndexContext) {
            SliceIndexContext sliceIndexContext = (SliceIndexContext) indexValueExprContext;
            if (sliceIndexContext.start == null && sliceIndexContext.end == null) {
                addInstruction(new SliceInstruction(errorReporter, SliceInstruction.Mode.COPY));
            } else if (sliceIndexContext.start == null) {
                sliceIndexContext.end.accept(this);
                addInstruction(new SliceInstruction(errorReporter, SliceInstruction.Mode.LEFT));
            } else if (sliceIndexContext.end == null) {
                sliceIndexContext.start.accept(this);
                addInstruction(new SliceInstruction(errorReporter, SliceInstruction.Mode.RIGHT));
            } else {
                sliceIndexContext.start.accept(this);
                sliceIndexContext.end.accept(this);
                addInstruction(new SliceInstruction(errorReporter, SliceInstruction.Mode.BOTH));
            }
        }
        return null;
    }

    @Override
    public Void visitCustomPath(CustomPathContext ctx) {
        ErrorReporter errorReporter = newReporterWithToken(ctx.getStart());
        String path = ctx.varId().getText();
        addInstruction(new ConstInstruction(errorReporter, path, null));

        String operatorId = ctx.opId().getText();
        BinaryOperator binaryOperator = operatorFactory.getBinaryOperator(operatorId);
        addInstruction(new OperatorInstruction(errorReporter, binaryOperator, ctx.opId().getStart().getStartIndex()));
        return null;
    }

    @Override
    public Void visitLeftAsso(LeftAssoContext ctx) {
        BinaryopContext binaryopContext = ctx.binaryop();
        ErrorReporter opErrReporter = newReporterWithToken(binaryopContext.getStart());
        String operatorId = binaryopContext.getText();
        BaseExprContext rightExpr = ctx.baseExpr();
        // short circuit operator
        if ("&&".equals(operatorId)) {
            jumpRightIfExpect(false, opErrReporter, rightExpr, operatorId, binaryopContext.getStart().getStartIndex());
        } else if ("||".equals(operatorId)) {
            jumpRightIfExpect(true, opErrReporter, rightExpr, operatorId, binaryopContext.getStart().getStartIndex());
        } else {
            rightExpr.accept(this);
            BinaryOperator binaryOperator = operatorFactory.getBinaryOperator(operatorId);
            addInstruction(new OperatorInstruction(opErrReporter, binaryOperator, binaryopContext.getStart().getStartIndex()));
        }
        return null;
    }

    @Override
    public Void visitMacroStatement(MacroStatementContext ctx) {
        String macroId = ctx.varId().getText();

        BlockStatementsContext macroBlockStatementsContext = ctx.blockStatements();

        BlockStatementContext lastStmt = getMacroLastStmt(ctx, macroBlockStatementsContext);
        generatorScope.defineMacro(macroId, new MacroDefine(
                getMacroInstructions(macroBlockStatementsContext),
                lastStmt instanceof ExpressionStatementContext)
        );
        
        if (initOptions.isTraceExpression()) {
            pureAddInstruction(new TraceEvaludatedInstruction(newReporterWithToken(ctx.varId().getStart()), ctx.varId().getStart().getStartIndex()));
        }
        
        return null;
    }

    private BlockStatementContext getMacroLastStmt(MacroStatementContext macroCtx,
                                                   BlockStatementsContext macroBlockStatementsCtx) {
        if (macroBlockStatementsCtx == null) {
            return macroCtx;
        }
        List<BlockStatementContext> blockStatementContexts = macroBlockStatementsCtx.blockStatement().stream()
                .filter(bs -> !(bs instanceof EmptyStatementContext))
                .collect(Collectors.toList());
        return blockStatementContexts.isEmpty() ? macroCtx : blockStatementContexts.get(blockStatementContexts.size() - 1);
    }

    private List<QLInstruction> getMacroInstructions(BlockStatementsContext macroBlockStatementsContext) {
        if (macroBlockStatementsContext == null) {
            return Collections.emptyList();
        } else {
            QvmInstructionVisitor subVisitor = parseWithSubVisitor(macroBlockStatementsContext,
                    new GeneratorScope(macroScopeName(), generatorScope), Context.MACRO);
            return subVisitor.getInstructions();
        }
    }

    @Override
    public Void visitLocalVariableDeclaration(LocalVariableDeclarationContext ctx) {
        if (initOptions.isTraceExpression()) {
            addInstruction(new TraceEvaludatedInstruction(newReporterWithToken(ctx.getStart()), ctx.getStart().getStartIndex()));
        }

        Class<?> declCls = parseDeclType(ctx.declType());
        List<VariableDeclaratorContext> variableDeclaratorContexts = ctx.variableDeclaratorList().variableDeclarator();
        for (VariableDeclaratorContext variableDeclarator : variableDeclaratorContexts) {
            VariableInitializerContext variableInitializer = variableDeclarator.variableInitializer();
            if (variableInitializer == null) {
                addInstruction(
                        new ConstInstruction(newReporterWithToken(variableDeclarator.getStop()), null, null)
                );
            } else {
                parseInitializer(variableInitializer, declCls);
            }
            VariableDeclaratorIdContext variableDeclaratorIdContext = variableDeclarator.variableDeclaratorId();
            addInstruction(
                    new DefineLocalInstruction(newReporterWithToken(variableDeclaratorIdContext.getStart()),
                            variableDeclaratorIdContext.getText(), declCls)
            );
        }
        return null;
    }

    private void parseInitializer(VariableInitializerContext variableInitializer, Class<?> declCls) {
        ExpressionContext expression = variableInitializer.expression();
        if (expression != null) {
            expression.accept(this);
            return;
        }
        ArrayInitializerContext arrayInitializerContext = variableInitializer
                .arrayInitializer();
        newArrWithInitializers(declCls, arrayInitializerContext);
    }

    private void newArrWithInitializers(Class<?> componentClz,
                                        ArrayInitializerContext arrayInitializerContext) {
        VariableInitializerListContext variableInitializerListContext = arrayInitializerContext.variableInitializerList();
        List<VariableInitializerContext> initializerContexts = variableInitializerListContext == null ?
                Collections.emptyList() : variableInitializerListContext.variableInitializer();
        for (VariableInitializerContext variableInitializerContext : initializerContexts) {
            variableInitializerContext.accept(this);
        }
        addInstruction(new NewArrayInstruction(
                newReporterWithToken(arrayInitializerContext.getStart()), componentClz, initializerContexts.size()
        ));
    }

    @Override
    public Void visitLeftHandSide(LeftHandSideContext ctx) {
        VarIdContext idContext = ctx.varId();
        List<PathPartContext> pathPartContexts = ctx.pathPart();
        if (pathPartContexts.size() == 1 && pathPartContexts.get(0) instanceof CallExprContext) {
            CallExprContext callExprContext = (CallExprContext) pathPartContexts.get(0);
            ArgumentListContext argumentListContext = callExprContext.argumentList();
            visitCallFunction(idContext, argumentListContext);
        } else {
            int tailPartStart = parseIdHeadPart(idContext, pathPartContexts);
            for (int i = tailPartStart; i < pathPartContexts.size(); i++) {
                pathPartContexts.get(i).accept(this);
            }
        }
        return null;
    }

    private void visitCallFunction(VarIdContext functionNameContext, ArgumentListContext argumentListContext) {
        String functionName = functionNameContext.getText();
        CompileTimeFunction compileTimeFunction = compileTimeFunctions.get(functionName);
        if (compileTimeFunction != null) {
            ErrorReporter functionNameReporter = newReporterWithToken(functionNameContext.getStart());
            compileTimeFunction.createFunctionInstruction(
                    functionName, argumentListContext == null ?
                            Collections.emptyList() : argumentListContext.expression(),
                    operatorFactory, new CodeGenerator() {
                        @Override
                        public void addInstruction(QLInstruction qlInstruction) {
                            QvmInstructionVisitor.this.addInstruction(qlInstruction);
                        }

                        @Override
                        public void addInstructionsByTree(ParseTree tree) {
                            tree.accept(QvmInstructionVisitor.this);
                        }

                        @Override
                        public QLSyntaxException reportParseErr(String errCode, String errReason) {
                            return QvmInstructionVisitor.this.reportParseErr(functionNameContext.getStart(),
                                    errCode, errReason);
                        }

                        @Override
                        public QLambdaDefinition generateLambdaDefinition(ExpressionContext expressionContext, List<QLambdaDefinitionInner.Param> params) {
                            QvmInstructionVisitor subVisitor = parseExprBodyWithSubVisitor(expressionContext, generatorScope, context);
                            return new QLambdaDefinitionInner(functionName, subVisitor.getInstructions(), params, subVisitor.getMaxStackSize());
                        }

                        @Override
                        public ErrorReporter getErrorReporter() {
                            return functionNameReporter;
                        }

                        @Override
                        public ErrorReporter newReporterWithToken(Token token) {
                            return QvmInstructionVisitor.this.newReporterWithToken(token);
                        }
                    }

            );
            return;
        }

        if (argumentListContext != null) {
            argumentListContext.accept(this);
        }
        int argSize = argumentListContext == null ? 0 : argumentListContext.expression().size();
        addInstruction(
                new CallFunctionInstruction(newReporterWithToken(functionNameContext.getStart()), functionName, argSize)
        );
    }

    @Override
    public Void visitPrimary(PrimaryContext ctx) {
        PrimaryNoFixContext primaryNoFixContext = ctx.primaryNoFix();
        List<PathPartContext> pathPartContexts = ctx.pathPart();

        // path
        // head part
        int tailPartStart = parsePathHeadPart(primaryNoFixContext, pathPartContexts);

        // tail part
        for (int i = tailPartStart; i < pathPartContexts.size(); i++) {
            pathPartContexts.get(i).accept(this);
        }

        SuffixExpressContext suffixExpressContext = ctx.suffixExpress();
        if (suffixExpressContext != null) {
            String suffixOperator = suffixExpressContext.getText();
            UnaryOperator suffixUnaryOperator = operatorFactory.getSuffixUnaryOperator(suffixOperator);
            addInstruction(
                    new UnaryInstruction(
                            newReporterWithToken(suffixExpressContext.getStart()),
                            suffixUnaryOperator, suffixExpressContext.getStart().getStartIndex()
                    )
            );
        }

        PrefixExpressContext prefixExpressContext = ctx.prefixExpress();
        if (prefixExpressContext != null) {
            String prefixOperator = prefixExpressContext.getText();
            UnaryOperator prefixUnaryOperator = operatorFactory.getPrefixUnaryOperator(prefixOperator);
            addInstruction(
                    new UnaryInstruction(
                            newReporterWithToken(prefixExpressContext.getStart()),
                            prefixUnaryOperator, prefixExpressContext.getStart().getStartIndex()
                    )
            );
        }
        return null;
    }

    @Override
    public Void visitTypeExpr(TypeExprContext ctx) {
        Class<?> cls = BuiltInTypesSet.getCls(ctx.primitiveType().getText());
        addInstruction(new ConstInstruction(newReporterWithToken(ctx.getStart()), new MetaClass(cls), null));
        return null;
    }

    @Override
    public Void visitArrayInitializer(ArrayInitializerContext ctx) {
        return super.visitArrayInitializer(ctx);
    }

    @Override
    public Void visitConstExpr(ConstExprContext ctx) {
        ctx.literal().accept(this);
        return null;
    }

    @Override
    public Void visitContextSelectExpr(ContextSelectExprContext ctx) {
        String variableName = ctx.SelectorVariable_VANME().getText().trim();
        addInstruction(new LoadInstruction(newReporterWithToken(ctx.getStart()), variableName, ctx.getStart().getStartIndex()));
        return null;
    }

    @Override
    public Void visitLiteral(LiteralContext literal) {
        TerminalNode integerLiteral = literal.IntegerLiteral();
        if (integerLiteral != null) {
            try {
                Number intResult = parseInteger(remove(integerLiteral.getText(), '_'));
                addInstruction(new ConstInstruction(
                        newReporterWithToken(integerLiteral.getSymbol()), intResult,
                        integerLiteral.getSymbol().getStartIndex())
                );
            } catch (NumberFormatException nfe) {
                throw reportParseErr(integerLiteral.getSymbol(), QLErrorCodes.INVALID_NUMBER.name(), QLErrorCodes.INVALID_NUMBER.getErrorMsg());
            }
            return null;
        }
        TerminalNode floatingPointLiteral = literal.FloatingPointLiteral();
        if (floatingPointLiteral != null) {
            try {
                Number floatingResult = parseFloating(remove(floatingPointLiteral.getText(), '_'));
                addInstruction(new ConstInstruction(
                        newReporterWithToken(floatingPointLiteral.getSymbol()), floatingResult,
                        floatingPointLiteral.getSymbol().getStartIndex())
                );
            } catch (NumberFormatException nfe) {
                throw reportParseErr(floatingPointLiteral.getSymbol(), QLErrorCodes.INVALID_NUMBER.name(), QLErrorCodes.INVALID_NUMBER.getErrorMsg());
            }
            return null;
        }
        TerminalNode integerOrFloatingLiteral = literal.IntegerOrFloatingLiteral();
        if (integerOrFloatingLiteral != null) {
            try {
                String numberText = integerOrFloatingLiteral.getText();
                Number numberResult = numberText.contains(".")?
                        parseFloating(remove(numberText, '_')): parseInteger(remove(numberText, '_'));
                addInstruction(new ConstInstruction(
                        newReporterWithToken(integerOrFloatingLiteral.getSymbol()), numberResult,
                        integerOrFloatingLiteral.getSymbol().getStartIndex()
                    )
                );
            } catch (NumberFormatException nfe) {
                throw reportParseErr(integerOrFloatingLiteral.getSymbol(), QLErrorCodes.INVALID_NUMBER.name(), QLErrorCodes.INVALID_NUMBER.getErrorMsg());
            }
            return null;
        }
        BoolenLiteralContext booleanLiteral = literal.boolenLiteral();
        if (booleanLiteral != null) {
            boolean boolValue = Boolean.parseBoolean(booleanLiteral.getText());
            addInstruction(
                    new ConstInstruction(
                            newReporterWithToken(booleanLiteral.getStart()),
                            boolValue, booleanLiteral.getStart().getStartIndex()
                    )
            );
            return null;
        }
        TerminalNode quoteStringLiteral = literal.QuoteStringLiteral();
        if (quoteStringLiteral != null) {
            String escapedStr = parseStringEscape(quoteStringLiteral.getText());
            addInstruction(
                    new ConstInstruction(
                            newReporterWithToken(quoteStringLiteral.getSymbol()),
                            escapedStr, quoteStringLiteral.getSymbol().getStartIndex()
                    )
            );
            return null;
        }
        DoubleQuoteStringLiteralContext doubleQuoteStringLiteral = literal.doubleQuoteStringLiteral();
        if (doubleQuoteStringLiteral != null) {
            visitDoubleQuoteStringLiteral(doubleQuoteStringLiteral);
            return null;
        }
        TerminalNode nullLiteral = literal.NULL();
        if (nullLiteral != null) {
            addInstruction(
                    new ConstInstruction(
                            newReporterWithToken(nullLiteral.getSymbol()),
                            null, nullLiteral.getSymbol().getStartIndex()
                    )
            );
            return null;
        }
        return null;
    }

    @Override
    public Void visitDoubleQuoteStringLiteral(DoubleQuoteStringLiteralContext ctx) {
        if (initOptions.getInterpolationMode() == InterpolationMode.DISABLE) {
            TerminalNode characters = ctx.StaticStringCharacters();
            if (characters == null) {
                addInstruction(new ConstInstruction(newReporterWithToken(ctx.getStart()), "", null));
                return null;
            }
            String originText = characters.getText();
            addInstruction(new ConstInstruction(
                    newReporterWithToken(ctx.getStart()),
                    parseStringEscapeStartEnd(originText, 0, originText.length()), null
            ));
            return null;
        }
        int childCount = ctx.getChildCount();
        for (int i = 1; i < childCount - 1; i++) {
            ParseTree child = ctx.getChild(i);
            if (child instanceof StringExpressionContext) {
                StringExpressionContext stringExpression = (StringExpressionContext) child;
                ExpressionContext expression = stringExpression.expression();
                if (expression != null) {
                    // SCRIPT
                    visitExpression(expression);
                } else {
                    // VARIABLE
                    TerminalNode varTerminalNode = stringExpression.SelectorVariable_VANME();
                    String varName = varTerminalNode.getText().trim();
                    addInstruction(new LoadInstruction(newReporterWithToken(varTerminalNode.getSymbol()), varName, null));
                }
            } else if (child instanceof TerminalNode) {
                TerminalNode terminalNode = (TerminalNode) child;
                String originStr = terminalNode.getText();
                addInstruction(
                    new ConstInstruction(
                        newReporterWithToken(terminalNode.getSymbol()),
                        parseStringEscapeStartEnd(originStr, 0, originStr.length()),
                        ctx.getStart().getStartIndex()
                    )
                );
            }
        }
        addInstruction(new StringJoinInstruction(newReporterWithToken(ctx.getStart()), childCount - 2));
        return null;
    }

    private String parseStringEscape(String originStr) {
        return parseStringEscapeStartEnd(originStr, 1, originStr.length() - 1);
    }

    private String parseStringEscapeStartEnd(String originStr, int start, int end) {
        StringBuilder result = new StringBuilder();
        final byte init = 0;
        final byte escape = 1;
        byte state = 0;

        int i = start;
        while (i < end) {
            char cur = originStr.charAt(i++);
            switch (state) {
                case init:
                    if (cur == '\\') {
                        state = escape;
                    } else {
                        result.append(cur);
                    }
                    break;
                case escape:
                    state = init;
                    switch (cur) {
                        case 'b':
                            result.append('\b');
                            break;
                        case 't':
                            result.append('\t');
                            break;
                        case 'n':
                            result.append('\n');
                            break;
                        case 'f':
                            result.append('\f');
                            break;
                        case 'r':
                            result.append('\r');
                            break;
                        case '"':
                            result.append('"');
                            break;
                        case '\'':
                            result.append('\'');
                            break;
                        case '\\':
                            result.append('\\');
                            break;
                        case '$':
                            result.append('$');
                            break;
                    }
                    break;
            }
        }
        return result.toString();
    }

    private Number parseFloating(String floatingText) {
        char floatingTypeFlag = floatingText.charAt(floatingText.length() - 1);
        switch (floatingTypeFlag) {
            case 'f':
            case 'F':
                return new BigDecimal(floatingText.substring(0, floatingText.length() - 1)).floatValue();
            case 'd':
            case 'D':
                return new BigDecimal(floatingText.substring(0, floatingText.length() - 1)).doubleValue();
            default:
                BigDecimal baseDecimal = new BigDecimal(floatingText);
                return baseDecimal.compareTo(MAX_DOUBLE) <= 0 ? maybePresentWithDouble(baseDecimal): baseDecimal;
        }
    }

    private Number maybePresentWithDouble(BigDecimal origin) {
        double doubleValue = origin.doubleValue();
        BigDecimal reference = new BigDecimal(doubleValue);
        return reference.compareTo(origin) == 0? doubleValue: origin;
    }

    private Number parseInteger(String intText) {
        char intTypeFlag = intText.charAt(intText.length() - 1);
        switch (intTypeFlag) {
            case 'l':
            case 'L':
                String baseIntText = intText.substring(0, intText.length() - 1);
                BigInteger baseInt = parseBaseInteger(baseIntText);
                return baseInt.longValue();
            default:
                // auto type
                baseInt = parseBaseInteger(intText);
                if (baseInt.compareTo(MAX_INTEGER) <= 0) {
                    return baseInt.intValue();
                } else if (baseInt.compareTo(MAX_LONG) <= 0) {
                    return baseInt.longValue();
                } else {
                    return baseInt;
                }
        }
    }

    private BigInteger parseBaseInteger(String intText) {
        String radixPrefix = subString(intText, 2);
        switch (radixPrefix) {
            case "0x":
            case "0X":
                // radix 16
                return new BigInteger(intText.substring(2), 16);
            case "0b":
            case "0B":
                // radix 2
                return new BigInteger(intText.substring(2), 2);
            default:
                if (radixPrefix.startsWith("0")) {
                    // radix 8
                    return new BigInteger(intText, 8);
                } else {
                    // radix 10
                    return new BigInteger(intText);
                }
        }
    }

    private Class<?> parseDeclTypeNoArr(DeclTypeNoArrContext declTypeNoArrContext) {
        PrimitiveTypeContext primitiveTypeContext = declTypeNoArrContext.primitiveType();
        if (primitiveTypeContext != null) {
            return BuiltInTypesSet.getCls(primitiveTypeContext.getText());
        }

        ClsTypeContext clsTypeContext = declTypeNoArrContext.clsType();
        return parseClsIds(clsTypeContext.varId());
    }

    private Class<?> parseDeclType(DeclTypeContext declTypeContext) {
        Class<?> baseCls = parseDeclBaseCls(declTypeContext);
        DimsContext dims = declTypeContext.dims();
        int layers = dims == null ? 0 : dims.LBRACK().size();
        return wrapInArray(baseCls, layers);
    }

    private Class<?> parseDeclBaseCls(DeclTypeContext declTypeContext) {
        PrimitiveTypeContext primitiveTypeContext = declTypeContext.primitiveType();
        if (primitiveTypeContext != null) {
            return BuiltInTypesSet.getCls(primitiveTypeContext.getText());
        }
        ClsTypeContext clsTypeContext = declTypeContext.clsType();
        return parseClsIds(clsTypeContext.varId());
    }

    private String remove(String target, char c) {
        StringBuilder builder = new StringBuilder(target.length());
        for (int i = 0; i < target.length(); i++) {
            char iChar = target.charAt(i);
            if (iChar != c) {
                builder.append(iChar);
            }
        }
        return builder.toString();
    }

    private String subString(String target, int end) {
        if (end > target.length()) {
            return target;
        }
        return target.substring(0, end);
    }

    private Class<?> wrapInArray(Class<?> baseType, int layers) {
        for (int i = 0; i < layers; i++) {
            baseType = Array.newInstance(baseType, 0).getClass();
        }
        return baseType;
    }

    private Class<?> parseClsIds(List<VarIdContext> varIdContexts) {
        List<String> fieldIds = varIdContexts.stream()
                .map(RuleContext::getText)
                .collect(Collectors.toList());
        ImportManager.LoadPartQualifiedResult loadPartQualifiedResult = importManager.loadPartQualified(fieldIds);
        if (loadPartQualifiedResult.getCls() == null || loadPartQualifiedResult.getRestIndex() != fieldIds.size()) {
            Token lastIdToken = varIdContexts.get(varIdContexts.size() - 1).getStart();
            throw reportParseErr(lastIdToken, QLErrorCodes.CLASS_NOT_FOUND.name(),
                    String.format(QLErrorCodes.CLASS_NOT_FOUND.getErrorMsg(), String.join(".", fieldIds))
            );
        }
        return loadPartQualifiedResult.getCls();
    }

    private QLSyntaxException reportParseErr(Token token, String errCode, String errReason) {
        return QLException.reportScannerErr(script, token.getStartIndex(),
                token.getLine(), token.getCharPositionInLine(),
                token.getText(), errCode, errReason);
    }

    public List<QLInstruction> getInstructions() {
        return instructionList;
    }

    public int getMaxStackSize() {
        return maxStackSize;
    }

    private void ifElseInstructions(ErrorReporter conditionReporter, List<QLInstruction> thenInstructions, Integer thenTraceKey,
                                    List<QLInstruction> elseInstructions, Integer elseTraceKey, int traceKey) {
        JumpIfPopInstruction jumpIf = new JumpIfPopInstruction(conditionReporter, false, -1);
        pureAddInstruction(jumpIf);
        int jumpStart = instructionList.size();
        thenInstructions.forEach(this::pureAddInstruction);
        if (initOptions.isTraceExpression()) {
            if (thenTraceKey != null) {
                pureAddInstruction(new TracePeekInstruction(conditionReporter, thenTraceKey));
            }
            pureAddInstruction(new TracePeekInstruction(conditionReporter, traceKey));
        }
        addTimeoutInstruction();

        JumpInstruction jump = new JumpInstruction(conditionReporter, -1);
        pureAddInstruction(jump);

        jumpIf.setPosition(instructionList.size() - jumpStart);

        jumpStart = instructionList.size();
        elseInstructions.forEach(this::pureAddInstruction);
        if (initOptions.isTraceExpression()) {
            if (elseTraceKey != null) {
                pureAddInstruction(new TracePeekInstruction(conditionReporter, elseTraceKey));
            }
            pureAddInstruction(new TracePeekInstruction(conditionReporter, traceKey));
        }
        addTimeoutInstruction();
        jump.setPosition(instructionList.size() - jumpStart);
    }

    private void jumpRightIfExpect(boolean expect, ErrorReporter opErrReporter, RuleContext right, String operatorId, int traceKey) {
        QvmInstructionVisitor rightVisitor = parseWithSubVisitor(right, generatorScope, Context.MACRO);
        List<QLInstruction> rightInstructions = rightVisitor.getInstructions();

        JumpIfInstruction jumpIf = new JumpIfInstruction(opErrReporter, expect, -1, traceKey);
        pureAddInstruction(jumpIf);

        int jumpStart = instructionList.size();

        rightInstructions.forEach(this::pureAddInstruction);
        BinaryOperator binaryOperator = operatorFactory.getBinaryOperator(operatorId);
        addInstruction(new OperatorInstruction(opErrReporter, binaryOperator, traceKey));
        addTimeoutInstruction();

        jumpIf.setPosition(instructionList.size() - jumpStart);
    }

    private QvmInstructionVisitor parseWithSubVisitor(RuleContext ruleContext, GeneratorScope generatorScope,
                                                      Context context) {
        QvmInstructionVisitor subVisitor = new QvmInstructionVisitor(script, importManager,
                generatorScope, operatorFactory, context, compileTimeFunctions, initOptions);
        ruleContext.accept(subVisitor);
        return subVisitor;
    }

    private QvmInstructionVisitor parseExprBodyWithSubVisitor(ExpressionContext expressionContext,
                                                              GeneratorScope generatorScope, Context context) {
        QvmInstructionVisitor subVisitor = new QvmInstructionVisitor(script, importManager,
                generatorScope, operatorFactory, context, compileTimeFunctions, initOptions);
        // reduce the level of syntax tree when expression is a block
        subVisitor.visitBodyExpression(expressionContext);
        return subVisitor;
    }

    private boolean handleStmt(BlockStatementContext statementContext) {
        if (maybeMacroCall(statementContext)) {
            String macroName = statementContext.getStart().getText();
            MacroDefine macroDefine = generatorScope.getMacroInstructions(macroName);
            if (macroDefine != null) {
                macroDefine.getMacroInstructions().forEach(this::pureAddInstruction);
                addTimeoutInstruction();
                return macroDefine.isLastStmtExpress();
            }
        }
        statementContext.accept(this);
        return statementContext instanceof ExpressionStatementContext;
    }

    private boolean maybeMacroCall(BlockStatementContext statementContext) {
        if (statementContext instanceof ExpressionStatementContext) {
            ExpressionContext expressionContext = ((ExpressionStatementContext) statementContext).expression();
            if (expressionContext != null) {
                if (expressionContext.getStart() == expressionContext.getStop()) {
                    return expressionContext.getStart().getType() == QLexer.ID;
                }
            }
        }
        return false;
    }

    private void pureAddInstruction(QLInstruction qlInstruction) {
        int stackExpandSize = qlInstruction.stackOutput() - qlInstruction.stackInput();
        expandStackSize(stackExpandSize);
        instructionList.add(qlInstruction);
    }

    private void addInstruction(QLInstruction qlInstruction) {
        if (instructionList.size() - timeoutCheckPoint > TIMEOUT_CHECK_GAP) {
            addTimeoutInstruction();
        }
        pureAddInstruction(qlInstruction);
        if (qlInstruction instanceof MethodInvokeInstruction || qlInstruction instanceof CallFunctionInstruction ||
                qlInstruction instanceof CallConstInstruction || qlInstruction instanceof CallInstruction) {
            addTimeoutInstruction();
        }
    }

    private void addTimeoutInstruction() {
        QLInstruction lastInstruction = instructionList.get(instructionList.size() - 1);
        if (lastInstruction instanceof CheckTimeOutInstruction) {
            return;
        }
        this.timeoutCheckPoint = instructionList.size();
        instructionList.add(new CheckTimeOutInstruction(lastInstruction.getErrorReporter()));
    }

    private void expandStackSize(int stackExpandSize) {
        stackSize += stackExpandSize;
        if (stackSize > maxStackSize) {
            maxStackSize = stackSize;
        }
    }

    private ErrorReporter newReporterWithToken(Token token) {
        return new DefaultErrReporter(script, token.getStartIndex(),
                token.getLine(), token.getCharPositionInLine(), token.getText());
    }

    private int whileCount() {
        return whileCounter++;
    }

    private int forCount() {
        return forCounter++;
    }

    private int ifCount() {
        return ifCounter++;
    }

    private int tryCount() {
        return tryCounter++;
    }

    private String blockScopeName() {
        return generatorScope.getName() + SCOPE_SEPARATOR + BLOCK_LAMBDA_NAME_PREFIX + blockCounter++;
    }

    private String macroScopeName() {
        return generatorScope.getName() + SCOPE_SEPARATOR + MACRO_PREFIX + macroCounter++;
    }

    private String lambdaScopeName() {
        return generatorScope.getName() + SCOPE_SEPARATOR + LAMBDA_PREFIX + lambdaCounter++;
    }

}
