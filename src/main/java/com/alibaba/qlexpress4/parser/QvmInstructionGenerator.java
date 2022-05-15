package com.alibaba.qlexpress4.parser;

import com.alibaba.qlexpress4.exception.DefaultErrorReporter;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.parser.tree.*;
import com.alibaba.qlexpress4.runtime.QLambda;
import com.alibaba.qlexpress4.runtime.QLambdaInner;
import com.alibaba.qlexpress4.runtime.QVm;
import com.alibaba.qlexpress4.runtime.instruction.*;
import com.alibaba.qlexpress4.runtime.operator.OperatorFactory;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Author: DQinYuan
 */
public class QvmInstructionGenerator implements QLProgramVisitor<Void, Void> {

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

    private final QVm qVm;

    private final String script;

    private final List<QLInstruction> instructionList = new ArrayList<>();

    private int blockCounter = 0;
    private int forCounter = 0;
    private int ifCounter = 0;
    private int lambdaCounter = 0;
    private int macroCounter = 0;
    private int ternaryCounter = 0;
    private int tryCounter = 0;
    private int whileCounter = 0;

    public QvmInstructionGenerator(QVm qVm, String script) {
        this.qVm = qVm;
        this.script = script;
    }

    @Override
    public Void visit(Program program, Void context) {
        program.getStmtList().forEach(stmt -> stmt.accept(this, context));
        return null;
    }

    @Override
    public Void visit(ArrayCallExpr arrayCallExpr, Void context) {
        arrayCallExpr.getTarget().accept(this, context);
        arrayCallExpr.getIndex().accept(this, context);
        instructionList.add(new IndexInstruction(newReporterByNode(arrayCallExpr)));
        return null;
    }

    @Override
    public Void visit(AssignExpr assignExpr, Void context) {
        assignExpr.getLeft().accept(this, context);
        assignExpr.getRight().accept(this, context);
        instructionList.add(new OperatorInstruction(newReporterByNode(assignExpr), OperatorFactory
                .getOperator(assignExpr.getKeyToken().getLexeme())));
        return null;
    }

    @Override
    public Void visit(BinaryOpExpr binaryOpExpr, Void context) {
        instructionList.add(new OperatorInstruction(newReporterByNode(binaryOpExpr), OperatorFactory
                .getOperator(binaryOpExpr.getKeyToken().getLexeme())));
        return null;
    }

    @Override
    public Void visit(Block block, Void context) {
        instructionList.add(new ConstInstruction(newReporterByNode(block),
                generateLambda(blockLambdaName(), block, context)));
        instructionList.add(new CallInstruction(newReporterByNode(block), 0));
        return null;
    }

    @Override
    public Void visit(Break aBreak, Void context) {
        instructionList.add(new BreakInstruction(newReporterByNode(aBreak)));
        return null;
    }

    @Override
    public Void visit(CallExpr callExpr, Void context) {
        callExpr.getTarget().accept(this, context);
        callExpr.getArguments().forEach(arg -> arg.accept(this, context));
        instructionList.add(new CallInstruction(newReporterByNode(callExpr),
                callExpr.getArguments().size()));
        return null;
    }

    @Override
    public Void visit(CastExpr castExpr, Void context) {
        castExpr.getTypeExpr().accept(this, context);
        castExpr.getTarget().accept(this, context);
        instructionList.add(new CastInstruction(newReporterByNode(castExpr)));
        return null;
    }

    @Override
    public Void visit(ConstExpr constExpr, Void context) {
        instructionList.add(new ConstInstruction(newReporterByNode(constExpr), constExpr.getConstValue()));
        return null;
    }

    @Override
    public Void visit(Continue aContinue, Void context) {
        ErrorReporter errorReporter = newReporterByNode(aContinue);
        instructionList.add(new ConstInstruction(errorReporter, null));
        instructionList.add(new ReturnInstruction(errorReporter));
        return null;
    }

    @Override
    public Void visit(EmptyStmt emptyStmt, Void context) {
        return null;
    }

