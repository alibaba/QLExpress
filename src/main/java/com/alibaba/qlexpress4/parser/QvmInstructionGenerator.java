package com.alibaba.qlexpress4.parser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.alibaba.qlexpress4.exception.DefaultErrorReporter;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.parser.tree.*;
import com.alibaba.qlexpress4.runtime.ConstLambdaDefinition;
import com.alibaba.qlexpress4.runtime.QLambdaDefinition;
import com.alibaba.qlexpress4.runtime.QLambdaDefinitionInner;
import com.alibaba.qlexpress4.runtime.QResult;
import com.alibaba.qlexpress4.runtime.instruction.*;
import com.alibaba.qlexpress4.runtime.operator.OperatorManager;

/**
 * Author: DQinYuan
 */
public class QvmInstructionGenerator implements QLProgramVisitor<Void, GeneratorScope> {

    private static final String BLOCK_LAMBDA_NAME_PREFIX = "BLOCK_";
    private static final String FOR_LAMBDA_NAME_PREFIX = "FOR_";
    private static final String CONDITION_SUFFIX = "_CONDITION";
    private static final String BODY_SUFFIX = "_BODY";
    private static final String IF_LAMBDA_PREFIX = "IF_";
    private static final String THEN_SUFFIX = "_THEN";
    private static final String ELSE_SUFFIX = "_ELSE";
    private static final String LAMBDA_PREFIX = "LAMBDA_";
    private static final String MACRO_PREFIX = "MACRO_";
    private static final String TERNARY_PREFIX = "TERNARY_";
    private static final String TRY_LAMBDA_PREFIX = "TRY_";
    private static final String CATCH_SUFFIX = "_CATCH";
    private static final String FINAL_SUFFIX = "_FINAL";
    private static final String WHILE_PREFIX = "WHILE_";
    private static final String SHORT_CIRCUIT = "SHORT_CIRCUIT";

    private final String prefix;

    private final String script;

    private final OperatorManager operatorManager;

    private final List<QLInstruction> instructionList = new ArrayList<>();

    private int stackSize;

    private int maxStackSize;

    private int blockCounter = 0;
    private int forCounter = 0;
    private int ifCounter = 0;
    private int lambdaCounter = 0;
    private int macroCounter = 0;
    private int ternaryCounter = 0;
    private int tryCounter = 0;
    private int whileCounter = 0;

    public QvmInstructionGenerator(OperatorManager operatorManager, String prefix, String script) {
        this.operatorManager = operatorManager;
        this.prefix = prefix;
        this.script = script;
    }

    @Override
    public Void visit(Program program, GeneratorScope generatorScope) {
        program.getStmtList().accept(this, generatorScope);
        return null;
    }

    @Override
    public Void visit(StmtList stmtList, GeneratorScope generatorScope) {
        Stmt preStmt = null;
        boolean isMacro = false;
        for (Stmt stmt : stmtList.getStmts()) {
            if (!isMacro && preStmt instanceof Expr) {
                // pop if no acceptor
                addInstruction(new PopInstruction(newReporterByNode(preStmt)));
            }
            isMacro = handleStmt(stmt, generatorScope);
            preStmt = stmt;
        }
        if (preStmt instanceof Expr) {
            addInstruction(new ReturnInstruction(newReporterByNode(preStmt), QResult.ResultType.RETURN));
        }
        return null;
    }

    private boolean handleStmt(Stmt stmt, GeneratorScope generatorScope) {
        if (stmt instanceof IdExpr) {
            NodeInstructions macroInstructions = generatorScope
                .getMacroInstructions(stmt.getKeyToken().getLexeme());
            if (macroInstructions != null) {
                addInstructions(macroInstructions);
                return true;
            }
        }
        stmt.accept(this, generatorScope);
        return false;
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

        // short circuit
        NodeInstructions shortCircuitInstructions = getShortCircuitInstructions(binaryOpExpr, generatorScope);
        if (shortCircuitInstructions != null) {
            int localMaxSize = stackSize + shortCircuitInstructions.getMaxStackSize();
            if (localMaxSize > maxStackSize) {
                maxStackSize = localMaxSize;
            }
            return null;
        }

        binaryOpExpr.getRight().accept(this, generatorScope);
        addInstruction(new OperatorInstruction(newReporterByNode(binaryOpExpr), operatorManager
            .getBinaryOperator(binaryOpExpr.getKeyToken().getLexeme())));
        return null;
    }

