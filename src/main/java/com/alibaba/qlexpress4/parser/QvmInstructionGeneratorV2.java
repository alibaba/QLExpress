package com.alibaba.qlexpress4.parser;

import com.alibaba.qlexpress4.exception.DefaultErrorReporter;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.parser.tree.*;
import com.alibaba.qlexpress4.runtime.ExceptionTable;
import com.alibaba.qlexpress4.runtime.MetaClass;
import com.alibaba.qlexpress4.runtime.QLambdaDefinitionInner;
import com.alibaba.qlexpress4.runtime.QResult;
import com.alibaba.qlexpress4.runtime.instruction.*;
import com.alibaba.qlexpress4.runtime.operator.OperatorManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * TODO 性能对比
 * a generator instance for one scope
 * Author: DQinYuan
 */
public class QvmInstructionGeneratorV2 implements QLProgramVisitor<QList<QLInstruction>, Void> {

    private static final String FOR_BODY = "FOR_BODY_";
    private static final String FOR_CONDITION = "FOR_CONDITION_";
    private static final String FOR_UPDATE = "FOR_UPDATE_";
    private static final String FOR_INIT = "FOR_INIT_";
    private static final String WHILE_CONDITION = "WHILE_CONDITION_";
    private static final String WHILE_BODY = "WHILE_BODY_";
    private static final String BLOCK = "BLOCK_";
    private static final String FUNCTION = "FUNCTION_";
    private static final String LAMBDA = "LAMBDA_";
    private static final String MACRO = "MACRO_";
    private static final String IF = "IF_";
    private static final String THEN = "_THEN_";
    private static final String ELSE = "_ELSE_";
    private static final String SCOPE_SPLIT = "$";

    private int instructionSize;

    private int maxStackSize;

    private int stackSize;

    private int forCounter;

    private int whileCounter;

    private int blockCounter;

    private int functionCounter;

    private int lambdaCounter;

    private int macroCounter;

    private int ifCounter;

    private final String script;

    private final OperatorManager operatorManager;

    private final GeneratorScopeV2 generatorScope;

    private final String scopeName;

    public QvmInstructionGeneratorV2(String script, OperatorManager operatorManager,
                                     GeneratorScopeV2 generatorScope, String scopeName) {
        this.script = script;
        this.operatorManager = operatorManager;
        this.generatorScope = generatorScope;
        this.scopeName = scopeName;
    }

    @Override
    public QList<QLInstruction> visit(Program program, Void context) {
        return program.getStmtList().accept(this, context);
    }

    @Override
    public QList<QLInstruction> visit(StmtList stmtList, Void context) {
        QList<QLInstruction> result = QList.emptyList();

        Stmt preStmt = null;
        for (Stmt stmt : stmtList.getStmts()) {
            if (preStmt instanceof Expr) {
                // pop if expression without acceptor
                result.addLast(expandStack(new PopInstruction(newReporterByNode(preStmt))));
            }
            preStmt = handleStmt(stmt, result, context);
        }

        switch (generatorScope.getScopeType()) {
            case FUNCTION:
                // fill return
                if (preStmt instanceof Expr) {
                    result.addLast(expandStack(
                            new ReturnInstruction(newReporterByNode(preStmt), QResult.ResultType.RETURN)
                    ));
                }
                break;
            case LOOP:
                if (preStmt instanceof Expr) {
                    result.addLast(expandStack(
                            new PopInstruction(newReporterByNode(preStmt))
                    ));
                }
                break;
            case BLOCK:
                // fill block expr result
                if (preStmt instanceof Expr || preStmt instanceof ReturnStmt || preStmt instanceof Break ||
                        preStmt instanceof Continue) {
                    return result;
                }
                result.addLast(expandStack(
                        new ConstInstruction(newReporterByNode(
                                preStmt == null? stmtList: preStmt
                        ), null)
                ));
                break;
            case MACRO:
                break;
        }

        return result;
    }

    private Stmt handleStmt(Stmt stmt, QList<QLInstruction> list, Void context) {
        if (stmt instanceof IdExpr) {
            MacroDefineV2 macroDefine = generatorScope.getMacroDefine(stmt.getKeyToken().getLexeme());
            if (macroDefine != null) {
                QLInstruction[] macroInstructions = expandMacroStack(macroDefine);
                for (QLInstruction macroInstruction : macroInstructions) {
                    list.addLast(expandStack(macroInstruction));
                }
                return macroDefine.getLastStmt();
            }
        }
        list.addAll(stmt.accept(this, context));
        return stmt;
    }