    @Override
    public Void visit(FieldCallExpr fieldCallExpr, Void context) {
        fieldCallExpr.getExpr().accept(this, context);
        String fieldName = fieldCallExpr.getAttribute().getId();
        instructionList.add(new GetFieldInstruction(newReporterByNode(fieldCallExpr), fieldName));
        return null;
    }

    @Override
    public Void visit(ForEachStmt forEachStmt, Void context) {
        forEachStmt.getTarget().accept(this, context);
        ErrorReporter forEachErrReporter = newReporterByNode(forEachStmt);
        VarDecl itVar = forEachStmt.getItVar();
        QLambda bodyLambda = generateLambda(FOR_LAMBDA_NAME_PREFIX + forCount(), forEachStmt.getBody(), context,
                Collections.singletonList(
                        new QLambdaInner.Param(itVar.getVariable().getId(), itVar.getType().getClz())));
        instructionList.add(new ForEachInstruction(forEachErrReporter, bodyLambda));
        return null;
    }

    @Override
    public Void visit(ForStmt forStmt, Void context) {
        int forCount = forCount();
        QLambda conditionLambda = generateLambda(FOR_LAMBDA_NAME_PREFIX + forCount + CONDITION_SUFFIX,
                forStmt.getCondition(), context);
        QLambda bodyLambda = generateLambda(FOR_LAMBDA_NAME_PREFIX + forCount + BODY_SUFFIX,
                forStmt.getBody(), context, Collections.emptyList(),
                generateNodeInstructions(forStmt.getForUpdate(), context));
        ErrorReporter forErrReporter = newReporterByNode(forStmt);
        WhileInstruction whileInstruction = new WhileInstruction(forErrReporter,
                conditionLambda, bodyLambda);
        QLambda forLambda = generateLambda(FOR_LAMBDA_NAME_PREFIX + forCount, forStmt.getForInit(), context,
                Collections.emptyList(), Collections.singletonList(whileInstruction));
        instructionList.add(new ConstInstruction(forErrReporter, forLambda));
        instructionList.add(new CallInstruction(forErrReporter, 0));
        return null;
    }

    @Override
    public Void visit(FunctionStmt functionStmt, Void context) {
        String functionName = functionStmt.getName().getId();
        QLambda functionLambda = generateLambda(functionName, functionStmt.getBody(), context);
        ErrorReporter errorReporter = newReporterByNode(functionStmt);
        instructionList.add(new ConstInstruction(errorReporter, functionLambda));
        instructionList.add(new DefineLocalInstruction(errorReporter, functionName));
        return null;
    }

    @Override
    public Void visit(GroupExpr groupExpr, Void context) {
        groupExpr.getExpr().accept(this, context);
        return null;
    }

    @Override
    public Void visit(Identifier identifier, Void context) {
        return null;
    }

    @Override
    public Void visit(IdExpr idExpr, Void context) {
        instructionList.add(new LoadInstruction(newReporterByNode(idExpr)));
        return null;
    }

    @Override
    public Void visit(IfStmt ifStmt, Void context) {
        ifStmt.getCondition().accept(this, context);

        int ifCount = ifCount();
        QLambda thenLambda = generateLambda(IF_LAMBDA_PREFIX + ifCount + THEN_SUFFIX,
                ifStmt.getThenBranch(), context);
        instructionList.add(new IfInstruction(newReporterByNode(ifStmt), thenLambda,
                ifStmt.getElseBranch() != null? generateLambda(IF_LAMBDA_PREFIX + ifCount + ELSE_SUFFIX,
                        ifStmt.getElseBranch(), context): null));
        return null;
    }

    @Override
    public Void visit(ImportStmt importStmt, Void context) {
        // import statement has been handled in parser
        return null;
    }

    @Override
    public Void visit(LambdaExpr lambdaExpr, Void context) {
        List<QLambdaInner.Param> paramClzes = lambdaExpr.getParameters().stream()
                .map(varDecl -> new QLambdaInner.Param(varDecl.getVariable().getId(),
                        varDecl.getType().getClz()))
                .collect(Collectors.toList());
        QLambda qLambda = generateLambda(lambdaName(), lambdaExpr.getExprBody() == null ? lambdaExpr.getBlockBody() :
                        lambdaExpr.getExprBody(), context, paramClzes);
        instructionList.add(new ConstInstruction(newReporterByNode(lambdaExpr), qLambda));
        return null;
    }