    private NodeInstructions getShortCircuitInstructions(BinaryOpExpr binaryOpExpr,
        GeneratorScope generatorScope) {
        String opLexeme = binaryOpExpr.getKeyToken().getLexeme();
        if ("||".equals(opLexeme)) {
            // if (left) true else right
            NodeInstructions nodeInstructions = generateNodeInstructions(SHORT_CIRCUIT,
                binaryOpExpr.getRight(), generatorScope);
            ErrorReporter errorReporter = newReporterByNode(binaryOpExpr);
            addInstruction(new IfInstruction(errorReporter, new ConstLambdaDefinition(true),
                new QLambdaDefinitionInner(SHORT_CIRCUIT, addReturn(errorReporter,
                    nodeInstructions.getInstructions()), Collections.emptyList(),
                    nodeInstructions.getMaxStackSize()), false));
            return nodeInstructions;
        } else if ("&&".equals(opLexeme)) {
            // if (left) right else false
            NodeInstructions nodeInstructions = generateNodeInstructions(SHORT_CIRCUIT,
                binaryOpExpr.getRight(), generatorScope);
            ErrorReporter errorReporter = newReporterByNode(binaryOpExpr);
            addInstruction(new IfInstruction(errorReporter,
                new QLambdaDefinitionInner(SHORT_CIRCUIT,
                    addReturn(errorReporter, nodeInstructions.getInstructions()),
                    Collections.emptyList(), nodeInstructions.getMaxStackSize()),
                new ConstLambdaDefinition(false), false));
            return nodeInstructions;
        }
        return null;
    }

    private static List<QLInstruction> addReturn(ErrorReporter errorReporter, List<QLInstruction> origin) {
        origin.add(new ReturnInstruction(errorReporter, QResult.ResultType.RETURN));
        return origin;
    }

    @Override
    public Void visit(Block block, GeneratorScope generatorScope) {
        addInstruction(new CallConstInstruction(newReporterByNode(block),
            generateLambdaNewScope(blockLambdaName(), block.getStmtList(), generatorScope)));
        return null;
    }

    @Override
    public Void visit(Break aBreak, GeneratorScope generatorScope) {
        addInstruction(new BreakContinueInstruction(newReporterByNode(aBreak), QResult.BREAK_RESULT));
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
        addInstruction(new BreakContinueInstruction(errorReporter, QResult.CONTINUE_RESULT));
        addInstruction(new ConstInstruction(errorReporter, null));
        addInstruction(new ReturnInstruction(errorReporter, QResult.ResultType.RETURN));
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
        QLambdaDefinition bodyLambda = lambdaBodyDefinition(prefix + FOR_LAMBDA_NAME_PREFIX + forCount(),
                forEachStmt.getBody(), generatorScope,
                Collections.singletonList(
                        new QLambdaDefinitionInner.Param(itVar.getVariable().getId(), itVar.getType().getClz())
                ), forEachErrReporter);;
        addInstruction(new ForEachInstruction(forEachErrReporter, bodyLambda));
        return null;
    }

    @Override
    public Void visit(ForStmt forStmt, GeneratorScope generatorScope) {
        int forCount = forCount();
        QLambdaDefinition conditionLambda = generateLambda(
            prefix + FOR_LAMBDA_NAME_PREFIX + forCount + CONDITION_SUFFIX,
            forStmt.getCondition(), generatorScope);
        String bodyLambdaName = prefix + FOR_LAMBDA_NAME_PREFIX + forCount + BODY_SUFFIX;
        QLambdaDefinitionInner bodyLambda = generateLambda(bodyLambdaName,
            forStmt.getBody(), generatorScope, Collections.emptyList(),
            generateNodeInstructions(bodyLambdaName, forStmt.getForUpdate(), generatorScope));
        ErrorReporter forErrReporter = newReporterByNode(forStmt);
        WhileInstruction whileInstruction = new WhileInstruction(forErrReporter,
            conditionLambda, bodyLambda);
        QLambdaDefinitionInner forLambda = generateLambdaNewScope(prefix + FOR_LAMBDA_NAME_PREFIX + forCount,
            forStmt.getForInit(), generatorScope, Collections.emptyList(),
            new NodeInstructions(Collections.singletonList(whileInstruction), 0));
        addInstruction(new CallConstInstruction(forErrReporter, forLambda));
        return null;
    }