    @Override
    public QList<QLInstruction> visit(IndexCallExpr indexCallExpr, Void context) {
        QList<QLInstruction> targetInstructions = indexCallExpr.getTarget().accept(this, context);
        QList<QLInstruction> indexInstructions = indexCallExpr.getIndex().accept(this, context);
        QLInstruction indexInstruction = expandStack(new IndexInstruction(newReporterByNode(indexCallExpr)));

        return QList.fromList(targetInstructions)
                .addAll(indexInstructions)
                .addLast(indexInstruction);
    }

    @Override
    public QList<QLInstruction> visit(AssignExpr assignExpr, Void context) {
        QList<QLInstruction> leftInstructions = assignExpr.getLeft().accept(this, context);
        QList<QLInstruction> rightInstructions = assignExpr.getRight().accept(this, context);
        // TODO: 找不到操作符报错
        QLInstruction operatorInstruction = expandStack(new OperatorInstruction(newReporterByNode(assignExpr),
                operatorManager.getBinaryOperator(assignExpr.getKeyToken().getLexeme())
        ));

        return QList.fromList(leftInstructions)
                .addAll(rightInstructions)
                .addLast(operatorInstruction);
    }

    @Override
    public QList<QLInstruction> visit(BinaryOpExpr binaryOpExpr, Void context) {
        // short circuit
        if ("&&".equals(binaryOpExpr.getKeyToken().getLexeme())) {
            return jumpRightIfExpect(false, binaryOpExpr, context);
        } else if ("||".equals(binaryOpExpr.getKeyToken().getLexeme())) {
            return jumpRightIfExpect(true, binaryOpExpr, context);
        }

        QList<QLInstruction> leftInstructions = binaryOpExpr.getLeft().accept(this, context);
        QList<QLInstruction> rightInstructions = binaryOpExpr.getRight().accept(this, context);
        QLInstruction operatorInstruction = expandStack(
                new OperatorInstruction(newReporterByNode(binaryOpExpr), operatorManager
                        .getBinaryOperator(binaryOpExpr.getKeyToken().getLexeme()))
        );

        return QList.fromList(leftInstructions)
                .addAll(rightInstructions)
                .addLast(operatorInstruction);
    }

    private QList<QLInstruction> jumpRightIfExpect(boolean expect, BinaryOpExpr binaryOpExpr, Void context) {
        QList<QLInstruction> leftInstructions = binaryOpExpr.getLeft().accept(this, context);
        expandStack(JumpIfInstruction.INSTANCE);
        QList<QLInstruction> rightInstructions = binaryOpExpr.getRight().accept(this, context);
        ErrorReporter errorReporter = newReporterByNode(binaryOpExpr);
        QLInstruction operatorInstruction = expandStack(
                new OperatorInstruction(errorReporter, operatorManager
                        .getBinaryOperator(binaryOpExpr.getKeyToken().getLexeme()))
        );

        return QList.fromList(leftInstructions)
                .addLast(new JumpIfInstruction(errorReporter, expect, instructionSize))
                .addAll(rightInstructions)
                .addLast(operatorInstruction);
    }

    @Override
    public QList<QLInstruction> visit(Block block, Void context) {
        ErrorReporter blockErrReporter = newReporterByNode(block);
        String blockScopeName = subBlockName();

        QLInstruction newScopeInstruction = expandStack(
                new NewScopeInstruction(blockErrReporter, blockScopeName, ExceptionTable.EMPTY)
        );

        QvmInstructionGeneratorV2 subVisitor = newSubVisitor(blockScopeName, GeneratorScopeV2.ScopeType.BLOCK);
        QList<QLInstruction> blockInstructions = block.getStmtList().accept(subVisitor, context);
        // TODO 测试用例
        expandStackSizeForExprScope(subVisitor.maxStackSize, subVisitor.instructionSize);

        QLInstruction closeScopeInstruction = expandStack(new CloseScopeInstruction(blockErrReporter, blockScopeName));

        return QList.singletonList(newScopeInstruction)
                .addAll(blockInstructions)
                .addLast(closeScopeInstruction);
    }

    private void expandStackSizeForExprScope(int scopeMaxStack, int scopeInstructionSize) {
        int localMaxStack = stackSize + scopeMaxStack;
        // expr will push a value on stack finally
        stackSize += 1;
        if (localMaxStack > maxStackSize) {
            this.maxStackSize = localMaxStack;
        }
        instructionSize += scopeInstructionSize;
    }

    @Override
    public QList<QLInstruction> visit(Break aBreak, Void context) {
        return QList.singletonList(expandStack(
                new BreakContinueInstruction(newReporterByNode(aBreak), QResult.LOOP_BREAK_RESULT)
        ));
    }

