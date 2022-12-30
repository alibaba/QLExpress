package com.alibaba.qlexpress4.parser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.alibaba.qlexpress4.exception.DefaultErrorReporter;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.exception.QLErrorCodes;
import com.alibaba.qlexpress4.parser.tree.*;
import com.alibaba.qlexpress4.runtime.*;
import com.alibaba.qlexpress4.runtime.instruction.*;
import com.alibaba.qlexpress4.runtime.operator.OperatorManager;

/**
 * Author: DQinYuan
 */
public class QvmInstructionGenerator implements QLProgramVisitor<Void, GeneratorScope> {

    private static final String BLOCK_LAMBDA_NAME_PREFIX = "BLOCK_";
    private static final String FOR_LAMBDA_NAME_PREFIX = "FOR_";
    private static final String CONDITION_SUFFIX = "_CONDITION";
    private static final String UPDATE_SUFFIX = "_UPDATE";
    private static final String BODY_SUFFIX = "_BODY";
    private static final String IF_LAMBDA_PREFIX = "IF_";
    private static final String THEN_SUFFIX = "_THEN";
    private static final String ELSE_SUFFIX = "_ELSE";
    private static final String LAMBDA_PREFIX = "LAMBDA_";
    private static final String MACRO_PREFIX = "MACRO_";
    private static final String TRY_LAMBDA_PREFIX = "TRY_";
    private static final String CATCH_SUFFIX = "_CATCH";
    private static final String FINAL_SUFFIX = "_FINAL";
    private static final String WHILE_PREFIX = "WHILE_";
    private static final String SHORT_CIRCUIT = "SHORT_CIRCUIT";
    private static final String TERNARY = "TERNARY";

    public enum Context {
        // recursive and cover each other
        BLOCK, LOOP, MACRO
    }

    private final String prefix;

    private final String script;

    private final OperatorManager operatorManager;

    private final List<QLInstruction> instructionList = new ArrayList<>();

    private final Context context;

    /**
     * true when generate embed instructions
     */
    private final boolean embed;

    private int stackSize;

    private int maxStackSize;

    private int blockCounter = 0;
    private int forCounter = 0;
    private int ifCounter = 0;
    private int lambdaCounter = 0;
    private int macroCounter = 0;
    private int tryCounter = 0;
    private int whileCounter = 0;

    public QvmInstructionGenerator(OperatorManager operatorManager, String prefix, String script) {
        this.operatorManager = operatorManager;
        this.prefix = prefix;
        this.script = script;
        this.context = Context.BLOCK;
        this.embed = false;
    }

    public QvmInstructionGenerator(OperatorManager operatorManager, String prefix, String script, Context context,
                                   boolean embed) {
        this.operatorManager = operatorManager;
        this.prefix = prefix;
        this.script = script;
        this.context = context;
        this.embed = embed;
    }

    @Override
    public Void visit(Program program, GeneratorScope generatorScope) {
        program.getStmtList().accept(this, generatorScope);
        return null;
    }

    @Override
    public Void visit(StmtList stmtList, GeneratorScope generatorScope) {
        Stmt preStmt = null;
        for (Stmt stmt : stmtList.getStmts()) {
            if (preStmt instanceof Expr) {
                // pop if expression without acceptor
                addInstruction(new PopInstruction(newReporterByNode(preStmt)));
            }
            preStmt = handleStmt(stmt, generatorScope);
        }
        if (embed) {
            if (preStmt instanceof Expr || preStmt instanceof ReturnStmt || preStmt instanceof Break ||
                    preStmt instanceof Continue) {
                return null;
            }
            addInstruction(
                    new ConstInstruction(newReporterByNode(
                            preStmt == null? stmtList: preStmt
                    ), null)
            );
        } else if (context == Context.BLOCK) {
            if (preStmt instanceof Expr) {
                addInstruction(
                        new ReturnInstruction(newReporterByNode(preStmt), QResult.ResultType.RETURN)
                );
            }
        } else if (context == Context.LOOP) {
            if (preStmt instanceof ReturnStmt || preStmt instanceof Break || preStmt instanceof Continue) {
                return null;
            }
            addInstruction(new BreakContinueInstruction(newReporterByNode(
                    preStmt == null? stmtList: preStmt
            ), QResult.LOOP_CONTINUE_RESULT));
        }
        return null;
    }