    @Override
    public Void visit(ListExpr listExpr, Void context) {
        List<Expr> elementExprs = listExpr.getElements();
        elementExprs.forEach(expr -> expr.accept(this, context));
        instructionList.add(new NewListInstruction(newReporterByNode(listExpr), elementExprs.size()));
        return null;
    }

    @Override
    public Void visit(LocalVarDeclareStmt localVarDeclareStmt, Void context) {
        ErrorReporter errorReporter = newReporterByNode(localVarDeclareStmt);
        String varName = localVarDeclareStmt.getVarDecl().getVariable().getId();
        if (localVarDeclareStmt.getInitializer() != null) {
            localVarDeclareStmt.getInitializer().accept(this, context);
        } else {
            instructionList.add(new ConstInstruction(errorReporter, null));
        }
        instructionList.add(new DefineLocalInstruction(errorReporter, varName));
        return null;
    }

    @Override
    public Void visit(MacroStmt macroStmt, Void context) {
        QLambda macroLambda = generateLambda(macroLambdaName(), macroStmt.getBody(), context);
        ErrorReporter errorReporter = newReporterByNode(macroStmt);
        instructionList.add(new ConstInstruction(errorReporter, macroLambda));
        instructionList.add(new DefineLocalInstruction(errorReporter, macroStmt.getName().getId()));
        return null;
    }

    @Override
    public Void visit(NewExpr newExpr, Void context) {
        List<Expr> arguments = newExpr.getArguments();
        arguments.forEach(argExpr -> argExpr.accept(this, context));

        Class<?> clz = newExpr.getClazz().getClz();
        instructionList.add(new NewInstruction(newReporterByNode(newExpr), clz, arguments.size()));
        return null;
    }

    @Override
    public Void visit(PrefixUnaryOpExpr prefixUnaryOpExpr, Void context) {
        prefixUnaryOpExpr.getExpr().accept(this, context);
        String op = prefixUnaryOpExpr.getKeyToken().getLexeme();
        instructionList.add(new UnaryInstruction(newReporterByNode(prefixUnaryOpExpr),
                OperatorFactory.getPrefixUnaryOperator(op)));
        return null;
    }

    @Override
    public Void visit(SuffixUnaryOpExpr suffixUnaryOpExpr, Void context) {
        suffixUnaryOpExpr.getExpr().accept(this, context);
        String op = suffixUnaryOpExpr.getKeyToken().getLexeme();
        instructionList.add(new UnaryInstruction(newReporterByNode(suffixUnaryOpExpr),
                OperatorFactory.getSuffixUnaryOperator(op)));
        return null;
    }

    @Override
    public Void visit(ReturnStmt returnStmt, Void context) {
        ErrorReporter errorReporter = newReporterByNode(returnStmt);
        if (returnStmt.getExpr() != null) {
            returnStmt.getExpr().accept(this, context);
        } else {
            instructionList.add(new ConstInstruction(errorReporter, null));
        }

        instructionList.add(new ReturnInstruction(errorReporter));
        return null;
    }

    @Override
    public Void visit(TernaryExpr ternaryExpr, Void context) {
        ternaryExpr.getCondition().accept(this, context);

        int ternaryCount = ternaryCount();
        QLambda thenLambda = generateLambda(TERNARY_PREFIX + ternaryCount + THEN_SUFFIX,
                ternaryExpr.getThenExpr(), context);
        QLambda elseLambda = generateLambda(TERNARY_PREFIX + ternaryCount + ELSE_SUFFIX,
                ternaryExpr.getElseExpr(), context);
        instructionList.add(new IfInstruction(newReporterByNode(ternaryExpr), thenLambda, elseLambda));
        return null;
    }