    @Override
    public QList<QLInstruction> visit(CallExpr callExpr, Void context) {
        Expr target = callExpr.getTarget();
        if (target instanceof GetFieldExpr) {
            // method invoke
            GetFieldExpr getFieldExpr = (GetFieldExpr)target;
            QList<QLInstruction> fieldExprInstructions = getFieldExpr.getExpr().accept(this, context);
            QList<QLInstruction> argInstructions = exprListInstructions(callExpr.getArguments(), context);
            Identifier attribute = getFieldExpr.getAttribute();
            QLInstruction methodInvokeInstruction = expandStack(
                    new MethodInvokeInstruction(newReporterByToken(attribute.getKeyToken()),
                            attribute.getId(), callExpr.getArguments().size()));

            return QList.fromList(fieldExprInstructions)
                    .addAll(argInstructions)
                    .addLast(methodInvokeInstruction);
        } else if (target instanceof IdExpr) {
            QList<QLInstruction> argInstructions = exprListInstructions(callExpr.getArguments(), context);
            QLInstruction callFunctionInstruction = expandStack(
                    new CallFunctionInstruction(newReporterByNode(target),
                            target.getKeyToken().getLexeme(),
                            callExpr.getArguments().size()));

            return QList.fromList(argInstructions)
                    .addLast(callFunctionInstruction);
        }

        // evaluate lambda
        QList<QLInstruction> targetInstructions = target.accept(this, context);
        QList<QLInstruction> argInstructions = exprListInstructions(callExpr.getArguments(), context);
        QLInstruction callInstruction = expandStack(
                new CallInstruction(newReporterByNode(callExpr), callExpr.getArguments().size())
        );

        return QList.fromList(targetInstructions)
                .addAll(argInstructions)
                .addLast(callInstruction);
    }

    @Override
    public QList<QLInstruction> visit(CastExpr castExpr, Void context) {
        QList<QLInstruction> typeInstructions = castExpr.getTypeExpr().accept(this, context);
        QList<QLInstruction> targetInstructions = castExpr.getTarget().accept(this, context);
        QLInstruction castInstruction = expandStack(
                new CastInstruction(newReporterByNode(castExpr))
        );

        return QList.fromList(typeInstructions)
                .addAll(targetInstructions)
                .addLast(castInstruction);
    }

    @Override
    public QList<QLInstruction> visit(ConstExpr constExpr, Void context) {
        return QList.singletonList(expandStack(
                new ConstInstruction(newReporterByNode(constExpr), constExpr.getConstValue())
        ));
    }

    @Override
    public QList<QLInstruction> visit(Continue aContinue, Void context) {
        return QList.singletonList(expandStack(
                new BreakContinueInstruction(newReporterByNode(aContinue), QResult.LOOP_CONTINUE_RESULT)
        ));
    }

    @Override
    public QList<QLInstruction> visit(GetFieldExpr getFieldExpr, Void context) {
        QList<QLInstruction> targetInstructions = getFieldExpr.getExpr().accept(this, context);
        String fieldName = getFieldExpr.getAttribute().getId();
        QLInstruction getFieldInstruction = expandStack(
                new GetFieldInstruction(newReporterByNode(getFieldExpr), fieldName)
        );

        return QList.fromList(targetInstructions)
                .addLast(getFieldInstruction);
    }

    @Override
    public QList<QLInstruction> visit(ForEachStmt forEachStmt, Void context) {
        QList<QLInstruction> targetInstructions = forEachStmt.getTarget().accept(this, context);
        VarDecl itVar = forEachStmt.getItVar();
        Class<?> itVarClas = itVar.getType() != null? itVar.getType().getClz(): Object.class;
        int forCount = forCount();
        QLambdaDefinitionInner bodyLambda = generateLoopLambdaDefinition(
                subScopeName(FOR_BODY, forCount),
                toStmtList(forEachStmt.getBody()),
                Collections.singletonList(
                        new QLambdaDefinitionInner.Param(itVar.getVariable().getId(), itVarClas)
                ), context);

        return QList.fromList(targetInstructions)
                .addLast(expandStack(
                        new ForEachInstruction(newReporterByNode(forEachStmt), bodyLambda, itVarClas,
                                newReporterByNode(forEachStmt.getTarget()))
                ));
    }

