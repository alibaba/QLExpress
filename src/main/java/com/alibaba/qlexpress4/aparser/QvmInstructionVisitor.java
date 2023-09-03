package com.alibaba.qlexpress4.aparser;

import com.alibaba.qlexpress4.DefaultClassSupplier;
import com.alibaba.qlexpress4.exception.DefaultErrReporter;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.exception.QLException;
import com.alibaba.qlexpress4.exception.QLSyntaxException;
import com.alibaba.qlexpress4.runtime.MetaClass;
import com.alibaba.qlexpress4.runtime.QLambdaDefinition;
import com.alibaba.qlexpress4.runtime.QLambdaDefinitionInner;
import com.alibaba.qlexpress4.runtime.QResult;
import com.alibaba.qlexpress4.runtime.instruction.*;
import com.alibaba.qlexpress4.runtime.operator.BinaryOperator;
import com.alibaba.qlexpress4.runtime.operator.OperatorManager;
import com.alibaba.qlexpress4.runtime.operator.unary.UnaryOperator;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.TerminalNode;


import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

import static com.alibaba.qlexpress4.aparser.QLGrammarParser.*;

/**
 * Author: DQinYuan
 */
public class QvmInstructionVisitor extends QLGrammarBaseVisitor<Void> {
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

     enum Context {
        BLOCK, MACRO
    }

    private final String script;

    private final ImportManager importManager;

    private final GeneratorScope generatorScope;

    private final OperatorFactory operatorFactory;

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

    /**
     * main
     */
    public QvmInstructionVisitor(String script, ImportManager importManager,
                                 OperatorFactory operatorFactory) {
        this.script = script;
        this.importManager = importManager;
        this.generatorScope = new GeneratorScope("main", null);
        this.operatorFactory = operatorFactory;
        this.context = Context.BLOCK;
    }

    /**
     * recursion
     */
    public QvmInstructionVisitor(String script, ImportManager importManager,
                                 GeneratorScope generatorScope, OperatorFactory operatorFactory,
                                 Context context) {
        this.script = script;
        this.importManager = importManager;
        this.generatorScope = generatorScope;
        this.operatorFactory = operatorFactory;
        this.context = context;
    }

    public QvmInstructionVisitor(String script) {
        this.script = script;
        this.importManager = new ImportManager(DefaultClassSupplier.getInstance(),
                new ArrayList<>(), new HashMap<>());
        this.generatorScope = new GeneratorScope("MAIN", null);
        this.operatorFactory = new OperatorManager();
        this.context = Context.BLOCK;
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
        importManager.addImport(isInnerCls? ImportManager.importInnerCls(importPath):
                ImportManager.importPack(importPath));
        return null;
    }