    @Override
    public Void visit(TryCatchStmt tryCatchStmt, Void context) {
        int tryCount = tryCount();
        QLambda bodyLambda = generateLambda(TRY_LAMBDA_PREFIX + tryCount,
                tryCatchStmt.getBody(), context);

        Map<Class<?>, QLambda> exceptionTable = new HashMap<>();
        for (int catchCount = 0; catchCount < tryCatchStmt.getTryCatch().size(); catchCount++) {
            TryCatchStmt.CatchClause tryCatch = tryCatchStmt.getTryCatch().get(catchCount);
            String eName = tryCatch.getVariable().getId();
            List<QLInstruction> catchBody = generateNodeInstructions(tryCatch.getBody(), context);
            for (int exceptionCount = 0; exceptionCount < tryCatch.getExceptions().size(); exceptionCount++) {
                DeclType exceptionType = tryCatch.getExceptions().get(exceptionCount);
                QLambdaInner.Param eParam = new QLambdaInner.Param(eName, exceptionType.getClz());
                QLambdaInner qLambdaInner = new QLambdaInner(qVm, TRY_LAMBDA_PREFIX + catchCount + CATCH_SUFFIX + exceptionCount,
                        catchBody, Collections.singletonList(eParam));
                exceptionTable.put(exceptionType.getClz(), qLambdaInner);
            }
        }

        instructionList.add(new TryCatchInstruction(newReporterByNode(tryCatchStmt), bodyLambda, exceptionTable,
                tryCatchStmt.getTryFinal() != null?
                        generateLambda(TRY_LAMBDA_PREFIX + tryCount + FINAL_SUFFIX, tryCatchStmt, context): null)
        );
        return null;
    }

    @Override
    public Void visit(TypeExpr typeExpr, Void context) {
        instructionList.add(new ConstInstruction(newReporterByNode(typeExpr),
                typeExpr.getDeclType().getClz()));
        return null;
    }

    @Override
    public Void visit(WhileStmt whileStmt, Void context) {
        int whileCount = whileCount();
        QLambda conditionLambda = generateLambda(WHILE_PREFIX + whileCount + CONDITION_SUFFIX,
                whileStmt.getCondition(), context);
        QLambda bodyLambda = generateLambda(WHILE_PREFIX + whileCount + BODY_SUFFIX,
                whileStmt.getBody(), context);
        instructionList.add(new WhileInstruction(newReporterByNode(whileStmt), conditionLambda, bodyLambda));
        return null;
    }

    public List<QLInstruction> getInstructionList() {
        return instructionList;
    }

    private QLambda generateLambda(String name, SyntaxNode targetNode, Void context) {
        return generateLambda(name, targetNode, context, Collections.emptyList(), Collections.emptyList());
    }

    private QLambda generateLambda(String name, SyntaxNode targetNode, Void context,
                                   List<QLambdaInner.Param> paramsType) {
        return generateLambda(name, targetNode, context, paramsType, Collections.emptyList());
    }

    private QLambda generateLambda(String name, SyntaxNode targetNode, Void context,
                                   List<QLambdaInner.Param> paramsType, List<QLInstruction> extra) {
        List<QLInstruction> instructionList = generateNodeInstructions(targetNode, context);
        instructionList.addAll(extra);
        return new QLambdaInner(qVm, name, instructionList, paramsType);
    }

    private List<QLInstruction> generateNodeInstructions(SyntaxNode targetNode, Void context) {
        QvmInstructionGenerator qvmInstructionGenerator = new QvmInstructionGenerator(qVm, script);
        targetNode.accept(qvmInstructionGenerator, context);
        return qvmInstructionGenerator.getInstructionList();
    }

    private String blockLambdaName() {
        return BLOCK_LAMBDA_NAME_PREFIX + blockCounter++;
    }

    private String lambdaName() {
        return LAMBDA_PREFIX + lambdaCounter++;
    }

    private int forCount() {
        return forCounter++;
    }

    private int ifCount() {
        return ifCounter++;
    }

    private String macroLambdaName() {
        return MACRO_PREFIX + macroCounter++;
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