    @Override
    public QList<QLInstruction> visit(ForStmt forStmt, Void context) {
        int forCount = forCount();

        // for init
        QLambdaDefinitionInner forInitLambda = forStmt.getForInit() != null?
                generateLoopLambdaDefinitionNoParam(
                        subScopeName(FOR_INIT, forCount), toStmtList(forStmt.getForInit()), context):
                null;

        // condition
        QLambdaDefinitionInner forConditionLambda = forStmt.getCondition() != null?
                generateLambdaDefinitionNoParam(
                        subScopeName(FOR_CONDITION, forCount), toStmtList(forStmt.getCondition()), context):
                null;

        // for update
        QLambdaDefinitionInner forUpdateLambda = forStmt.getForUpdate() != null?
                generateLoopLambdaDefinitionNoParam(subScopeName(FOR_UPDATE, forCount),
                        toStmtList(forStmt.getForUpdate()), context):
                null;

        // for body
        QLambdaDefinitionInner forBodyLambda = generateLoopLambdaDefinitionNoParam(
                subScopeName(FOR_BODY, forCount), toStmtList(forStmt.getBody()), context);

        int forInitSize = forInitLambda == null? 0: forInitLambda.getMaxStackSize();
        int forConditionSize = forConditionLambda == null? 0: forConditionLambda.getMaxStackSize();
        int forUpdateSize = forUpdateLambda == null? 0: forUpdateLambda.getMaxStackSize();
        int forScopeMaxStackSize = Math.max(forInitSize, Math.max(forConditionSize, forUpdateSize));

        return QList.singletonList(expandStack(new ForInstruction(newReporterByNode(forStmt),
                forInitLambda, forConditionLambda,
                forStmt.getCondition() != null? newReporterByNode(forStmt.getCondition()): null,
                forUpdateLambda, forScopeMaxStackSize, forBodyLambda)
        ));
    }

    @Override
    public QList<QLInstruction> visit(FunctionStmt functionStmt, Void context) {
        String functionName = functionStmt.getName().getId();
        List<QLambdaDefinitionInner.Param> params = functionStmt.getParams().stream()
                .map(varDecl -> new QLambdaDefinitionInner.Param(
                        varDecl.getVariable().getId(),
                        varDecl.getType() == null ? Object.class : varDecl.getType().getClz()
                ))
                .collect(Collectors.toList());
        QLambdaDefinitionInner functionBodyLambda = generateLambdaDefinition(
                subScopeName(FUNCTION, functionCounter++), functionStmt.getBody().getStmtList(), params, context);

        return QList.singletonList(expandStack(
                new DefineFunctionInstruction(newReporterByNode(functionStmt), functionName, functionBodyLambda)
        ));
    }

    @Override
    public QList<QLInstruction> visit(GroupExpr groupExpr, Void context) {
        return groupExpr.getExpr().accept(this, context);
    }

    @Override
    public QList<QLInstruction> visit(Identifier identifier, Void context) {
        return null;
    }

    @Override
    public QList<QLInstruction> visit(IdExpr idExpr, Void context) {
        return QList.singletonList(expandStack(
                new LoadInstruction(newReporterByNode(idExpr), idExpr.getKeyToken().getLexeme()))
        );
    }

    @Override
    public QList<QLInstruction> visit(ImportStmt importStmt, Void context) {
        // import statement has been handled in parser
        return QList.emptyList();
    }

    @Override
    public QList<QLInstruction> visit(LambdaExpr lambdaExpr, Void context) {
        List<QLambdaDefinitionInner.Param> params = lambdaExpr.getParameters().stream()
                .map(varDecl -> new QLambdaDefinitionInner.Param(varDecl.getVariable().getId(),
                        varDecl.getType() == null? Object.class: varDecl.getType().getClz()))
                .collect(Collectors.toList());
        QLambdaDefinitionInner lambdaBody = generateLambdaDefinition(subScopeName(LAMBDA, lambdaCounter++), toStmtList(lambdaExpr.getBody()),
                params, context);
        return QList.singletonList(expandStack(
                new LoadLambdaInstruction(newReporterByNode(lambdaExpr), lambdaBody)
        ));
    }

    @Override
    public QList<QLInstruction> visit(ListExpr listExpr, Void context) {
        List<Expr> elementExprs = listExpr.getElements();
        QList<QLInstruction> elementsInstructions = exprListInstructions(listExpr.getElements(), context);
        QLInstruction newListInstruction = expandStack(
                new NewListInstruction(newReporterByNode(listExpr), elementExprs.size())
        );

        return QList.fromList(elementsInstructions)
                .addLast(newListInstruction);
    }

    @Override
    public QList<QLInstruction> visit(LocalVarDeclareStmt localVarDeclareStmt, Void context) {
        ErrorReporter errorReporter = newReporterByNode(localVarDeclareStmt);
        String varName = localVarDeclareStmt.getVarDecl().getVariable().getId();
        Class<?> declareClz = localVarDeclareStmt.getVarDecl().getType().getClz();
        QList<QLInstruction> initInstructions = localVarInitInstructions(localVarDeclareStmt, errorReporter, context);

        return QList.fromList(initInstructions)
                .addLast(expandStack(
                        new DefineLocalInstruction(errorReporter, varName, declareClz)
                ));
    }