    private Stmt handleStmt(Stmt stmt, GeneratorScope generatorScope) {
        if (stmt instanceof IdExpr) {
            MacroDefine macroDefine = generatorScope
                .getMacroInstructions(stmt.getKeyToken().getLexeme());
            if (macroDefine != null) {
                addMacroInstructions(macroDefine.getNodeInstructions());
                return macroDefine.getLastStmt();
            }
        }
        stmt.accept(this, generatorScope);
        return stmt;
    }

    private void checkBreakContinueInLoop(QLInstruction qlInstruction) {
        if (qlInstruction instanceof BreakContinueInstruction && context != Context.LOOP) {
            throw qlInstruction.getErrorReporter().reportFormat(QLErrorCodes.BREAK_CONTINUE_OUTSIDE_LOOP.name(),
                    QLErrorCodes.BREAK_CONTINUE_OUTSIDE_LOOP.getErrorMsg());
        }
    }

    @Override
    public Void visit(IndexCallExpr indexCallExpr, GeneratorScope generatorScope) {
        indexCallExpr.getTarget().accept(this, generatorScope);
        indexCallExpr.getIndex().accept(this, generatorScope);
        addInstruction(new IndexInstruction(newReporterByNode(indexCallExpr)));
        return null;
    }

    @Override
    public Void visit(AssignExpr assignExpr, GeneratorScope generatorScope) {
        assignExpr.getLeft().accept(this, generatorScope);
        assignExpr.getRight().accept(this, generatorScope);
        addInstruction(new OperatorInstruction(newReporterByNode(assignExpr), operatorManager
            .getBinaryOperator(assignExpr.getKeyToken().getLexeme())));
        return null;
    }

    @Override
    public Void visit(BinaryOpExpr binaryOpExpr, GeneratorScope generatorScope) {
        binaryOpExpr.getLeft().accept(this, generatorScope);

        ErrorReporter opErrReporter = newReporterByNode(binaryOpExpr);
        // short circuit
        if ("&&".equals(binaryOpExpr.getKeyToken().getLexeme())) {
            jumpRightIfExpect(false, opErrReporter, binaryOpExpr.getRight(), generatorScope);
        } else if ("||".equals(binaryOpExpr.getKeyToken().getLexeme())) {
            jumpRightIfExpect(true, opErrReporter, binaryOpExpr.getRight(), generatorScope);
        } else {
            binaryOpExpr.getRight().accept(this, generatorScope);
        }
        addInstruction(new OperatorInstruction(opErrReporter, operatorManager
                .getBinaryOperator(binaryOpExpr.getKeyToken().getLexeme())));
        return null;
    }

    private void jumpRightIfExpect(boolean expect, ErrorReporter opErrReporter, Expr right, GeneratorScope generatorScope) {
        NodeInstructions rightInstructions = generateNodeInstructionsEmbed(SHORT_CIRCUIT,
                toStmtList(right), generatorScope);
        List<QLInstruction> rightInstructionList = rightInstructions.getInstructions();
        addInstruction(new JumpIfInstruction(opErrReporter, expect,
                // right instruction + operator
                rightInstructionList.size() + 1));
        rightInstructionList.forEach(this::addInstruction);
    }

    @Override
    public Void visit(Block block, GeneratorScope generatorScope) {
        String blockScopeName = blockScopeName();
        ErrorReporter blockErrReporter = newReporterByNode(block);
        addInstruction(new NewScopeInstruction(blockErrReporter, blockScopeName));

        List<QLInstruction> blockInstructions = generateNodeInstructionsEmbed(blockScopeName,
                toStmtList(block), new GeneratorScope(generatorScope)).getInstructions();
        blockInstructions.forEach(this::addInstruction);

        addInstruction(new CloseScopeInstruction(blockErrReporter, blockScopeName));
        return null;
    }

    @Override
    public Void visit(Break aBreak, GeneratorScope generatorScope) {
        addInstruction(new BreakContinueInstruction(newReporterByNode(aBreak), QResult.LOOP_BREAK_RESULT));
        return null;
    }