    @Override
    public Void visitBlockStatements(BlockStatementsContext blockStatementsContext) {
        BlockStatementContext preStatement = null;
        int childCount = blockStatementsContext.getChildCount();
        for (int i = 0; i < childCount; i++) {
            if (preStatement instanceof ExpressionStatementContext) {
                // pop if expression without acceptor
                addInstruction(new PopInstruction(
                        newReporterWithToken(preStatement.getStart())
                    )
                );
            }
            BlockStatementContext child = blockStatementsContext.getChild(BlockStatementContext.class, i);
            preStatement = handleStmt(child);
        }

        if (context == Context.BLOCK) {
            if (preStatement instanceof ExpressionStatementContext) {
                addInstruction(new ReturnInstruction(
                        newReporterWithToken(preStatement.getStart()),
                        QResult.ResultType.RETURN)
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
                newReporterWithToken(expressionContext.getStart()), QResult.ResultType.RETURN));
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
        return primaryNoFixContext instanceof BlockExprContext? (BlockExprContext) primaryNoFixContext : null;
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
        addInstruction(new OperatorInstruction(newReporterWithToken(assignOperatorContext.getStart()), assignOperator));
        return null;
    }

    @Override
    public Void visitTraditionalForStatement(TraditionalForStatementContext ctx) {
        int forCount = forCount();
        ErrorReporter forErrReporter = newReporterWithToken(ctx.FOR().getSymbol());

        // for init
        ForInitContext forInitContext = ctx.forInit();
        QLambdaDefinitionInner forInitLambda = forInitContext != null?
                generateForInitLambda(forCount, forInitContext): null;

        // condition
        ExpressionContext forConditionContext = ctx.forCondition;
        QLambdaDefinitionInner forConditionLambda = forConditionContext != null?
                generateForExpressLambda(forCount, CONDITION_SUFFIX, forConditionContext): null;

        // for update
        ExpressionContext forUpdateContext = ctx.forUpdate;
        QLambdaDefinitionInner forUpdateLambda = forUpdateContext != null?
                generateForExpressLambda(forCount, UPDATE_SUFFIX, forUpdateContext): null;

        // for body
        BlockStatementContext forBodyContext = ctx.blockStatement();
        QLambdaDefinitionInner forBodyLambda = forBodyContext != null? generateForBodyLambda(forCount, forBodyContext): null;

        int forInitSize = forInitLambda == null? 0: forInitLambda.getMaxStackSize();
        int forConditionSize = forConditionLambda == null? 0: forConditionLambda.getMaxStackSize();
        int forUpdateSize = forUpdateLambda == null? 0: forUpdateLambda.getMaxStackSize();
        int forScopeMaxStackSize = Math.max(forInitSize, Math.max(forConditionSize, forUpdateSize));
        addInstruction(new ForInstruction(forErrReporter, forInitLambda, forConditionLambda,
                forConditionContext != null? newReporterWithToken(forConditionContext.getStart()): null,
                forUpdateLambda, forScopeMaxStackSize,
                forBodyLambda));
        return null;
    }

    private QLambdaDefinitionInner generateForBodyLambda(int forCount, BlockStatementContext forBodyContext) {
        if (forBodyContext instanceof ExpressionStatementContext) {
            ExpressionStatementContext exprForBodyContext = (ExpressionStatementContext) forBodyContext;
            return generateForExpressLambda(forCount, BODY_SUFFIX, exprForBodyContext.expression());
        } else {
            String scopeName = generatorScope.getName() + SCOPE_SEPARATOR + FOR_PREFIX + forCount + BODY_SUFFIX;
            QvmInstructionVisitor subVisitor = parseWithSubVisitor(forBodyContext,
                    new GeneratorScope(scopeName, generatorScope), Context.MACRO);
            return new QLambdaDefinitionInner(scopeName, subVisitor.getInstructions(),
                    Collections.emptyList(),subVisitor.getMaxStackSize());
        }
    }

    private QLambdaDefinitionInner generateForInitLambda(int forCount, ForInitContext forInitContext) {
        if (forInitContext.localVariableDeclaration() != null) {
            String scopeName = generatorScope.getName() + SCOPE_SEPARATOR + FOR_PREFIX + forCount + INIT_SUFFIX;
            QvmInstructionVisitor subVisitor = parseWithSubVisitor(forInitContext.localVariableDeclaration(),
                    new GeneratorScope(scopeName, generatorScope), Context.MACRO);
            return new QLambdaDefinitionInner(scopeName, subVisitor.getInstructions(),
                    Collections.emptyList(),subVisitor.getMaxStackSize());
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
                Collections.emptyList(),subVisitor.getMaxStackSize());
    }

    @Override
    public Void visitForEachStatement(ForEachStatementContext ctx) {
        ExpressionContext targetExprContext = ctx.expression();
        targetExprContext.accept(this);

        DeclTypeContext declTypeContext = ctx.declType();
        Class<?> itVarCls = declTypeContext == null? Object.class: parseDeclType(declTypeContext);

        String forEachBodyScopeName = generatorScope.getName() + SCOPE_SEPARATOR + FOR_PREFIX + forCount() + BODY_SUFFIX;
        QvmInstructionVisitor forEachBodyVisitor = parseWithSubVisitor(ctx.blockStatement(), new GeneratorScope(forEachBodyScopeName, generatorScope),
                Context.MACRO);
        QLambdaDefinition bodyDefinition = new QLambdaDefinitionInner(forEachBodyScopeName, forEachBodyVisitor.getInstructions(),
                Collections.singletonList(
                        new QLambdaDefinitionInner.Param(ctx.varId().getText(), itVarCls)
                ), forEachBodyVisitor.getMaxStackSize());
        addInstruction(new ForEachInstruction(
                newReporterWithToken(ctx.FOR().getSymbol()), bodyDefinition,
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

        QLambdaDefinitionInner whileBodyLambda = generateWhileBody(whileCount, ctx.blockStatement());

        addInstruction(new WhileInstruction(
                newReporterWithToken(ctx.WHILE().getSymbol()), conditionLambda, whileBodyLambda,
                Math.max(conditionLambda.getMaxStackSize(), whileBodyLambda.getMaxStackSize())
        ));
        return null;
    }

    private QLambdaDefinitionInner generateWhileBody(int whileCount, BlockStatementContext blockStatementContext) {
        String scopeName = generatorScope.getName() + SCOPE_SEPARATOR + WHILE_PREFIX + whileCount + BODY_SUFFIX;
        if (blockStatementContext instanceof ExpressionStatementContext) {
            ExpressionStatementContext expressionStatementContext = (ExpressionStatementContext) blockStatementContext;
            QvmInstructionVisitor subVisitor = parseExprBodyWithSubVisitor(expressionStatementContext.expression(),
                    new GeneratorScope(scopeName, generatorScope), Context.BLOCK);
            return new QLambdaDefinitionInner(scopeName, subVisitor.getInstructions(),
                    Collections.emptyList(), subVisitor.getMaxStackSize());
        } else {
            QvmInstructionVisitor subVisitor = parseWithSubVisitor(blockStatementContext,
                    new GeneratorScope(scopeName, generatorScope), Context.MACRO);
            return new QLambdaDefinitionInner(scopeName, subVisitor.getInstructions(),
                    Collections.emptyList(), subVisitor.getMaxStackSize());
        }
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
            addInstruction(new ConstInstruction(errorReporter, null));
        } else {
            expression.accept(this);
        }

        addInstruction(new ReturnInstruction(errorReporter, QResult.ResultType.RETURN));
        return null;
    }

    @Override
    public Void visitFunctionStatement(FunctionStatementContext ctx) {
        FormalOrInferredParameterListContext formalOrInferredParameterList = ctx.formalOrInferredParameterList();
        List<QLambdaDefinitionInner.Param> params = formalOrInferredParameterList == null?
                Collections.emptyList():
                parseFormalOrInferredParameterList(formalOrInferredParameterList);
        VarIdContext functionNameCtx = ctx.varId();
        QLambdaDefinition functionDefinition = parseFunctionDefinition(functionNameCtx.getText(), ctx, params);

        ErrorReporter errorReporter = newReporterWithToken(functionNameCtx.getStart());
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
        addInstruction(new ConstInstruction(errorReporter, castCls));
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
            ifElseInstructions(newReporterWithToken(ctx.QUESTION().getSymbol()),
                    thenVisitor.getInstructions(), elseVisitor.getInstructions());
        }

        return null;
    }

    @Override
    public Void visitBlockExpr(BlockExprContext ctx) {
        ErrorReporter blockErrReporter = newReporterWithToken(ctx.getStart());
        BlockStatementsContext blockStatementsContext = ctx.blockStatements();
        if (blockStatementsContext == null) {
            addInstruction(new ConstInstruction(blockErrReporter, null));
            return null;
        }

        String blockScopeName = blockScopeName();
        QvmInstructionVisitor blockSubVisitor = parseWithSubVisitor(blockStatementsContext,
                new GeneratorScope(blockScopeName, generatorScope), Context.MACRO);

        addInstruction(new NewScopeInstruction(blockErrReporter, blockScopeName));
        blockSubVisitor.getInstructions().forEach(this::addInstruction);
        addInstruction(new CloseScopeInstruction(blockErrReporter, blockScopeName));
        return null;
    }

    @Override
    public Void visitIfExpr(IfExprContext ctx) {
        ctx.condition.accept(this);

        int ifCount = ifCount();
        ErrorReporter ifErrorReporter = newReporterWithToken(ctx.IF().getSymbol());
        String ifScopeName = generatorScope.getName() + SCOPE_SEPARATOR + IF_PREFIX + ifCount;
        addInstruction(new NewScopeInstruction(ifErrorReporter, ifScopeName));

        String thenScopeName = generatorScope.getName() + SCOPE_SEPARATOR + IF_PREFIX + ifCount + THEN_SUFFIX;
        List<QLInstruction> thenInstructions = parseWithSubVisitor(ctx.thenBody,
                new GeneratorScope(thenScopeName, generatorScope), Context.MACRO).getInstructions();
        String elseScopeName = generatorScope.getName() + SCOPE_SEPARATOR + IF_PREFIX + ifCount + ELSE_SUFFIX;
        List<QLInstruction> elseInstructions = ctx.elseBody == null?
                Collections.singletonList(new ConstInstruction(ifErrorReporter, null)):
                parseWithSubVisitor(ctx.elseBody, new GeneratorScope(elseScopeName, generatorScope),
                        Context.MACRO).getInstructions();
        ifElseInstructions(ifErrorReporter, thenInstructions, elseInstructions);

        addInstruction(new CloseScopeInstruction(ifErrorReporter, ifScopeName));
        return null;
    }

    @Override
    public Void visitBreakContinueStatement(BreakContinueStatementContext ctx) {
        TerminalNode aBreak = ctx.BREAK();
        QResult qResult = aBreak == null? QResult.LOOP_CONTINUE_RESULT: QResult.LOOP_BREAK_RESULT;
        addInstruction(
                new BreakContinueInstruction(newReporterWithToken(ctx.getStart()), qResult)
        );
        return null;
    }

    @Override
    public Void visitListExpr(ListExprContext ctx) {
        ListItemsContext listItemsContext = ctx.listItems();
        ErrorReporter listErrorReporter = newReporterWithToken(ctx.getStart());
        if (listItemsContext == null) {
            addInstruction(new ConstInstruction(listErrorReporter, new ArrayList<>()));
            return null;
        }
        List<ExpressionContext> expressions = listItemsContext.expression();
        for (ExpressionContext expression : expressions) {
            expression.accept(this);
        }
        addInstruction(new NewListInstruction(listErrorReporter, expressions.size()));
        return null;
    }

    @Override
    public Void visitMapExpr(MapExprContext ctx) {
        MapEntriesContext mapEntriesContext = ctx.mapEntries();
        List<MapEntryContext> mapEntryContexts = mapEntriesContext.mapEntry();
        List<String> keys = new ArrayList<>(mapEntryContexts.size());
        for (MapEntryContext mapEntryContext : mapEntryContexts) {
            keys.add(parseMsgKey(mapEntryContext.mapKey()));
            mapEntryContext.expression().accept(this);
        }
        addInstruction(new NewMapInstruction(newReporterWithToken(ctx.getStart()), keys));
        return null;
    }

    private String parseMsgKey(MapKeyContext mapKeyContext) {
        if (mapKeyContext instanceof IdKeyContext) {
            return mapKeyContext.getText();
        } else if (mapKeyContext instanceof StringKeyContext) {
            return parseStringEscape(mapKeyContext.getText());
        } else if (mapKeyContext instanceof RawStringKeyContext) {
            return parseRawStringEscape(mapKeyContext.getText());
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
        int argNum = argumentListContext == null? 0: argumentListContext.expression().size();
        addInstruction(new NewInstruction(newReporterWithToken(ctx.NEW().getSymbol()), newCls, argNum));
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
        ExpressionContext lambdaBodyExpr = ctx.expression();
        QvmInstructionVisitor lambdaSubVisitor = parseExprBodyWithSubVisitor(lambdaBodyExpr,
                new GeneratorScope(lambdaScopeName, generatorScope), Context.BLOCK);
        QLambdaDefinition lambdaDefinition = new QLambdaDefinitionInner(lambdaScopeName,
                lambdaSubVisitor.getInstructions(), lambdaParams, lambdaSubVisitor.getMaxStackSize());
        addInstruction(new LoadLambdaInstruction(
                newReporterWithToken(ctx.ARROW().getSymbol()), lambdaDefinition
        ));
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
        Class<?> paramCls = declTypeContext == null? Object.class: parseDeclType(declTypeContext);
        return new QLambdaDefinitionInner.Param(paramName, paramCls);
    }

    @Override
    public Void visitTryCatchExpr(TryCatchExprContext ctx) {
        BlockStatementsContext blockStatementsContext = ctx.blockStatements();
        if (blockStatementsContext == null) {
            addInstruction(new ConstInstruction(
                    newReporterWithToken(ctx.TRY().getSymbol()),
                    null
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
            QvmInstructionVisitor catchSubVisitor = parseWithSubVisitor(tryCatchContext.blockStatements(),
                    new GeneratorScope(catchBodyName, generatorScope), Context.BLOCK);

            List<DeclTypeContext> catchDeclTypes = catchParamsContext.declType();
            if (catchDeclTypes.isEmpty()) {
                QLambdaDefinitionInner.Param param = new QLambdaDefinitionInner.Param(eName, Object.class);
                QLambdaDefinition exceptionHandlerDefinition = new QLambdaDefinitionInner(
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
                QLambdaDefinition exceptionHandlerDefinition = new QLambdaDefinitionInner(
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
        addInstruction(new LoadInstruction(newReporterWithToken(ctx.getStart()), ctx.varId().getText()));
        return null;
    }

    private int parsePathHeadPart(PrimaryNoFixContext primaryNoFixContext, List<PathPartContext> pathPartContexts) {
        if (primaryNoFixContext instanceof TypeExprContext) {
            Class<?> cls = BuiltInTypesSet.getCls(primaryNoFixContext.getStart().getText());
            int dimPartNum = parseDimParts(0, pathPartContexts);
            addInstruction(new ConstInstruction(
                    newReporterWithToken(primaryNoFixContext.getStart()),
                    new MetaClass(dimPartNum > 0? wrapInArray(cls, dimPartNum): cls))
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
            if (argumentListContext != null) {
                argumentListContext.accept(this);
            }
            int argSize = argumentListContext == null? 0: argumentListContext.expression().size();
            String functionName = idContext.getText();
            addInstruction(
                    new CallFunctionInstruction(newReporterWithToken(idContext.getStart()), functionName, argSize)
            );
            return 1;
        }
        List<String> headPartIds = new ArrayList<>();
        headPartIds.add(idContext.getText());
        for (PathPartContext pathPartContext : pathPartContexts) {
            if (pathPartContext instanceof FieldAccessContext) {
                headPartIds.add(pathPartContext.getStop().getText());
            } else {
                break;
            }
        }
        ImportManager.LoadQualifiedResult loadQualifiedResult = importManager.loadQualified(headPartIds);
        if (loadQualifiedResult.getCls() != null) {
            int restIndex = loadQualifiedResult.getRestIndex() - 1;
            Token clsReportToken = restIndex == 0? idContext.getStart(): pathPartContexts.get(restIndex - 1).getStop();
            int dimPartNum = parseDimParts(restIndex, pathPartContexts);
            Class<?> cls = dimPartNum > 0? wrapInArray(loadQualifiedResult.getCls(), dimPartNum):
                    loadQualifiedResult.getCls();

            addInstruction(new ConstInstruction(newReporterWithToken(clsReportToken), new MetaClass(cls)));
            return restIndex + dimPartNum;
        } else {
            addInstruction(new LoadInstruction(newReporterWithToken(idContext.getStart()), idContext.getText()));
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
        return indexExprContext.expression() == null;
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
                newReporterWithToken(fieldNameToken), fieldNameToken.getText(), false
        ));
        return null;
    }

    @Override
    public Void visitOptionalFieldAccess(OptionalFieldAccessContext ctx) {
        Token fieldNameToken = ctx.getStop();
        addInstruction(new GetFieldInstruction(
                newReporterWithToken(fieldNameToken), fieldNameToken.getText(), true
        ));
        return null;
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
        int argNum = argumentListContext == null? 0: argumentListContext.expression().size();
        addInstruction(new CallInstruction(newReporterWithToken(ctx.getStart()), argNum));
        return null;
    }

    @Override
    public Void visitIndexExpr(IndexExprContext ctx) {
        ExpressionContext indexExpression = ctx.expression();
        if (indexExpression == null) {
            throw reportParseErr(ctx.getStop(), "MISSING_INDEX", "missing index expression");
        }
        indexExpression.accept(this);
        addInstruction(new IndexInstruction(newReporterWithToken(ctx.getStart())));
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
            jumpRightIfExpect(false, opErrReporter, rightExpr);
        } else if ("||".equals(operatorId)) {
            jumpRightIfExpect(true, opErrReporter, rightExpr);
        } else {
            rightExpr.accept(this);
        }

        BinaryOperator binaryOperator = operatorFactory.getBinaryOperator(operatorId);
        addInstruction(new OperatorInstruction(opErrReporter, binaryOperator));
        return null;
    }

    @Override
    public Void visitMacroStatement(MacroStatementContext ctx) {
        String macroId = ctx.varId().getText();

        BlockStatementsContext macroBlockStatementsContext = ctx.blockStatements();

        BlockStatementContext lastStmt = getMacroLastStmt(ctx, macroBlockStatementsContext);
        generatorScope.defineMacro(macroId, new MacroDefine(
                getMacroInstructions(macroBlockStatementsContext), lastStmt));
        return null;
    }

    private BlockStatementContext getMacroLastStmt(MacroStatementContext macroCtx,
                                                   BlockStatementsContext macroBlockStatementsCtx) {
        if (macroBlockStatementsCtx == null) {
            return macroCtx;
        }
        List<BlockStatementContext> blockStatementContexts = macroBlockStatementsCtx.blockStatement();
        return blockStatementContexts.isEmpty()? macroCtx: blockStatementContexts.get(blockStatementContexts.size() - 1);
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
        Class<?> declCls = parseDeclType(ctx.declType());
        List<VariableDeclaratorContext> variableDeclaratorContexts = ctx.variableDeclaratorList().variableDeclarator();
        for (VariableDeclaratorContext variableDeclarator : variableDeclaratorContexts) {
            VariableInitializerContext variableInitializer = variableDeclarator.variableInitializer();
            if (variableInitializer == null) {
                addInstruction(
                        new ConstInstruction(newReporterWithToken(variableDeclarator.getStop()), null)
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
        List<VariableInitializerContext> initializerContexts = variableInitializerListContext == null?
                Collections.emptyList(): variableInitializerListContext.variableInitializer();
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
            if (argumentListContext != null) {
                argumentListContext.accept(this);
            }
            int argSize = argumentListContext == null? 0: argumentListContext.expression().size();
            String functionName = idContext.getText();
            addInstruction(
                    new CallFunctionInstruction(newReporterWithToken(idContext.getStart()), functionName, argSize)
            );
        } else {
            int tailPartStart = parseIdHeadPart(idContext, pathPartContexts);
            for (int i = tailPartStart; i < pathPartContexts.size(); i++) {
                pathPartContexts.get(i).accept(this);
            }
        }
        return null;
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
                    new UnaryInstruction(newReporterWithToken(suffixExpressContext.getStart()), suffixUnaryOperator)
            );
        }

        PrefixExpressContext prefixExpressContext = ctx.prefixExpress();
        if (prefixExpressContext != null) {
            String prefixOperator = prefixExpressContext.getText();
            UnaryOperator prefixUnaryOperator = operatorFactory.getPrefixUnaryOperator(prefixOperator);
            addInstruction(
                    new UnaryInstruction(newReporterWithToken(prefixExpressContext.getStart()), prefixUnaryOperator)
            );
        }
        return null;
    }

    @Override
    public Void visitTypeExpr(TypeExprContext ctx) {
        Class<?> cls = BuiltInTypesSet.getCls(ctx.primitiveType().getText());
        addInstruction(new ConstInstruction(newReporterWithToken(ctx.getStart()), new MetaClass(cls)));
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
        String text = ctx.ContextSelector().getText();
        String variableName = text.substring(2, text.length() - 1).trim();
        addInstruction(new LoadInstruction(newReporterWithToken(ctx.getStart()), variableName));
        return null;
    }

    @Override
    public Void visitLiteral(LiteralContext literal) {
        TerminalNode integerLiteral = literal.IntegerLiteral();
        if (integerLiteral != null) {
            try {
                Number intResult = parseInteger(remove(integerLiteral.getText(), '_'));
                addInstruction(new ConstInstruction(newReporterWithToken(integerLiteral.getSymbol()), intResult));
            } catch (NumberFormatException nfe) {
                throw reportParseErr(integerLiteral.getSymbol(),
                        "INVALID_NUMBER", "invalid number");
            }
            return null;
        }
        TerminalNode floatingPointLiteral = literal.FloatingPointLiteral();
        if (floatingPointLiteral != null) {
            try {
                Number floatingResult = parseFloating(remove(floatingPointLiteral.getText(), '_'));
                addInstruction(new ConstInstruction(
                        newReporterWithToken(floatingPointLiteral.getSymbol()), floatingResult)
                );
            } catch (NumberFormatException nfe) {
                throw reportParseErr(floatingPointLiteral.getSymbol(), "INVALID_NUMBER", "invalid number");
            }
            return null;
        }
        BoolenLiteralContext booleanLiteral = literal.boolenLiteral();
        if (booleanLiteral != null) {
            boolean boolValue = Boolean.parseBoolean(booleanLiteral.getText());
            addInstruction(
                    new ConstInstruction(newReporterWithToken(booleanLiteral.getStart()), boolValue)
            );
            return null;
        }
        TerminalNode rawStringLiteral = literal.RawStringLiteral();
        if (rawStringLiteral != null) {
            String rawStr = parseRawStringEscape(rawStringLiteral.getText());
            addInstruction(
                    new ConstInstruction(newReporterWithToken(rawStringLiteral.getSymbol()), rawStr)
            );
            return null;
        }
        TerminalNode stringLiteral = literal.StringLiteral();
        if (stringLiteral != null) {
            ErrorReporter errorReporter = newReporterWithToken(stringLiteral.getSymbol());
            int strPartNum = handleStringInterpolation(
                    errorReporter,
                    parseStringEscape(stringLiteral.getText())
            );
            if (strPartNum > 1) {
                addInstruction(new StringJoinInstruction(errorReporter, strPartNum));
            }
            return null;
        }
        TerminalNode nullLiteral = literal.NULL();
        if (nullLiteral != null) {
            addInstruction(
                    new ConstInstruction(newReporterWithToken(nullLiteral.getSymbol()), null)
            );
            return null;
        }
        return null;
    }

    private String parseRawStringEscape(String originStr) {
        StringBuilder result = new StringBuilder();
        final byte init = 0;
        final byte escape = 1;
        byte state = 0;

        int i = 1;
        while (i < originStr.length() - 1) {
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
                    if (cur == '\'') {
                        result.append('\'');
                    } else {
                        result.append('\\').append(cur);
                    }
                    break;
            }
        }
        return result.toString();
    }

    private int handleStringInterpolation(ErrorReporter errorReporter, String originStr) {
        int dollarIndex = originStr.indexOf("${");
        int dollarCloseIndex = dollarIndex == -1? -1: originStr.indexOf('}', dollarIndex);
        if (dollarIndex == -1 || dollarCloseIndex == -1) {
            addInstruction(
                    new ConstInstruction(errorReporter, originStr)
            );
            return 1;
        }
        String left = originStr.substring(0, dollarIndex);
        String variableName = originStr.substring(dollarIndex + 2, dollarCloseIndex).trim();
        String right = originStr.substring(dollarCloseIndex + 1);

        // left const string
        addInstruction(
                new ConstInstruction(errorReporter, left)
        );

        // variable
        addInstruction(
                new LoadInstruction(errorReporter, variableName)
        );

        // right
        return 2 + handleStringInterpolation(errorReporter, right);
    }

    private String parseStringEscape(String originStr) {
        StringBuilder result = new StringBuilder();
        final byte init = 0;
        final byte escape = 1;
        byte state = 0;

        int i = 1;
        while (i < originStr.length() - 1) {
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
                BigDecimal baseDecimal = new BigDecimal(floatingText.substring(0, floatingText.length() - 1));
                return baseDecimal.floatValue();
            case 'd':
            case 'D':
                baseDecimal = new BigDecimal(floatingText.substring(0, floatingText.length() - 1));
                return baseDecimal.doubleValue();
            default:
                baseDecimal = new BigDecimal(floatingText);
                return baseDecimal.compareTo(MAX_DOUBLE) <= 0? baseDecimal.doubleValue(): baseDecimal;
        }
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
        PrimitiveTypeContext primitiveTypeContext = declTypeContext.primitiveType();
        if (primitiveTypeContext != null) {
            return BuiltInTypesSet.getCls(primitiveTypeContext.getText());
        }

        ReferenceTypeContext referenceTypeContext = declTypeContext.referenceType();
        ClsTypeContext clsTypeContext = referenceTypeContext.clsType();
        if (clsTypeContext != null) {
            return parseClsIds(clsTypeContext.varId());
        }

        // array
        ArrayTypeContext arrayTypeContext = referenceTypeContext.arrayType();
        PrimitiveTypeContext arrPrimitiveTypeContext = arrayTypeContext.primitiveType();
        Class<?> baseType = arrPrimitiveTypeContext != null?
                BuiltInTypesSet.getCls(arrPrimitiveTypeContext.getText()):
                parseClsIds(arrayTypeContext.clsType().varId());
        DimsContext dimsContext = arrayTypeContext.dims();
        int layers = dimsContext.LBRACK().size();
        return wrapInArray(baseType, layers);
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
        ImportManager.LoadQualifiedResult loadQualifiedResult = importManager.loadQualified(fieldIds);
        if (loadQualifiedResult.getCls() == null || loadQualifiedResult.getRestIndex() != fieldIds.size()) {
            Token lastIdToken = varIdContexts.get(varIdContexts.size() - 1).getStart();
            throw reportParseErr(lastIdToken, "CLASS_NOT_FOUND",
                    "can not find class: " + String.join(".", fieldIds));
        }
        return loadQualifiedResult.getCls();
    }

    private QLSyntaxException reportParseErr(Token token, String errCode, String errReason) {
        return QLException.reportScannerErr(script, token.getStopIndex() + 1,
                token.getLine(), token.getCharPositionInLine(),
                token.getText(), errCode, errReason);
    }

    public List<QLInstruction> getInstructions() {
        return instructionList;
    }

    public int getMaxStackSize() {
        return maxStackSize;
    }

    private void ifElseInstructions(ErrorReporter conditionReporter, List<QLInstruction> thenInstructions,
                                    List<QLInstruction> elseInstructions) {
        addInstruction(new JumpIfPopInstruction(conditionReporter, false,
                thenInstructions.size() + 1));
        thenInstructions.forEach(this::addInstruction);
        addInstruction(new JumpInstruction(conditionReporter, elseInstructions.size()));
        elseInstructions.forEach(this::addInstruction);
    }

    private void jumpRightIfExpect(boolean expect, ErrorReporter opErrReporter, RuleContext right) {
        QvmInstructionVisitor rightVisitor = parseWithSubVisitor(right, generatorScope, Context.MACRO);
        List<QLInstruction> rightInstructions = rightVisitor.getInstructions();
        addInstruction(new JumpIfInstruction(opErrReporter, expect,
                // right instruction + operator
                rightInstructions.size() + 1));
        rightInstructions.forEach(this::addInstruction);
    }

    private QvmInstructionVisitor parseWithSubVisitor(RuleContext ruleContext, GeneratorScope generatorScope,
                                                      Context context) {
        QvmInstructionVisitor subVisitor = new QvmInstructionVisitor(script, importManager,
                generatorScope, operatorFactory, context);
        ruleContext.accept(subVisitor);
        return subVisitor;
    }

    private QvmInstructionVisitor parseExprBodyWithSubVisitor(ExpressionContext expressionContext,
                                                              GeneratorScope generatorScope, Context context) {
        QvmInstructionVisitor subVisitor = new QvmInstructionVisitor(script, importManager,
                generatorScope, operatorFactory, context);
        subVisitor.visitBodyExpression(expressionContext);
        return subVisitor;
    }

    private BlockStatementContext handleStmt(BlockStatementContext statementContext) {
        if (maybeMacroCall(statementContext)) {
            String macroName = statementContext.getStart().getText();
            MacroDefine macroDefine = generatorScope.getMacroInstructions(macroName);
            if (macroDefine != null) {
                for (QLInstruction instruction : macroDefine.getMacroInstructions()) {
                    addInstruction(instruction);
                }
                return macroDefine.getLastStmt();
            }
        }
        statementContext.accept(this);
        return statementContext;
    }

    private boolean maybeMacroCall(BlockStatementContext statementContext) {
        if (statementContext instanceof ExpressionStatementContext) {
            ExpressionContext expressionContext = ((ExpressionStatementContext) statementContext).expression();
            if (expressionContext != null) {
                if (expressionContext.getStart() == expressionContext.getStop()) {
                    return expressionContext.getStart().getType() == QLGrammarLexer.ID;
                }
            }
        }
        return false;
    }

    private void addInstruction(QLInstruction qlInstruction) {
        int stackExpandSize = qlInstruction.stackOutput() - qlInstruction.stackInput();
        expandStackSize(stackExpandSize);
        instructionList.add(qlInstruction);
    }

    private void expandStackSize(int stackExpandSize) {
        stackSize += stackExpandSize;
        if (stackSize > maxStackSize) {
            maxStackSize = stackSize;
        }
    }

    private ErrorReporter newReporterWithToken(Token token) {
        return new DefaultErrReporter(script, token.getStopIndex() + 1,
                token.getLine(), token.getCharPositionInLine(), token.getText());
    }

    private ErrorReporter newReportWithMultiToken(Token start, Token end) {
        return new DefaultErrReporter(script, end.getStopIndex() + 1,
                start.getLine(), end.getCharPositionInLine(),
                script.substring(start.getStartIndex(), end.getStopIndex()));
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