    private QList<QLInstruction> localVarInitInstructions(LocalVarDeclareStmt localVarDeclareStmt,
                                                          ErrorReporter errorReporter,
                                                          Void context) {

        if (localVarDeclareStmt.getInitializer() != null) {
            Class<?> declareClz = localVarDeclareStmt.getVarDecl().getType().getClz();
            if (declareClz.isArray() && localVarDeclareStmt.getInitializer() instanceof ListExpr) {
                // opt array init, not support embed
                ListExpr listExpr = (ListExpr) localVarDeclareStmt.getInitializer();
                return newArrWithExprs(localVarDeclareStmt.getInitializer(), declareClz.getComponentType(),
                        listExpr.getElements(), context);
            } else if (declareClz == Character.class && localVarDeclareStmt.getInitializer() instanceof ConstExpr &&
                    isCharConst((ConstExpr) localVarDeclareStmt.getInitializer())) {
                // opt char init
                String constString = (String)
                        ((ConstExpr) localVarDeclareStmt.getInitializer()).getConstValue();
                return QList.singletonList(expandStack(
                        new ConstInstruction(newReporterByNode(
                                localVarDeclareStmt.getInitializer()), constString.charAt(0))
                ));
            } else {
                return localVarDeclareStmt.getInitializer().accept(this, context);
            }
        } else {
            return QList.singletonList(expandStack(
                    new ConstInstruction(errorReporter, null)
            ));
        }
    }

    @Override
    public QList<QLInstruction> visit(MacroStmt macroStmt, Void context) {
        ErrorReporter errorReporter = newReporterByNode(macroStmt);
        String macroScopeName = subScopeName(MACRO, macroCounter++);
        QvmInstructionGeneratorV2 macroVisitor = newSubVisitor(MACRO, GeneratorScopeV2.ScopeType.MACRO);
        QList<QLInstruction> macroBodyInstructions = macroStmt.getBody().getStmtList().accept(macroVisitor, context);

        QList<QLInstruction> macroInstructions = QList
                .<QLInstruction>singletonList(
                        new NewScopeInstruction(errorReporter, macroScopeName, ExceptionTable.EMPTY))
                .addAll(macroBodyInstructions)
                .addLast(new CloseScopeInstruction(errorReporter, macroScopeName));

        int macroInstructionSize = macroVisitor.instructionSize + 2;
        MacroDefineV2 macroDefine = new MacroDefineV2(
                macroInstructions.toArray(new QLInstruction[macroInstructionSize]),
                macroLastStmt(macroStmt), macroVisitor.maxStackSize);
        generatorScope.defineMacro(macroStmt.getName().getId(), macroDefine);
        return QList.emptyList();
    }

    private Stmt macroLastStmt(MacroStmt macroStmt) {
        List<Stmt> macroStmtList = macroStmt.getBody().getStmtList().getStmts();
        return macroStmtList.isEmpty()? macroStmt: macroStmtList.get(macroStmtList.size() - 1);
    }

    @Override
    public QList<QLInstruction> visit(NewExpr newExpr, Void context) {
        List<Expr> arguments = newExpr.getArguments();
        QList<QLInstruction> argInstructions = exprListInstructions(arguments, context);
        QLInstruction newInstruction = expandStack(
                new NewInstruction(newReporterByNode(newExpr), newExpr.getClazz().getClz(), arguments.size())
        );

        return QList.fromList(argInstructions)
                .addLast(newInstruction);
    }

    @Override
    public QList<QLInstruction> visit(PrefixUnaryOpExpr prefixUnaryOpExpr, Void context) {
        QList<QLInstruction> exprInstructions = prefixUnaryOpExpr.getExpr().accept(this, context);
        String op = prefixUnaryOpExpr.getKeyToken().getLexeme();
        QLInstruction unaryInstruction = expandStack(
                new UnaryInstruction(newReporterByNode(prefixUnaryOpExpr),
                        operatorManager.getPrefixUnaryOperator(op))
        );

        return QList.fromList(exprInstructions)
                .addLast(unaryInstruction);
    }

    @Override
    public QList<QLInstruction> visit(ReturnStmt returnStmt, Void context) {
        ErrorReporter errorReporter = newReporterByNode(returnStmt);
        QList<QLInstruction> returnExprInstructions = returnExprInstructions(returnStmt, errorReporter, context);
        QLInstruction returnInstruction = expandStack(
                new ReturnInstruction(errorReporter, QResult.ResultType.RETURN)
        );

        return QList.fromList(returnExprInstructions)
                .addLast(returnInstruction);
    }