    @Override
    public Void visit(CallExpr callExpr, GeneratorScope generatorScope) {
        Expr target = callExpr.getTarget();
        if (target instanceof GetFieldExpr) {
            // method invoke
            GetFieldExpr getFieldExpr = (GetFieldExpr)target;
            getFieldExpr.getExpr().accept(this, generatorScope);
            callExpr.getArguments().forEach(arg -> arg.accept(this, generatorScope));
            Identifier attribute = getFieldExpr
                .getAttribute();
            addInstruction(new MethodInvokeInstruction(newReporterByToken(attribute.getKeyToken()),
                attribute.getId(), callExpr.getArguments().size()));
        } else if (target instanceof IdExpr) {
            callExpr.getArguments().forEach(arg -> arg.accept(this, generatorScope));
            addInstruction(new CallFunctionInstruction(newReporterByNode(target),
                target.getKeyToken().getLexeme(), callExpr.getArguments().size()));
        } else {
            // evaluated lambda
            target.accept(this, generatorScope);
            callExpr.getArguments().forEach(arg -> arg.accept(this, generatorScope));
            addInstruction(new CallInstruction(newReporterByNode(callExpr),
                callExpr.getArguments().size()));
        }

        return null;
    }

    @Override
    public Void visit(CastExpr castExpr, GeneratorScope generatorScope) {
        castExpr.getTypeExpr().accept(this, generatorScope);
        castExpr.getTarget().accept(this, generatorScope);
        addInstruction(new CastInstruction(newReporterByNode(castExpr)));
        return null;
    }

    @Override
    public Void visit(ConstExpr constExpr, GeneratorScope generatorScope) {
        addInstruction(new ConstInstruction(newReporterByNode(constExpr), constExpr.getConstValue()));
        return null;
    }

    @Override
    public Void visit(Continue aContinue, GeneratorScope generatorScope) {
        ErrorReporter errorReporter = newReporterByNode(aContinue);
        addInstruction(new BreakContinueInstruction(errorReporter, QResult.LOOP_CONTINUE_RESULT));
        return null;
    }

    @Override
    public Void visit(GetFieldExpr getFieldExpr, GeneratorScope generatorScope) {
        getFieldExpr.getExpr().accept(this, generatorScope);
        String fieldName = getFieldExpr.getAttribute().getId();
        addInstruction(new GetFieldInstruction(newReporterByNode(getFieldExpr), fieldName));
        return null;
    }

    @Override
    public Void visit(ForEachStmt forEachStmt, GeneratorScope generatorScope) {
        forEachStmt.getTarget().accept(this, generatorScope);
        ErrorReporter forEachErrReporter = newReporterByNode(forEachStmt);
        VarDecl itVar = forEachStmt.getItVar();
        Class<?> itVarClas = itVar.getType() != null? itVar.getType().getClz(): Object.class;
        QLambdaDefinition bodyLambda = generateLambdaNewScopeContext(prefix + FOR_LAMBDA_NAME_PREFIX + forCount(),
                toStmtList(forEachStmt.getBody()), generatorScope,
                Collections.singletonList(
                        new QLambdaDefinitionInner.Param(itVar.getVariable().getId(), itVarClas)
                ), Context.LOOP);
        addInstruction(new ForEachInstruction(forEachErrReporter, bodyLambda,
                itVarClas, newReporterByNode(forEachStmt.getTarget())
        ));
        return null;
    }

