package com.alibaba.qlexpress4.parser;

import com.alibaba.qlexpress4.exception.DefaultErrorReporter;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.parser.tree.*;
import com.alibaba.qlexpress4.runtime.QLambda;
import com.alibaba.qlexpress4.runtime.QLambdaInner;
import com.alibaba.qlexpress4.runtime.QVm;
import com.alibaba.qlexpress4.runtime.instruction.*;

import java.util.*;

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
        QvmInstructionGenerator qvmInstructionGenerator = new QvmInstructionGenerator(qVm, script);
        qvmInstructionGenerator.visit(block, context);
        List<QLInstruction> instructions = qvmInstructionGenerator.getInstructionList();

        QLambda blockLambda = new QLambdaInner(qVm, blockLambdaName(),
                instructions, Collections.emptyList());
        instructionList.add(new CallLambdaInstruction(newReporterByKeyToken(block), blockLambda));
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
        return null;
    }

    @Override
    public Void visit(ConstExpr constExpr, Void context) {
        return null;
    }

    @Override
    public Void visit(Continue aContinue, Void context) {
        return null;
    }

    @Override
    public Void visit(EmptyStmt emptyStmt, Void context) {
        return null;
    }

    @Override
    public Void visit(FieldCallExpr fieldCallExpr, Void context) {
        return null;
    }

    @Override
    public Void visit(ForEachStmt forEachStmt, Void context) {
        return null;
    }

    @Override
    public Void visit(ForStmt forStmt, Void context) {
        return null;
    }

    @Override
    public Void visit(FunctionStmt functionStmt, Void context) {
        return null;
    }

    @Override
    public Void visit(GroupExpr groupExpr, Void context) {
        return null;
    }

    @Override
    public Void visit(Identifier identifier, Void context) {
        return null;
    }

    @Override
    public Void visit(IdExpr idExpr, Void context) {
        return null;
    }

    @Override
    public Void visit(IfStmt ifStmt, Void context) {
        return null;
    }

    @Override
    public Void visit(ImportStmt importStmt, Void context) {
        return null;
    }

    @Override
    public Void visit(LambdaExpr lambdaExpr, Void context) {
        return null;
    }

    @Override
    public Void visit(ListExpr listExpr, Void context) {
        return null;
    }

    @Override
    public Void visit(LocalVarDeclareStmt localVarDeclareStmt, Void context) {
        return null;
    }

    @Override
    public Void visit(MacroStmt macroStmt, Void context) {
        return null;
    }

    @Override
    public Void visit(NewExpr newExpr, Void context) {
        return null;
    }

    @Override
    public Void visit(PrefixUnaryOpExpr prefixUnaryOpExpr, Void context) {
        return null;
    }

    @Override
    public Void visit(ReturnStmt aReturnStmt, Void context) {
        return null;
    }

    @Override
    public Void visit(SuffixUnaryOpExpr suffixUnaryOpExpr, Void context) {
        return null;
    }

    @Override
    public Void visit(TernaryExpr ternaryExpr, Void context) {
        return null;
    }

    @Override
    public Void visit(TryCatchStmt tryCatchStmt, Void context) {
        return null;
    }

    @Override
    public Void visit(TypeExpr typeExpr, Void context) {
        return null;
    }

    @Override
    public Void visit(WhileStmt whileStmt, Void context) {
        return null;
    }

    public List<QLInstruction> getInstructionList() {
        return instructionList;
    }

    private String blockLambdaName() {
        return BLOCK_LAMBDA_NAME_PREFIX + blockCounter++;
    }

    private ErrorReporter newReporterByKeyToken(SyntaxNode syntaxNode) {
        return new DefaultErrorReporter(script, syntaxNode.getKeyToken());
    }

}