    private QList<QLInstruction> returnExprInstructions(ReturnStmt returnStmt,
                                                        ErrorReporter errorReporter, Void context) {
        if (returnStmt.getExpr() != null) {
            return returnStmt.getExpr().accept(this, context);
        } else {
            return QList.singletonList(expandStack(
                    new ConstInstruction(errorReporter, null)
            ));
        }
    }

    @Override
    public QList<QLInstruction> visit(SuffixUnaryOpExpr suffixUnaryOpExpr, Void context) {
        QList<QLInstruction> exprInstructions = suffixUnaryOpExpr.getExpr().accept(this, context);
        String op = suffixUnaryOpExpr.getKeyToken().getLexeme();
        QLInstruction unaryInstruction = expandStack(new UnaryInstruction(newReporterByNode(suffixUnaryOpExpr),
                operatorManager.getSuffixUnaryOperator(op))
        );

        return QList.fromList(exprInstructions)
                .addLast(unaryInstruction);
    }

    @Override
    public QList<QLInstruction> visit(TernaryExpr ternaryExpr, Void context) {
        QList<QLInstruction> conditionInstructions = ternaryExpr.getCondition().accept(this, context);
        expandStack(JumpIfPopInstruction.INSTANCE);
        QList<QLInstruction> thenInstructions = ternaryExpr.getThenExpr().accept(this, context);
        expandStack(JumpInstruction.INSTANCE);
        int jumpIfPos = instructionSize;
        QList<QLInstruction> elseInstructions = ternaryExpr.getElseExpr().accept(this, context);

        ErrorReporter conditionErrorReporter = newReporterByNode(ternaryExpr.getCondition());
        return QList.fromList(conditionInstructions)
                .addLast(new JumpIfPopInstruction(conditionErrorReporter,
                        false, jumpIfPos))
                .addAll(thenInstructions)
                .addLast(new JumpInstruction(conditionErrorReporter, instructionSize))
                .addAll(elseInstructions);
    }

    @Override
    public QList<QLInstruction> visit(TryCatch tryCatch, Void context) {
        return null;
    }

    @Override
    public QList<QLInstruction> visit(TypeExpr typeExpr, Void context) {
        return QList.singletonList(expandStack(
                new ConstInstruction(newReporterByNode(typeExpr), new MetaClass(typeExpr.getDeclType().getClz()))
        ));
    }

    @Override
    public QList<QLInstruction> visit(WhileStmt whileStmt, Void context) {
        int whileCount = whileCount();
        QLambdaDefinitionInner conditionLambda = generateLambdaDefinitionNoParam(
                subScopeName(WHILE_CONDITION, whileCount), toStmtList(whileStmt.getCondition()), context);
        QLambdaDefinitionInner bodyLambda = generateLoopLambdaDefinitionNoParam(
                subScopeName(WHILE_BODY, whileCount), toStmtList(whileStmt.getBody()), context);
        // condition lambda run in current scope
        // TODO: while 表达式化改造的话需要改这里
        if (conditionLambda.getMaxStackSize() > maxStackSize) {
            this.maxStackSize = conditionLambda.getMaxStackSize();
        }

        return QList.singletonList(expandStack(
                new WhileInstruction(newReporterByNode(whileStmt), conditionLambda, bodyLambda)
        ));
    }

    @Override
    public QList<QLInstruction> visit(IfExpr ifExpr, Void context) {
        ErrorReporter ifErrorReporter = newReporterByNode(ifExpr);
        ErrorReporter conditionErrorReporter = newReporterByNode(ifExpr.getCondition());
        String ifScopeName = subScopeName(IF, ifCounter++);

        QList<QLInstruction> conditionInstructions = ifExpr.getCondition().accept(this, context);
        QLInstruction newScopeInstruction = expandStack(new NewScopeInstruction(ifErrorReporter,
                ifScopeName, ExceptionTable.EMPTY));
        QList<QLInstruction> ifElseBodyInstructions = ifExpr.getElseBranch() == null?
                ifBodyInstructions(conditionErrorReporter, ifExpr.getThenBranch(), ifScopeName, context):
                ifElseBodyInstructions(conditionErrorReporter, ifExpr.getThenBranch(),
                        ifExpr.getElseBranch(), ifScopeName, context);
        QLInstruction closeScopeInstruction = expandStack(new CloseScopeInstruction(ifErrorReporter, ifScopeName));

        return QList.fromList(conditionInstructions)
                .addLast(newScopeInstruction)
                .addAll(ifElseBodyInstructions)
                .addLast(closeScopeInstruction);
    }