    @Override
    public Void visit(ForStmt forStmt, GeneratorScope generatorScope) {
        int forCount = forCount();
        ErrorReporter forErrReporter = newReporterByNode(forStmt);

        // for init
        QLambdaDefinitionInner forInitLambda = forStmt.getForInit() != null? generateLambda(
                prefix + FOR_LAMBDA_NAME_PREFIX + forCount, toStmtList(forStmt.getForInit()), generatorScope):
                null;

        // condition
        QLambdaDefinitionInner forConditionLambda = forStmt.getCondition() != null? generateLambda(
                prefix + FOR_LAMBDA_NAME_PREFIX + forCount + CONDITION_SUFFIX,
                toStmtList(forStmt.getCondition()), generatorScope
        ): null;

        // for update
        QLambdaDefinitionInner forUpdateLambda = forStmt.getForUpdate() != null? generateLambda(
                prefix + FOR_LAMBDA_NAME_PREFIX + forCount + UPDATE_SUFFIX,
                toStmtList(forStmt.getForUpdate()), generatorScope): null;

        // for body
        QLambdaDefinitionInner forBodyLambda = generateLambdaNewScopeContext(
                prefix + FOR_LAMBDA_NAME_PREFIX + forCount + BODY_SUFFIX,
                toStmtList(forStmt.getBody()), generatorScope, Context.LOOP);

        int forInitSize = forInitLambda == null? 0: forInitLambda.getMaxStackSize();
        int forConditionSize = forConditionLambda == null? 0: forConditionLambda.getMaxStackSize();
        int forUpdateSize = forUpdateLambda == null? 0: forUpdateLambda.getMaxStackSize();
        int forScopeMaxStackSize = Math.max(forInitSize, Math.max(forConditionSize, forUpdateSize));
        addInstruction(new ForInstruction(forErrReporter, forInitLambda,
                forConditionLambda,
                forStmt.getCondition() != null? newReporterByNode(forStmt.getCondition()): null,
                forUpdateLambda, forScopeMaxStackSize,
                forBodyLambda));
        return null;
    }

    @Override
    public Void visit(FunctionStmt functionStmt, GeneratorScope generatorScope) {
        String functionName = functionStmt.getName().getId();
        QLambdaDefinition functionLambda = generateLambdaNewScope(functionName, functionStmt.getBody().getStmtList(),
                generatorScope, functionStmt.getParams().stream()
                        .map(varDecl -> new QLambdaDefinitionInner.Param(
                                varDecl.getVariable().getId(),
                                varDecl.getType() == null? Object.class: varDecl.getType().getClz()
                        ))
                        .collect(Collectors.toList()));
        ErrorReporter errorReporter = newReporterByNode(functionStmt);
        addInstruction(new DefineFunctionInstruction(errorReporter, functionName, functionLambda));
        return null;
    }

    @Override
    public Void visit(GroupExpr groupExpr, GeneratorScope generatorScope) {
        groupExpr.getExpr().accept(this, generatorScope);
        return null;
    }

    @Override
    public Void visit(Identifier identifier, GeneratorScope generatorScope) {
        return null;
    }

    @Override
    public Void visit(IdExpr idExpr, GeneratorScope generatorScope) {
        String id = idExpr.getKeyToken().getLexeme();
        addInstruction(new LoadInstruction(newReporterByNode(idExpr), id));
        return null;
    }

    @Override
    public Void visit(IfExpr ifExpr, GeneratorScope generatorScope) {
        ifExpr.getCondition().accept(this, generatorScope);

        int ifCount = ifCount();

        ErrorReporter ifErrorReporter = newReporterByNode(ifExpr);
        String ifScopeName = prefix + IF_LAMBDA_PREFIX + ifCount;
        addInstruction(new NewScopeInstruction(ifErrorReporter, ifScopeName));

        // then body
        List<QLInstruction> thenInstructionList = generateNodeInstructionsEmbed(
                ifScopeName + THEN_SUFFIX, toStmtList(ifExpr.getThenBranch()),
                new GeneratorScope(generatorScope)).getInstructions();
        // else body
        List<QLInstruction> elseInstructionList = ifExpr.getElseBranch() == null?
                Collections.singletonList(new ConstInstruction(ifErrorReporter, null)):
                generateNodeInstructionsEmbed(
                        ifScopeName + ELSE_SUFFIX, toStmtList(ifExpr.getElseBranch()),
                        new GeneratorScope(generatorScope)).getInstructions();
        ifElseInstructions(newReporterByNode(ifExpr.getCondition()), thenInstructionList, elseInstructionList);

        addInstruction(new CloseScopeInstruction(ifErrorReporter, ifScopeName));
        return null;
    }

    private void ifElseInstructions(ErrorReporter conditionReporter, List<QLInstruction> thenInstructions,
                                    List<QLInstruction> elseInstructions) {
        addInstruction(new JumpIfPopInstruction(conditionReporter, false,
                thenInstructions.size() + 1));
        thenInstructions.forEach(this::addInstruction);
        addInstruction(new JumpInstruction(conditionReporter, elseInstructions.size()));
        elseInstructions.forEach(this::addInstruction);
    }

