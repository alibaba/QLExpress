package com.alibaba.qlexpress4.parser;

import com.alibaba.qlexpress4.exception.DefaultErrorReporter;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.parser.tree.*;
import com.alibaba.qlexpress4.runtime.QLambda;
import com.alibaba.qlexpress4.runtime.QLambdaInner;
import com.alibaba.qlexpress4.runtime.QVm;
import com.alibaba.qlexpress4.runtime.instruction.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Author: DQinYuan
 */
public class QvmInstructionGenerator implements QLProgramVisitor<Void, Void> {

    private static final String BLOCK_LAMBDA_NAME_PREFIX = "BLOCK_";

    private final QVm qVm;

    private final String script;

    private final List<QLInstruction> instructionList = new ArrayList<>();

    private int blockCounter = 0;

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
        instructionList.add(new IndexInstruction(newReporterByKeyToken(arrayCallExpr)));
        return null;
    }

    @Override
    public Void visit(AssignExpr assignExpr, Void context) {
        assignExpr.getLeft().accept(this, context);
        assignExpr.getRight().accept(this, context);
        // TODO: assignInstruction or '=' operator
        return null;
    }

    @Override
    public Void visit(BinaryOpExpr binaryOpExpr, Void context) {
        // TODO: operator framework
        return null;
    }

    @Override
    public Void visit(Block block, Void context) {
        instructionList.add(new ConstInstruction(newReporterByKeyToken(block), generateLambda(block, context)));
        instructionList.add(new CallInstruction(newReporterByKeyToken(block), 0));
        return null;
    }

    @Override
    public Void visit(Break aBreak, Void context) {
        instructionList.add(new BreakInstruction(newReporterByKeyToken(aBreak)));
        return null;
    }

    @Override
    public Void visit(CallExpr callExpr, Void context) {
        callExpr.getTarget().accept(this, context);
        callExpr.getArguments().forEach(arg -> arg.accept(this, context));
        instructionList.add(new CallInstruction(newReporterByKeyToken(callExpr),
                callExpr.getArguments().size()));
        return null;
    }

    @Override
    public Void visit(CastExpr castExpr, Void context) {
        castExpr.getTypeExpr().accept(this, context);
        castExpr.getTarget().accept(this, context);
        instructionList.add(new CastInstruction(newReporterByKeyToken(castExpr)));
        return null;
    }

    @Override
    public Void visit(ConstExpr constExpr, Void context) {
        instructionList.add(new ConstInstruction(newReporterByKeyToken(constExpr), constExpr.getConstValue()));
        return null;
    }

    @Override
    public Void visit(Continue aContinue, Void context) {
        ErrorReporter errorReporter = newReporterByKeyToken(aContinue);
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
        String fieldName = fieldCallExpr.getAttribute().getKeyToken().getLexeme();
        instructionList.add(new GetFieldInstruction(newReporterByKeyToken(fieldCallExpr), fieldName));
        return null;
    }

    @Override
    public Void visit(ForEachStmt forEachStmt, Void context) {
        forEachStmt.getTarget().accept(this, context);
        ErrorReporter forEachErrReporter = newReporterByKeyToken(forEachStmt);
        instructionList.add(new IteratorInstruction(forEachErrReporter));
        // TODO: delete dup instruction, add StackParamLambda
        return null;
    }

    @Override
    public Void visit(ForStmt forStmt, Void context) {
        QLambda conditionLambda = generateLambda(forStmt.getCondition(), context);
        QLambda bodyLambda = generateLambda(forStmt.getBody(), context,
                Collections.emptyList(), generateNodeInstructions(forStmt.getForUpdate(), context));
        ErrorReporter forErrReporter = newReporterByKeyToken(forStmt);
        WhileInstruction whileInstruction = new WhileInstruction(forErrReporter,
                conditionLambda, bodyLambda);
        QLambda forLambda = generateLambda(forStmt.getForInit(), context,
                Collections.emptyList(), Collections.singletonList(whileInstruction));
        instructionList.add(new ConstInstruction(forErrReporter, forLambda));
        instructionList.add(new CallInstruction(forErrReporter, 0));
        return null;
    }

    @Override
    public Void visit(FunctionStmt functionStmt, Void context) {
        String functionName = functionStmt.getName().getKeyToken().getLexeme();
        QLambda functionLambda = generateLambda(functionStmt.getBody(), context);
        // TODO: DefineLocalInstruction
        // TODO: operator =
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
        // TODO: loadGlobalInstruction
        return null;
    }

    @Override
    public Void visit(IfStmt ifStmt, Void context) {
        ifStmt.getCondition().accept(this, context);
        QLambda thenLambda = generateLambda(ifStmt.getThenBranch(), context);
        QLambda elseLambda = generateLambda(ifStmt.getElseBranch(), context);
        instructionList.add(new IfInstruction(newReporterByKeyToken(ifStmt), thenLambda, elseLambda));
        return null;
    }

    @Override
    public Void visit(ImportStmt importStmt, Void context) {
        // import statement has been handled in parser
        return null;
    }

    @Override
    public Void visit(LambdaExpr lambdaExpr, Void context) {
        List<Class<?>> paramClzes = lambdaExpr.getParameters().stream()
                .map(varDecl -> varDecl.getType().getClz())
                .collect(Collectors.toList());
        QLambda qLambda = generateLambda(lambdaExpr.getExprBody() == null ? lambdaExpr.getBlockBody() :
                        lambdaExpr.getExprBody(), context, paramClzes, Collections.emptyList());
        instructionList.add(new ConstInstruction(newReporterByKeyToken(lambdaExpr), qLambda));
        return null;
    }

    @Override
    public Void visit(ListExpr listExpr, Void context) {
        List<Expr> elementExprs = listExpr.getElements();
        elementExprs.forEach(expr -> expr.accept(this, context));
        instructionList.add(new NewListInstruction(newReporterByKeyToken(listExpr), elementExprs.size()));
        return null;
    }

    @Override
    public Void visit(LocalVarDeclareStmt localVarDeclareStmt, Void context) {
        VarDecl varDecl = localVarDeclareStmt.getVarDecl();
        // TODO: defineLocalInstruction
        return null;
    }

    @Override
    public Void visit(MacroStmt macroStmt, Void context) {
        QLambda macroLambda = generateLambda(macroStmt.getBody(), context);
        // TODO: defineLocalInstruction
        return null;
    }

    @Override
    public Void visit(NewExpr newExpr, Void context) {
        List<Expr> arguments = newExpr.getArguments();
        arguments.forEach(argExpr -> argExpr.accept(this, context));

        Class<?> clz = newExpr.getClazz().getClz();
        instructionList.add(new NewInstruction(newReporterByKeyToken(newExpr), clz, arguments.size()));
        return null;
    }

    @Override
    public Void visit(PrefixUnaryOpExpr prefixUnaryOpExpr, Void context) {
        // TODO: unaryOperatorInstruction
        return null;
    }

    @Override
    public Void visit(SuffixUnaryOpExpr suffixUnaryOpExpr, Void context) {
        // TODO: unaryOperatorInstruction
        return null;
    }

    @Override
    public Void visit(ReturnStmt aReturnStmt, Void context) {
        aReturnStmt.getExpr().accept(this, context);
        instructionList.add(new ReturnInstruction(newReporterByKeyToken(aReturnStmt)));
        return null;
    }

    @Override
    public Void visit(TernaryExpr ternaryExpr, Void context) {
        ternaryExpr.getCondition().accept(this, context);
        QLambda thenLambda = generateLambda(ternaryExpr.getThenExpr(), context);
        QLambda elseLambda = generateLambda(ternaryExpr.getElseExpr(), context);
        instructionList.add(new IfInstruction(newReporterByKeyToken(ternaryExpr), thenLambda, elseLambda));
        return null;
    }

    @Override
    public Void visit(TryCatchStmt tryCatchStmt, Void context) {
        QLambda bodyLambda = generateLambda(tryCatchStmt.getBody(), context);

        Map<Class<?>, QLambda> exceptionTable = new HashMap<>();
        for (TryCatchStmt.CatchClause tryCatch : tryCatchStmt.getTryCatch()) {
            for (DeclType exceptionType : tryCatch.getExceptions()) {
                // TODO: qLambda add parameter name
            }
        }

        return null;
    }

    @Override
    public Void visit(TypeExpr typeExpr, Void context) {
        instructionList.add(new ConstInstruction(newReporterByKeyToken(typeExpr),
                typeExpr.getDeclType().getClz()));
        return null;
    }

    @Override
    public Void visit(WhileStmt whileStmt, Void context) {
        QLambda conditionLambda = generateLambda(whileStmt.getCondition(), context);
        QLambda bodyLambda = generateLambda(whileStmt.getBody(), context);
        instructionList.add(new WhileInstruction(newReporterByKeyToken(whileStmt), conditionLambda, bodyLambda));
        return null;
    }

    public List<QLInstruction> getInstructionList() {
        return instructionList;
    }

    private QLambda generateLambda(SyntaxNode targetNode, Void context) {
        return generateLambda(targetNode, context, Collections.emptyList(), Collections.emptyList());
    }

    private QLambda generateLambda(SyntaxNode targetNode, Void context,
                                   List<Class<?>> paramsType, List<QLInstruction> extra) {
        List<QLInstruction> instructionList = generateNodeInstructions(targetNode, context);
        instructionList.addAll(extra);
        return new QLambdaInner(qVm, blockLambdaName(), instructionList, paramsType);
    }

    private List<QLInstruction> generateNodeInstructions(SyntaxNode targetNode, Void context) {
        QvmInstructionGenerator qvmInstructionGenerator = new QvmInstructionGenerator(qVm, script);
        targetNode.accept(qvmInstructionGenerator, context);
        return qvmInstructionGenerator.getInstructionList();
    }

    private String blockLambdaName() {
        return BLOCK_LAMBDA_NAME_PREFIX + blockCounter++;
    }

    private ErrorReporter newReporterByKeyToken(SyntaxNode syntaxNode) {
        return new DefaultErrorReporter(script, syntaxNode.getKeyToken());
    }

}