    private QList<QLInstruction> ifBodyInstructions(ErrorReporter conditionErrorReporter, Stmt thenBody,
                                                    String ifScopeName, Void context) {
        expandStack(JumpIfPopInstruction.INSTANCE);

        QvmInstructionGeneratorV2 thenSubVisitor = newSubVisitor(
                ifScopeName + THEN, GeneratorScopeV2.ScopeType.BLOCK);
        QList<QLInstruction> thenInstructions = toStmtList(thenBody).accept(thenSubVisitor, context);

        expandStack(JumpInstruction.INSTANCE);
        // no need expand stack for const instruction alone
        QLInstruction elseInstruction = new ConstInstruction(conditionErrorReporter, null);
        expandStackSizeForExprScope(Math.max(thenSubVisitor.maxStackSize, 1),
                thenSubVisitor.instructionSize + 1);

        return QList.<QLInstruction>singletonList(new JumpIfPopInstruction(conditionErrorReporter, false,
                        thenSubVisitor.instructionSize + 2))
                .addAll(thenInstructions)
                .addLast(new JumpInstruction(conditionErrorReporter, thenSubVisitor.instructionSize + 3))
                .addLast(elseInstruction);
    }

    private QList<QLInstruction> ifElseBodyInstructions(ErrorReporter conditionErrorReporter, Stmt thenBody,
                                                        Stmt elseBody, String ifScopeName, Void context) {
        expandStack(JumpIfPopInstruction.INSTANCE);

        QvmInstructionGeneratorV2 thenSubVisitor = newSubVisitor(
                ifScopeName + THEN, GeneratorScopeV2.ScopeType.BLOCK);
        QList<QLInstruction> thenInstructions = toStmtList(thenBody).accept(thenSubVisitor, context);

        expandStack(JumpInstruction.INSTANCE);
        QvmInstructionGeneratorV2 elseSubVisitor = newSubVisitor(
                ifScopeName + ELSE, GeneratorScopeV2.ScopeType.BLOCK);
        QList<QLInstruction> elseInstructions = toStmtList(elseBody).accept(elseSubVisitor, context);
        expandStackSizeForExprScope(Math.max(thenSubVisitor.maxStackSize, elseSubVisitor.maxStackSize),
                thenSubVisitor.instructionSize + elseSubVisitor.instructionSize);

        QLInstruction jumpIfInstruction = new JumpIfPopInstruction(conditionErrorReporter,
                false, thenSubVisitor.instructionSize + 2);
        QLInstruction jumpInstruction = new JumpInstruction(conditionErrorReporter,
                thenSubVisitor.instructionSize + elseSubVisitor.instructionSize + 2);
        return QList.singletonList(jumpIfInstruction)
                .addAll(thenInstructions)
                .addLast(jumpInstruction)
                .addAll(elseInstructions);
    }

    @Override
    public QList<QLInstruction> visit(GetMethodExpr getMethodExpr, Void context) {
        QList<QLInstruction> targetInstructions = getMethodExpr.getExpr().accept(this, context);
        QLInstruction getMethodInstruction = expandStack(new GetMethodInstruction(newReporterByNode(getMethodExpr),
                getMethodExpr.getAttribute().getId())
        );
        return QList.fromList(targetInstructions)
                .addLast(getMethodInstruction);
    }

    @Override
    public QList<QLInstruction> visit(MapExpr mapExpr, Void context) {
        QList<QLInstruction> result = QList.emptyList();
        List<String> keys = new ArrayList<>(mapExpr.getEntries().size());
        for (Map.Entry<String, Expr> entry : mapExpr.getEntries()) {
            keys.add(entry.getKey());
            result.addAll(entry.getValue().accept(this, context));
        }
        return result.addLast(expandStack(
                new NewMapInstruction(newReporterByNode(mapExpr), keys)
        ));
    }

    @Override
    public QList<QLInstruction> visit(MultiNewArrayExpr newArrayDimsExpr, Void context) {
        QList<QLInstruction> dimsInstructions = exprListInstructions(newArrayDimsExpr.getDims(), context);
        QLInstruction multiNewArrayInstruction = expandStack(
                new MultiNewArrayInstruction(newReporterByNode(newArrayDimsExpr),
                        newArrayDimsExpr.getClz(), newArrayDimsExpr.getDims().size())
        );

        return QList.fromList(dimsInstructions)
                .addLast(multiNewArrayInstruction);
    }

    @Override
    public QList<QLInstruction> visit(NewArrayExpr newArrayExpr, Void context) {
        return newArrWithExprs(newArrayExpr, newArrayExpr.getClz(), newArrayExpr.getValues(), context);
    }

