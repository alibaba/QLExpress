package com.alibaba.qlexpress4.parser;

import com.alibaba.qlexpress4.exception.DefaultErrorReporter;
import com.alibaba.qlexpress4.parser.tree.*;
import com.alibaba.qlexpress4.runtime.instruction.IndexInstruction;
import com.alibaba.qlexpress4.runtime.instruction.QLInstruction;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: DQinYuan
 */
public class QvmInstructionGenerator implements QLProgramVisitor<Void, Void> {

    private final String script;

    private final List<QLInstruction> instructions = new ArrayList<>();

    public QvmInstructionGenerator(String script) {
        this.script = script;
    }

    @Override
    public Void visit(Program program, Void context) {
        program.getStmtList().forEach(stmt -> stmt.accept(this, context));
        return null;
    }

    @Override
    public Void visit(ArrayCallExpr arrayCallExpr, Void context) {
        return null;
    }

    @Override
    public Void visit(AssignExpr assignExpr, Void context) {
        return null;
    }

    @Override
    public Void visit(BinaryOpExpr binaryOpExpr, Void context) {
        return null;
    }

    @Override
    public Void visit(Block block, Void context) {
        return null;
    }

    @Override
    public Void visit(Break aBreak, Void context) {
        return null;
    }

    @Override
    public Void visit(CallExpr callExpr, Void context) {
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


}