    @Override
    public Void visit(FunctionStmt functionStmt, GeneratorScope generatorScope) {
        String functionName = functionStmt.getName().getId();
        QLambdaDefinition functionLambda = generateLambdaNewScope(functionName, functionStmt.getBody().getStmtList(),
                generatorScope, functionStmt.getParams().stream()
                        .map(varDecl ->new QLambdaDefinitionInner.Param(
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

        ErrorReporter ifErrReporter = newReporterByNode(ifExpr);
        int ifCount = ifCount();
        QLambdaDefinition thenLambda = lambdaBodyDefinition(
                prefix + IF_LAMBDA_PREFIX + ifCount + THEN_SUFFIX, ifExpr.getThenBranch(),
                generatorScope, Collections.emptyList(), ifErrReporter);
        addInstruction(new IfInstruction(newReporterByNode(ifExpr), thenLambda,
            ifExpr.getElseBranch() != null ?
                    lambdaBodyDefinition(prefix + IF_LAMBDA_PREFIX + ifCount + ELSE_SUFFIX,
                            ifExpr.getElseBranch(), generatorScope, Collections.emptyList(), ifErrReporter) : null,
                true));
        return null;
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
        for (Expr value : newArrayExpr.getValues()) {
            value.accept(this, context);
        }
        addInstruction(new NewArrayInstruction(newReporterByNode(newArrayExpr),
                newArrayExpr.getClz(), newArrayExpr.getValues().size()));
        return null;
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
        QLambdaDefinition qLambda = lambdaBodyDefinition(lambdaName(), lambdaBody, generatorScope, paramClzes, errorReporter);
        addInstruction(new LoadLambdaInstruction(errorReporter, qLambda));
        return null;
    }

    private QLambdaDefinition lambdaBodyDefinition(String name, Stmt lambdaBody, GeneratorScope generatorScope,
                                                   List<QLambdaDefinitionInner.Param> paramClzes,
                                                   ErrorReporter errorReporter) {
        return lambdaBody instanceof Block?
                generateLambdaNewScope(name, ((Block) lambdaBody).getStmtList(), generatorScope, paramClzes):
                generateLambdaNewScope(name, lambdaBody, generatorScope, paramClzes,
                        new NodeInstructions(Collections.singletonList(
                                new ReturnInstruction(errorReporter, QResult.ResultType.RETURN)), 0)
                );
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
        if (localVarDeclareStmt.getInitializer() != null) {
            localVarDeclareStmt.getInitializer().accept(this, generatorScope);
        } else {
            addInstruction(new ConstInstruction(errorReporter, null));
        }
        addInstruction(new DefineLocalInstruction(errorReporter, varName,
            localVarDeclareStmt.getVarDecl().getType().getClz()));
        return null;
    }

    @Override
    public Void visit(MacroStmt macroStmt, GeneratorScope generatorScope) {
        NodeInstructions macroInstructions = generateNodeInstructions(macroLambdaName(),
            macroStmt.getBody().getStmtList(), generatorScope);
        generatorScope.defineMacro(macroStmt.getName().getId(), macroInstructions);
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

        addInstruction(new ReturnInstruction(errorReporter, QResult.ResultType.CASCADE_RETURN));
        return null;
    }

    @Override
    public Void visit(TernaryExpr ternaryExpr, GeneratorScope generatorScope) {
        ternaryExpr.getCondition().accept(this, generatorScope);

        int ternaryCount = ternaryCount();
        QLambdaDefinition thenLambda = generateLambda(prefix + TERNARY_PREFIX + ternaryCount + THEN_SUFFIX,
            ternaryExpr.getThenExpr(), generatorScope);
        QLambdaDefinition elseLambda = generateLambda(prefix + TERNARY_PREFIX + ternaryCount + ELSE_SUFFIX,
            ternaryExpr.getElseExpr(), generatorScope);
        addInstruction(new IfInstruction(newReporterByNode(ternaryExpr), thenLambda, elseLambda, false));
        return null;
    }

    @Override
    public Void visit(TryCatch tryCatchExpr, GeneratorScope generatorScope) {
        int tryCount = tryCount();
        QLambdaDefinition bodyLambda = generateLambdaNewScope(prefix + TRY_LAMBDA_PREFIX + tryCount,
            tryCatchExpr.getBody(), generatorScope);

        Map<Class<?>, QLambdaDefinition> exceptionTable = new HashMap<>();
        for (int catchCount = 0; catchCount < tryCatchExpr.getTryCatch().size(); catchCount++) {
            TryCatch.CatchClause tryCatch = tryCatchExpr.getTryCatch().get(catchCount);
            String eName = tryCatch.getVariable().getId();
            String lambdaName = prefix + TRY_LAMBDA_PREFIX + catchCount + CATCH_SUFFIX;
            NodeInstructions catchBody = generateNodeInstructionsNewScope(lambdaName,
                tryCatch.getBody(), generatorScope);
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
                    tryCatchExpr, generatorScope) :
                null));
        return null;
    }

    @Override
    public Void visit(TypeExpr typeExpr, GeneratorScope generatorScope) {
        addInstruction(new ConstInstruction(newReporterByNode(typeExpr),
            typeExpr.getDeclType().getClz()));
        return null;
    }

    @Override
    public Void visit(WhileStmt whileStmt, GeneratorScope generatorScope) {
        int whileCount = whileCount();
        QLambdaDefinition conditionLambda = generateLambda(prefix + WHILE_PREFIX + whileCount + CONDITION_SUFFIX,
            whileStmt.getCondition(), generatorScope);
        QLambdaDefinition bodyLambda = generateLambdaNewScope(prefix + WHILE_PREFIX + whileCount + BODY_SUFFIX,
            whileStmt.getBody(), generatorScope);
        addInstruction(new WhileInstruction(newReporterByNode(whileStmt), conditionLambda, bodyLambda));
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
        stackSize += stackExpandSize;
        if (stackSize > maxStackSize) {
            maxStackSize = stackSize;
        }
        instructionList.add(qlInstruction);
    }

    private void addInstructions(NodeInstructions instructions) {
        if (instructions.getMaxStackSize() > maxStackSize) {
            maxStackSize = instructions.getMaxStackSize();
        }
        instructionList.addAll(instructions.getInstructions());
    }

    private QLambdaDefinition generateLambdaNewScope(String name, SyntaxNode targetNode,
        GeneratorScope generatorScope) {
        return generateLambda(name, targetNode, new GeneratorScope(generatorScope));
    }

    private QLambdaDefinition generateLambdaNewScope(String name, SyntaxNode targetNode, GeneratorScope generatorScope,
        List<QLambdaDefinitionInner.Param> paramsType) {
        return generateLambda(name, targetNode, paramsType, new GeneratorScope(generatorScope));
    }

    private QLambdaDefinitionInner generateLambdaNewScope(String name, SyntaxNode targetNode,
        GeneratorScope generatorScope,
        List<QLambdaDefinitionInner.Param> paramsType, NodeInstructions extra) {
        return generateLambda(name, targetNode, new GeneratorScope(generatorScope),
            paramsType, extra);
    }

    private QLambdaDefinition generateLambda(String name, SyntaxNode targetNode, GeneratorScope generatorScope) {
        return generateLambda(name, targetNode, Collections.emptyList(), generatorScope);
    }

    private QLambdaDefinition generateLambda(String name, SyntaxNode targetNode,
        List<QLambdaDefinitionInner.Param> paramsType,
        GeneratorScope generatorScope) {
        NodeInstructions nodeInstructions = generateNodeInstructions(name, targetNode, generatorScope);
        return new QLambdaDefinitionInner(name, nodeInstructions.getInstructions(), paramsType,
            nodeInstructions.getMaxStackSize());
    }

    private QLambdaDefinitionInner generateLambda(String name, SyntaxNode targetNode, GeneratorScope generatorScope,
        List<QLambdaDefinitionInner.Param> paramsType, NodeInstructions extra) {
        NodeInstructions nodeInstructions = generateNodeInstructions(name, targetNode, generatorScope);
        List<QLInstruction> instructions = nodeInstructions.getInstructions();
        instructions.addAll(extra.getInstructions());
        return new QLambdaDefinitionInner(name, instructions, paramsType,
            Math.max(nodeInstructions.getMaxStackSize(), extra.getMaxStackSize()));
    }

    private NodeInstructions generateNodeInstructionsNewScope(String name, SyntaxNode targetNode,
        GeneratorScope generatorScope) {
        return generateNodeInstructions(name, targetNode, new GeneratorScope(generatorScope));
    }

    private NodeInstructions generateNodeInstructions(String name, SyntaxNode targetNode,
        GeneratorScope generatorScope) {
        QvmInstructionGenerator qvmInstructionGenerator = new QvmInstructionGenerator(operatorManager,
            name + "_", script);
        targetNode.accept(qvmInstructionGenerator, generatorScope);
        return new NodeInstructions(qvmInstructionGenerator.getInstructionList(),
            qvmInstructionGenerator.getMaxStackSize());
    }

    private String blockLambdaName() {
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

    private int ternaryCount() {
        return ternaryCounter++;
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