    @Override
    public Void visit(GetMethodExpr getMethodExpr, GeneratorScope context) {
        getMethodExpr.getExpr().accept(this, context);
        addInstruction(new GetMethodInstruction(newReporterByNode(getMethodExpr),
            getMethodExpr.getAttribute().getId()));
        return null;
    }

    @Override
    public Void visit(MapExpr mapExpr, GeneratorScope context) {
        List<String> keys = new ArrayList<>(mapExpr.getEntries().size());
        for (Map.Entry<String, Expr> entry : mapExpr.getEntries()) {
            keys.add(entry.getKey());
            entry.getValue().accept(this, context);
        }
        addInstruction(new NewMapInstruction(newReporterByNode(mapExpr), keys));
        return null;
    }

    @Override
    public Void visit(MultiNewArrayExpr newArrayDimsExpr, GeneratorScope context) {
        for (Expr dim : newArrayDimsExpr.getDims()) {
            dim.accept(this, context);
        }
        addInstruction(new MultiNewArrayInstruction(newReporterByNode(newArrayDimsExpr),
                newArrayDimsExpr.getClz(), newArrayDimsExpr.getDims().size()));
        return null;
    }

    @Override
    public Void visit(NewArrayExpr newArrayExpr, GeneratorScope context) {
        newArrWithExprs(newArrayExpr, newArrayExpr.getClz(), newArrayExpr.getValues(), context);
        return null;
    }

    private void newArrWithExprs(SyntaxNode syntaxNode, Class<?> componentClz,
                                 List<Expr> exprs, GeneratorScope context) {
        for (Expr value : exprs) {
            value.accept(this, context);
        }
        addInstruction(new NewArrayInstruction(newReporterByNode(syntaxNode), componentClz, exprs.size()));
    }

    @Override
    public Void visit(ImportStmt importStmt, GeneratorScope generatorScope) {
        // import statement has been handled in parser
        return null;
    }

    @Override
    public Void visit(LambdaExpr lambdaExpr, GeneratorScope generatorScope) {
        List<QLambdaDefinitionInner.Param> paramClzes = lambdaExpr.getParameters().stream()
            .map(varDecl -> new QLambdaDefinitionInner.Param(varDecl.getVariable().getId(),
                varDecl.getType() == null? Object.class: varDecl.getType().getClz()))
            .collect(Collectors.toList());
        Expr lambdaBody = lambdaExpr.getBody();
        ErrorReporter errorReporter = newReporterByNode(lambdaExpr);
        QLambdaDefinition qLambda = generateLambdaNewScope(lambdaName(), toStmtList(lambdaBody), generatorScope, paramClzes);
        addInstruction(new LoadLambdaInstruction(errorReporter, qLambda));
        return null;
    }

    @Override
    public Void visit(ListExpr listExpr, GeneratorScope generatorScope) {
        List<Expr> elementExprs = listExpr.getElements();
        elementExprs.forEach(expr -> expr.accept(this, generatorScope));
        addInstruction(new NewListInstruction(newReporterByNode(listExpr), elementExprs.size()));
        return null;
    }

    @Override
    public Void visit(LocalVarDeclareStmt localVarDeclareStmt, GeneratorScope generatorScope) {
        ErrorReporter errorReporter = newReporterByNode(localVarDeclareStmt);
        String varName = localVarDeclareStmt.getVarDecl().getVariable().getId();
        Class<?> declareClz = localVarDeclareStmt.getVarDecl().getType().getClz();
        if (localVarDeclareStmt.getInitializer() != null) {
            if (declareClz.isArray() && localVarDeclareStmt.getInitializer() instanceof ListExpr) {
                // opt array init, not support embed
                ListExpr listExpr = (ListExpr) localVarDeclareStmt.getInitializer();
                newArrWithExprs(localVarDeclareStmt.getInitializer(), declareClz.getComponentType(),
                        listExpr.getElements(), generatorScope);
            } else if (declareClz == Character.class && localVarDeclareStmt.getInitializer() instanceof ConstExpr &&
                isCharConst((ConstExpr) localVarDeclareStmt.getInitializer())) {
                String constString = (String)
                        ((ConstExpr) localVarDeclareStmt.getInitializer()).getConstValue();
                addInstruction(new ConstInstruction(
                        newReporterByNode(localVarDeclareStmt.getInitializer()),
                        constString.charAt(0))
                );
            } else {
                localVarDeclareStmt.getInitializer().accept(this, generatorScope);
            }
        } else {
            addInstruction(new ConstInstruction(errorReporter, null));
        }
        addInstruction(new DefineLocalInstruction(errorReporter, varName, declareClz));
        return null;
    }