    private QList<QLInstruction> newArrWithExprs(SyntaxNode syntaxNode, Class<?> componentClz,
                                                 List<Expr> exprs, Void context) {
        QList<QLInstruction> elements = exprListInstructions(exprs, context);
        QLInstruction newArrayInstruction = expandStack(
                new NewArrayInstruction(newReporterByNode(syntaxNode), componentClz, exprs.size())
        );

        return QList.fromList(elements)
                .addLast(newArrayInstruction);
    }

    private boolean isCharConst(ConstExpr constExpr) {
        Object constValue = constExpr.getConstValue();
        return constValue instanceof String && ((String) constValue).length() == 1;
    }

    private QList<QLInstruction> exprListInstructions(List<Expr> exprs, Void context) {
        QList<QLInstruction> exprInstructions = QList.emptyList();
        for (Expr expr : exprs) {
            exprInstructions.addAll(expr.accept(this, context));
        }
        return exprInstructions;
    }

    private int forCount() {
        return forCounter++;
    }

    private int whileCount() {
        return whileCounter++;
    }

    private StmtList toStmtList(Stmt stmt) {
        if (stmt instanceof Block) {
            return ((Block) stmt).getStmtList();
        } else {
            return new StmtList(stmt.getKeyToken(), Collections.singletonList(stmt));
        }
    }

    private QLInstruction[] expandMacroStack(MacroDefineV2 macroDefine) {
        if (macroDefine.getMaxStackSize() > maxStackSize) {
            maxStackSize = macroDefine.getMaxStackSize();
        }
        return macroDefine.getInstructions();
    }

    private QLInstruction expandStack(QLInstruction qlInstruction) {
        int stackExpandSize = qlInstruction.stackOutput() - qlInstruction.stackInput();
        expandStackSize(stackExpandSize);
        return qlInstruction;
    }

    private void expandStackSize(int stackExpandSize) {
        stackSize += stackExpandSize;
        if (stackSize > maxStackSize) {
            maxStackSize = stackSize;
        }

        instructionSize++;
    }

    private ErrorReporter newReporterByNode(SyntaxNode syntaxNode) {
        return new DefaultErrorReporter(script, syntaxNode.getKeyToken());
    }

    private ErrorReporter newReporterByToken(Token token) {
        return new DefaultErrorReporter(script, token);
    }

    private String subBlockName() {
        return subScopeName(BLOCK, blockCounter++);
    }

    private String subScopeName(String type, int count) {
        return scopeName + SCOPE_SPLIT + type + count;
    }

    private QLambdaDefinitionInner generateLambdaDefinitionNoParam(String scopeName,
                                                                   StmtList stmtList,
                                                                   Void context) {
        return generateLambdaDefinition(scopeName, stmtList, Collections.emptyList(), context);
    }

    private QLambdaDefinitionInner generateLambdaDefinition(String scopeName,
                                                            StmtList stmtList,
                                                            List<QLambdaDefinitionInner.Param> paramsType,
                                                            Void context) {
        QvmInstructionGeneratorV2 subVisitor = newSubVisitor(scopeName, GeneratorScopeV2.ScopeType.FUNCTION);
        QList<QLInstruction> bodyInstructions = stmtList.accept(subVisitor, context);
        return new QLambdaDefinitionInner(scopeName, bodyInstructions
                .toArray(new QLInstruction[subVisitor.instructionSize]), paramsType, subVisitor.maxStackSize);
    }

    private QLambdaDefinitionInner generateLoopLambdaDefinitionNoParam(String scopeName,
                                                                   StmtList stmtList,
                                                                   Void context) {
        return generateLoopLambdaDefinition(scopeName, stmtList, Collections.emptyList(), context);
    }

    private QLambdaDefinitionInner generateLoopLambdaDefinition(String scopeName,
                                                            StmtList stmtList,
                                                            List<QLambdaDefinitionInner.Param> paramsType,
                                                            Void context) {
        QvmInstructionGeneratorV2 subVisitor = newSubVisitor(scopeName, GeneratorScopeV2.ScopeType.LOOP);
        QList<QLInstruction> bodyInstructions = stmtList.accept(subVisitor, context);
        return new QLambdaDefinitionInner(scopeName, bodyInstructions
                .toArray(new QLInstruction[subVisitor.instructionSize]), paramsType, subVisitor.maxStackSize);
    }

    private QvmInstructionGeneratorV2 newSubVisitor(String scopeName, GeneratorScopeV2.ScopeType scopeType) {
        return new QvmInstructionGeneratorV2(script, operatorManager,
                new GeneratorScopeV2(generatorScope, scopeType), scopeName);
    }

    public int getMaxStackSize() {
        return maxStackSize;
    }

    public int getInstructionSize() {
        return instructionSize;
    }
}
