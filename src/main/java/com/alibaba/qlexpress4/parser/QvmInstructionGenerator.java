package com.alibaba.qlexpress4.parser;

import com.alibaba.qlexpress4.exception.DefaultErrorReporter;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.parser.tree.*;
import com.alibaba.qlexpress4.runtime.*;
import com.alibaba.qlexpress4.runtime.instruction.*;
import com.alibaba.qlexpress4.runtime.operator.OperatorFactory;

import java.util.*;
import java.util.stream.Collectors;

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

    private final String prefix;

    private final String script;

    private final GeneratorScope generatorScope;

    private final List<QLInstruction> instructionList = new ArrayList<>();

    private int blockCounter = 0;
    private int forCounter = 0;
    private int ifCounter = 0;
    private int lambdaCounter = 0;
    private int macroCounter = 0;
    private int ternaryCounter = 0;
    private int tryCounter = 0;
    private int whileCounter = 0;

    public QvmInstructionGenerator(String prefix, String script,GeneratorScope generatorScope) {
        this.prefix = prefix;
        this.script = script;
        this.generatorScope = generatorScope;
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
                // pop if no acceptor
                instructionList.add(new PopInstruction(newReporterByNode(preStmt)));
            }
            preStmt = stmt;
            preStmt.accept(this, generatorScope);
        }
        if (preStmt instanceof Expr) {
            instructionList.add(new ReturnInstruction(newReporterByNode(preStmt), QResult.ResultType.RETURN));
        }
        return null;
    }

    @Override
    public Void visit(IndexCallExpr indexCallExpr, GeneratorScope generatorScope) {
        indexCallExpr.getTarget().accept(this, generatorScope);
        indexCallExpr.getIndex().accept(this, generatorScope);
        instructionList.add(new IndexInstruction(newReporterByNode(indexCallExpr)));
        return null;
    }

    @Override
    public Void visit(AssignExpr assignExpr, GeneratorScope generatorScope) {
        assignExpr.getLeft().accept(this, generatorScope);
        assignExpr.getRight().accept(this, generatorScope);
        instructionList.add(new OperatorInstruction(newReporterByNode(assignExpr), OperatorFactory
                .getOperator(assignExpr.getKeyToken().getLexeme())));
        return null;
    }

    @Override
    public Void visit(BinaryOpExpr binaryOpExpr, GeneratorScope generatorScope) {
        instructionList.add(new OperatorInstruction(newReporterByNode(binaryOpExpr), OperatorFactory
                .getOperator(binaryOpExpr.getKeyToken().getLexeme())));
        return null;
    }

    @Override
    public Void visit(Block block, GeneratorScope generatorScope) {
        instructionList.add(new LoadLambdaInstruction(newReporterByNode(block),
                generateLambdaNewScope(blockLambdaName(), block.getStmtList(), generatorScope)));
        instructionList.add(new CallInstruction(newReporterByNode(block), 0));
        return null;
    }

    @Override
    public Void visit(Break aBreak, GeneratorScope generatorScope) {
        instructionList.add(new BreakInstruction(newReporterByNode(aBreak)));
        return null;
    }

    @Override
    public Void visit(CallExpr callExpr, GeneratorScope generatorScope) {
        Expr target = callExpr.getTarget();
        if (target instanceof FieldCallExpr) {
            // method invoke optimize
            FieldCallExpr fieldCallExpr = (FieldCallExpr) target;
            fieldCallExpr.getExpr().accept(this, generatorScope);
            callExpr.getArguments().forEach(arg -> arg.accept(this, generatorScope));
            Identifier attribute = fieldCallExpr
                    .getAttribute();
            instructionList.add(new MethodInvokeInstruction(newReporterByToken(attribute.getKeyToken()),
                    attribute.getId(), callExpr.getArguments().size()));
        } else {
            // evaluated lambda
            target.accept(this, generatorScope);
            callExpr.getArguments().forEach(arg -> arg.accept(this, generatorScope));
            instructionList.add(new CallInstruction(newReporterByNode(callExpr),
                    callExpr.getArguments().size()));
        }

        return null;
    }

    @Override
    public Void visit(CastExpr castExpr, GeneratorScope generatorScope) {
        castExpr.getTypeExpr().accept(this, generatorScope);
        castExpr.getTarget().accept(this, generatorScope);
        instructionList.add(new CastInstruction(newReporterByNode(castExpr)));
        return null;
    }

    @Override
    public Void visit(ConstExpr constExpr, GeneratorScope generatorScope) {
        instructionList.add(new ConstInstruction(newReporterByNode(constExpr), constExpr.getConstValue()));
        return null;
    }

    @Override
    public Void visit(Continue aContinue, GeneratorScope generatorScope) {
        ErrorReporter errorReporter = newReporterByNode(aContinue);
        instructionList.add(new ConstInstruction(errorReporter, null));
        instructionList.add(new ReturnInstruction(errorReporter, QResult.ResultType.RETURN));
        return null;
    }

    @Override
    public Void visit(FieldCallExpr fieldCallExpr, GeneratorScope generatorScope) {
        fieldCallExpr.getExpr().accept(this, generatorScope);
        String fieldName = fieldCallExpr.getAttribute().getId();
        instructionList.add(new GetFieldInstruction(newReporterByNode(fieldCallExpr), fieldName));
        return null;
    }

    @Override
    public Void visit(ForEachStmt forEachStmt, GeneratorScope generatorScope) {
        forEachStmt.getTarget().accept(this, generatorScope);
        ErrorReporter forEachErrReporter = newReporterByNode(forEachStmt);
        VarDecl itVar = forEachStmt.getItVar();
        QLambdaDefinition bodyLambda = generateLambdaNewScope(prefix + FOR_LAMBDA_NAME_PREFIX + forCount(),
                forEachStmt.getBody() instanceof Block? ((Block) forEachStmt.getBody()).getStmtList():
                forEachStmt.getBody(),
                generatorScope, Collections.singletonList(
                        new QLambdaDefinition.Param(itVar.getVariable().getId(), itVar.getType().getClz())));
        instructionList.add(new ForEachInstruction(forEachErrReporter, bodyLambda));
        return null;
    }

    @Override
    public Void visit(ForStmt forStmt, GeneratorScope generatorScope) {
        int forCount = forCount();
        QLambdaDefinition conditionLambda = generateLambda(prefix + FOR_LAMBDA_NAME_PREFIX + forCount + CONDITION_SUFFIX,
                forStmt.getCondition(), generatorScope);
        String bodyLambdaName = prefix + FOR_LAMBDA_NAME_PREFIX + forCount + BODY_SUFFIX;
        QLambdaDefinition bodyLambda = generateLambda(bodyLambdaName,
                forStmt.getBody(), generatorScope, Collections.emptyList(),
                generateNodeInstructions(bodyLambdaName, forStmt.getForUpdate(), generatorScope));
        ErrorReporter forErrReporter = newReporterByNode(forStmt);
        WhileInstruction whileInstruction = new WhileInstruction(forErrReporter,
                conditionLambda, bodyLambda);
        QLambdaDefinition forLambda = generateLambdaNewScope(prefix + FOR_LAMBDA_NAME_PREFIX + forCount,
                forStmt.getForInit(), generatorScope, Collections.emptyList(),
                Collections.singletonList(whileInstruction));
        instructionList.add(new ConstInstruction(forErrReporter, forLambda));
        instructionList.add(new CallInstruction(forErrReporter, 0));
        return null;
    }

    @Override
    public Void visit(FunctionStmt functionStmt, GeneratorScope generatorScope) {
        String functionName = functionStmt.getName().getId();
        QLambdaDefinition functionLambda = generateLambdaNewScope(functionName, functionStmt.getBody(), generatorScope);
        ErrorReporter errorReporter = newReporterByNode(functionStmt);
        instructionList.add(new ConstInstruction(errorReporter, functionLambda));
        instructionList.add(new DefineLocalInstruction(errorReporter, functionName, QLambda.class));
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
        List<QLInstruction> macroInstructions = generatorScope.getMacroInstructions(id);
        if (macroInstructions != null) {
            // macro
            instructionList.addAll(macroInstructions);
        } else {
            instructionList.add(new LoadInstruction(newReporterByNode(idExpr), id));
        }
        return null;
    }

    @Override
    public Void visit(IfExpr ifExpr, GeneratorScope generatorScope) {
        ifExpr.getCondition().accept(this, generatorScope);

        int ifCount = ifCount();
        QLambdaDefinition thenLambda = generateLambdaNewScope(prefix + IF_LAMBDA_PREFIX + ifCount + THEN_SUFFIX,
                ifExpr.getThenBranch(), generatorScope);
        instructionList.add(new IfInstruction(newReporterByNode(ifExpr), thenLambda,
                ifExpr.getElseBranch() != null? generateLambdaNewScope(prefix + IF_LAMBDA_PREFIX + ifCount + ELSE_SUFFIX,
                        ifExpr.getElseBranch(), generatorScope): null));
        return null;
    }

    @Override
    public Void visit(ImportStmt importStmt, GeneratorScope generatorScope) {
        // import statement has been handled in parser
        return null;
    }

    @Override
    public Void visit(LambdaExpr lambdaExpr, GeneratorScope generatorScope) {
        List<QLambdaDefinition.Param> paramClzes = lambdaExpr.getParameters().stream()
                .map(varDecl -> new QLambdaDefinition.Param(varDecl.getVariable().getId(),
                        varDecl.getType().getClz()))
                .collect(Collectors.toList());
        QLambdaDefinition qLambda = generateLambdaNewScope(lambdaName(), lambdaExpr.getExprBody() == null ?
                lambdaExpr.getBlockBody() : lambdaExpr.getExprBody(), generatorScope, paramClzes);
        instructionList.add(new LoadLambdaInstruction(newReporterByNode(lambdaExpr), qLambda));
        return null;
    }

    @Override
    public Void visit(ListExpr listExpr, GeneratorScope generatorScope) {
        List<Expr> elementExprs = listExpr.getElements();
        elementExprs.forEach(expr -> expr.accept(this, generatorScope));
        instructionList.add(new NewListInstruction(newReporterByNode(listExpr), elementExprs.size()));
        return null;
    }

    @Override
    public Void visit(LocalVarDeclareStmt localVarDeclareStmt, GeneratorScope generatorScope) {
        ErrorReporter errorReporter = newReporterByNode(localVarDeclareStmt);
        String varName = localVarDeclareStmt.getVarDecl().getVariable().getId();
        if (localVarDeclareStmt.getInitializer() != null) {
            localVarDeclareStmt.getInitializer().accept(this, generatorScope);
        } else {
            instructionList.add(new ConstInstruction(errorReporter, null));
        }
        instructionList.add(new DefineLocalInstruction(errorReporter, varName,
                localVarDeclareStmt.getVarDecl().getType().getClz()));
        return null;
    }

    @Override
    public Void visit(MacroStmt macroStmt, GeneratorScope generatorScope) {
        List<QLInstruction> macroInstructions = generateNodeInstructions(macroLambdaName(),
                macroStmt.getBody().getStmtList(), generatorScope);
        generatorScope.defineMacro(macroStmt.getName().getId(), macroInstructions);
        return null;
    }

    @Override
    public Void visit(NewExpr newExpr, GeneratorScope generatorScope) {
        List<Expr> arguments = newExpr.getArguments();
        arguments.forEach(argExpr -> argExpr.accept(this, generatorScope));

        Class<?> clz = newExpr.getClazz().getClz();
        instructionList.add(new NewInstruction(newReporterByNode(newExpr), clz, arguments.size()));
        return null;
    }

    @Override
    public Void visit(PrefixUnaryOpExpr prefixUnaryOpExpr, GeneratorScope generatorScope) {
        prefixUnaryOpExpr.getExpr().accept(this, generatorScope);
        String op = prefixUnaryOpExpr.getKeyToken().getLexeme();
        instructionList.add(new UnaryInstruction(newReporterByNode(prefixUnaryOpExpr),
                OperatorFactory.getPrefixUnaryOperator(op)));
        return null;
    }

    @Override
    public Void visit(SuffixUnaryOpExpr suffixUnaryOpExpr, GeneratorScope generatorScope) {
        suffixUnaryOpExpr.getExpr().accept(this, generatorScope);
        String op = suffixUnaryOpExpr.getKeyToken().getLexeme();
        instructionList.add(new UnaryInstruction(newReporterByNode(suffixUnaryOpExpr),
                OperatorFactory.getSuffixUnaryOperator(op)));
        return null;
    }

    @Override
    public Void visit(ReturnStmt returnStmt, GeneratorScope generatorScope) {
        ErrorReporter errorReporter = newReporterByNode(returnStmt);
        if (returnStmt.getExpr() != null) {
            returnStmt.getExpr().accept(this, generatorScope);
        } else {
            instructionList.add(new ConstInstruction(errorReporter, null));
        }

        instructionList.add(new ReturnInstruction(errorReporter, QResult.ResultType.CASCADE_RETURN));
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
        instructionList.add(new IfInstruction(newReporterByNode(ternaryExpr), thenLambda, elseLambda));
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
            List<QLInstruction> catchBody = generateNodeInstructionsNewScope(lambdaName,
                    tryCatch.getBody(), generatorScope);
            for (int exceptionCount = 0; exceptionCount < tryCatch.getExceptions().size(); exceptionCount++) {
                DeclType exceptionType = tryCatch.getExceptions().get(exceptionCount);
                QLambdaDefinition.Param eParam = new QLambdaDefinition.Param(eName, exceptionType.getClz());
                QLambdaDefinition exceptionHandlerDefinition = new QLambdaDefinition(lambdaName, catchBody,
                        Collections.singletonList(eParam), 0);
                exceptionTable.put(exceptionType.getClz(), exceptionHandlerDefinition);
            }
        }

        instructionList.add(new TryCatchInstruction(newReporterByNode(tryCatchExpr), bodyLambda, exceptionTable,
                tryCatchExpr.getTryFinal() != null?
                        generateLambdaNewScope(prefix + TRY_LAMBDA_PREFIX + tryCount + FINAL_SUFFIX,
                                tryCatchExpr, generatorScope) :
                        null)
        );
        return null;
    }

    @Override
    public Void visit(TypeExpr typeExpr, GeneratorScope generatorScope) {
        instructionList.add(new ConstInstruction(newReporterByNode(typeExpr),
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
        instructionList.add(new WhileInstruction(newReporterByNode(whileStmt), conditionLambda, bodyLambda));
        return null;
    }

    public List<QLInstruction> getInstructionList() {
        return instructionList;
    }

    private QLambdaDefinition generateLambdaNewScope(String name, SyntaxNode targetNode, GeneratorScope generatorScope) {
        return generateLambda(name, targetNode, new GeneratorScope(generatorScope),
                Collections.emptyList(), Collections.emptyList());
    }

    private QLambdaDefinition generateLambdaNewScope(String name, SyntaxNode targetNode, GeneratorScope generatorScope,
                                             List<QLambdaDefinition.Param> paramsType) {
        return generateLambda(name, targetNode, new GeneratorScope(generatorScope),
                paramsType, Collections.emptyList());
    }

    private QLambdaDefinition generateLambdaNewScope(String name, SyntaxNode targetNode, GeneratorScope generatorScope,
                                             List<QLambdaDefinition.Param> paramsType, List<QLInstruction> extra) {
        return generateLambda(name, targetNode, new GeneratorScope(generatorScope),
                paramsType, extra);
    }

    private QLambdaDefinition generateLambda(String name, SyntaxNode targetNode, GeneratorScope generatorScope) {
        return generateLambda(name, targetNode, generatorScope, Collections.emptyList(), Collections.emptyList());
    }

    private QLambdaDefinition generateLambda(String name, SyntaxNode targetNode, GeneratorScope generatorScope,
                                   List<QLambdaDefinition.Param> paramsType, List<QLInstruction> extra) {
        List<QLInstruction> instructionList = generateNodeInstructions(name, targetNode, generatorScope);
        instructionList.addAll(extra);
        return new QLambdaDefinition(name, instructionList, paramsType, 0);
    }

    private List<QLInstruction> generateNodeInstructionsNewScope(String name, SyntaxNode targetNode,
                                                         GeneratorScope generatorScope) {
        return generateNodeInstructions(name, targetNode, new GeneratorScope(generatorScope));
    }

    private List<QLInstruction> generateNodeInstructions(String name, SyntaxNode targetNode,
                                                         GeneratorScope generatorScope) {
        QvmInstructionGenerator qvmInstructionGenerator = new QvmInstructionGenerator(name + "_",
                script, generatorScope);
        targetNode.accept(qvmInstructionGenerator, generatorScope);
        return qvmInstructionGenerator.getInstructionList();
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
}