    private boolean isCharConst(ConstExpr constExpr) {
        Object constValue = constExpr.getConstValue();
        return constValue instanceof String && ((String) constValue).length() == 1;
    }

    @Override
    public Void visit(MacroStmt macroStmt, GeneratorScope generatorScope) {
        NodeInstructions macroInstructions = generateNodeInstructions(macroLambdaName(),
            macroStmt.getBody().getStmtList(), generatorScope, Context.MACRO, false);
        List<Stmt> macroStmtList = macroStmt.getBody().getStmtList().getStmts();
        Stmt macroLastStmt = macroStmtList.isEmpty()? macroStmt: macroStmtList.get(macroStmtList.size() - 1);
        generatorScope.defineMacro(macroStmt.getName().getId(),
                new MacroDefine(macroInstructions, macroLastStmt));
        return null;
    }

    @Override
    public Void visit(NewExpr newExpr, GeneratorScope generatorScope) {
        List<Expr> arguments = newExpr.getArguments();
        arguments.forEach(argExpr -> argExpr.accept(this, generatorScope));

        Class<?> clz = newExpr.getClazz().getClz();
        addInstruction(new NewInstruction(newReporterByNode(newExpr), clz, arguments.size()));
        return null;
    }

    @Override
    public Void visit(PrefixUnaryOpExpr prefixUnaryOpExpr, GeneratorScope generatorScope) {
        prefixUnaryOpExpr.getExpr().accept(this, generatorScope);
        String op = prefixUnaryOpExpr.getKeyToken().getLexeme();
        addInstruction(new UnaryInstruction(newReporterByNode(prefixUnaryOpExpr),
            operatorManager.getPrefixUnaryOperator(op)));
        return null;
    }

    @Override
    public Void visit(SuffixUnaryOpExpr suffixUnaryOpExpr, GeneratorScope generatorScope) {
        suffixUnaryOpExpr.getExpr().accept(this, generatorScope);
        String op = suffixUnaryOpExpr.getKeyToken().getLexeme();
        addInstruction(new UnaryInstruction(newReporterByNode(suffixUnaryOpExpr),
            operatorManager.getSuffixUnaryOperator(op)));
        return null;
    }

    @Override
    public Void visit(ReturnStmt returnStmt, GeneratorScope generatorScope) {
        ErrorReporter errorReporter = newReporterByNode(returnStmt);
        if (returnStmt.getExpr() != null) {
            returnStmt.getExpr().accept(this, generatorScope);
        } else {
            addInstruction(new ConstInstruction(errorReporter, null));
        }

        addInstruction(new ReturnInstruction(errorReporter, QResult.ResultType.RETURN));
        return null;
    }

    @Override
    public Void visit(TernaryExpr ternaryExpr, GeneratorScope generatorScope) {
        ternaryExpr.getCondition().accept(this, generatorScope);

        NodeInstructions thenNode = generateNodeInstructionsEmbed(TERNARY,
                toStmtList(ternaryExpr.getThenExpr()), generatorScope);
        List<QLInstruction> thenInstructions = thenNode.getInstructions();

        NodeInstructions elseNode = generateNodeInstructionsEmbed(TERNARY,
                toStmtList(ternaryExpr.getElseExpr()), generatorScope);
        List<QLInstruction> elseInstructions = elseNode.getInstructions();
        ifElseInstructions(newReporterByNode(ternaryExpr.getCondition()), thenInstructions, elseInstructions);
        return null;
    }

    @Override
    public Void visit(TryCatch tryCatchExpr, GeneratorScope generatorScope) {
        int tryCount = tryCount();
        QLambdaDefinition bodyLambda = generateLambdaNewScope(prefix + TRY_LAMBDA_PREFIX + tryCount,
            toStmtList(tryCatchExpr.getBody()), generatorScope);

        Map<Class<?>, QLambdaDefinition> exceptionTable = new HashMap<>();
        for (int catchCount = 0; catchCount < tryCatchExpr.getTryCatch().size(); catchCount++) {
            TryCatch.CatchClause tryCatch = tryCatchExpr.getTryCatch().get(catchCount);
            String eName = tryCatch.getVariable().getId();
            String lambdaName = prefix + TRY_LAMBDA_PREFIX + catchCount + CATCH_SUFFIX;
            NodeInstructions catchBody = generateNodeInstructionsNewScope(lambdaName,
                toStmtList(tryCatch.getBody()), generatorScope);
            for (int exceptionCount = 0; exceptionCount < tryCatch.getExceptions().size(); exceptionCount++) {
                DeclType exceptionType = tryCatch.getExceptions().get(exceptionCount);
                QLambdaDefinitionInner.Param eParam = new QLambdaDefinitionInner.Param(eName, exceptionType.getClz());
                QLambdaDefinition exceptionHandlerDefinition = new QLambdaDefinitionInner(lambdaName,
                    catchBody.getInstructions(), Collections.singletonList(eParam),
                    catchBody.getMaxStackSize());
                exceptionTable.put(exceptionType.getClz(), exceptionHandlerDefinition);
            }
        }

        addInstruction(new TryCatchInstruction(newReporterByNode(tryCatchExpr), bodyLambda, exceptionTable,
            tryCatchExpr.getTryFinal() != null ?
                generateLambdaNewScope(prefix + TRY_LAMBDA_PREFIX + tryCount + FINAL_SUFFIX,
                    toStmtList(tryCatchExpr.getTryFinal()), generatorScope) :
                null));
        return null;
    }

    @Override
    public Void visit(TypeExpr typeExpr, GeneratorScope generatorScope) {
        addInstruction(new ConstInstruction(newReporterByNode(typeExpr),
            new MetaClass(typeExpr.getDeclType().getClz())));
        return null;
    }

    @Override
    public Void visit(WhileStmt whileStmt, GeneratorScope generatorScope) {
        int whileCount = whileCount();
        QLambdaDefinitionInner conditionLambda = generateLambda(prefix + WHILE_PREFIX + whileCount + CONDITION_SUFFIX,
            toStmtList(whileStmt.getCondition()), generatorScope);
        QLambdaDefinition bodyLambda = generateLambdaNewScopeContext(prefix + WHILE_PREFIX + whileCount + BODY_SUFFIX,
            toStmtList(whileStmt.getBody()), generatorScope, Context.LOOP);
        addInstruction(new WhileInstruction(newReporterByNode(whileStmt), conditionLambda, bodyLambda));
        // condition lambda run in current scope
        expandStackSize(conditionLambda.getMaxStackSize());
        return null;
    }

    public List<QLInstruction> getInstructionList() {
        return instructionList;
    }

    public int getMaxStackSize() {
        return maxStackSize;
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

    private void addMacroInstructions(NodeInstructions instructions) {
        // TODO: 改名成 addMacroInstructions, 对于条件分支场景进行嵌套检查
        if (instructions.getMaxStackSize() > maxStackSize) {
            maxStackSize = instructions.getMaxStackSize();
        }
        for (QLInstruction instruction : instructions.getInstructions()) {
            checkBreakContinueInLoop(instruction);
            instructionList.add(instruction);
        }
    }

    private QLambdaDefinitionInner generateLambdaNewScope(String name, StmtList stmtList,
        GeneratorScope generatorScope) {
        return generateLambda(name, stmtList, new GeneratorScope(generatorScope));
    }

    private QLambdaDefinitionInner generateLambdaNewScope(String name, StmtList stmtList, GeneratorScope generatorScope,
        List<QLambdaDefinitionInner.Param> paramsType) {
        return generateLambda(name, stmtList, paramsType, new GeneratorScope(generatorScope));
    }

    private QLambdaDefinitionInner generateLambdaNewScopeContext(String name, StmtList stmtList, GeneratorScope generatorScope,
                                                     List<QLambdaDefinitionInner.Param> paramsType, Context context) {
        NodeInstructions nodeInstructions = generateNodeInstructions(name, stmtList,
                new GeneratorScope(generatorScope), context, false);
        return new QLambdaDefinitionInner(name, nodeInstructions.getInstructions(), paramsType,
                nodeInstructions.getMaxStackSize());
    }

    private QLambdaDefinitionInner generateLambdaNewScopeContext(String name, StmtList stmtList,
                                                          GeneratorScope generatorScope,
                                                          Context context) {
        return generateLambdaNewScopeContext(name, stmtList, generatorScope, Collections.emptyList(), context);
    }

    private QLambdaDefinitionInner generateLambda(String name, StmtList stmtList, GeneratorScope generatorScope) {
        return generateLambda(name, stmtList, Collections.emptyList(), generatorScope);
    }

    private QLambdaDefinitionInner generateLambda(String name, StmtList stmtList,
        List<QLambdaDefinitionInner.Param> paramsType,
        GeneratorScope generatorScope) {
        NodeInstructions nodeInstructions = generateNodeInstructions(name, stmtList, generatorScope);
        return new QLambdaDefinitionInner(name, nodeInstructions.getInstructions(), paramsType,
            nodeInstructions.getMaxStackSize());
    }

    private NodeInstructions generateNodeInstructionsEmbed(String name, StmtList stmtList,
                                                           GeneratorScope generatorScope) {
        return generateNodeInstructions(name, stmtList, generatorScope, context, true);
    }

    private NodeInstructions generateNodeInstructionsNewScope(String name, StmtList stmtList,
                                                              GeneratorScope generatorScope) {
        return generateNodeInstructions(name, stmtList, new GeneratorScope(generatorScope));
    }

    private NodeInstructions generateNodeInstructions(String name, StmtList stmtList,
        GeneratorScope generatorScope) {
        return generateNodeInstructions(name, stmtList, generatorScope, context,false);
    }

    private NodeInstructions generateNodeInstructions(String name, StmtList stmtList, GeneratorScope generatorScope,
                                                      Context context, boolean embed) {
        QvmInstructionGenerator qvmInstructionGenerator = new QvmInstructionGenerator(operatorManager,
                name + "_", script, context, embed);
        stmtList.accept(qvmInstructionGenerator, generatorScope);
        return new NodeInstructions(qvmInstructionGenerator.getInstructionList(),
                qvmInstructionGenerator.getMaxStackSize());
    }

    private StmtList toStmtList(Stmt stmt) {
        if (stmt instanceof Block) {
            return ((Block) stmt).getStmtList();
        } else {
            return new StmtList(stmt.getKeyToken(), Collections.singletonList(stmt));
        }
    }


    private String blockScopeName() {
        return prefix + BLOCK_LAMBDA_NAME_PREFIX + blockCounter++;
    }

    private String lambdaName() {
        return prefix + LAMBDA_PREFIX + lambdaCounter++;
    }

    private int forCount() {
        return forCounter++;
    }

    private int ifCount() {
        return ifCounter++;
    }

    private String macroLambdaName() {
        return prefix + MACRO_PREFIX + macroCounter++;
    }

    private int tryCount() {
        return tryCounter++;
    }

    private int whileCount() {
        return whileCounter++;
    }

    private ErrorReporter newReporterByNode(SyntaxNode syntaxNode) {
        return new DefaultErrorReporter(script, syntaxNode.getKeyToken());
    }

    private ErrorReporter newReporterByToken(Token token) {
        return new DefaultErrorReporter(script, token);
    }

    public static class NodeInstructions {
        private final List<QLInstruction> instructions;
        private final int maxStackSize;

        private NodeInstructions(List<QLInstruction> instructions, int maxStackSize) {
            this.instructions = instructions;
            this.maxStackSize = maxStackSize;
        }

        public List<QLInstruction> getInstructions() {
            return instructions;
        }

        public int getMaxStackSize() {
            return maxStackSize;
        }
    }
}
