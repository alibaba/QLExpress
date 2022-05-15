package com.alibaba.qlexpress4.parser.tree;

/**
 * Author: DQinYuan
 */
public interface QLProgramVisitor<R, C> {

    R visit(Program program, C context);

    R visit(ArrayCallExpr arrayCallExpr, C context);

    R visit(AssignExpr assignExpr, C context);

    R visit(BinaryOpExpr binaryOpExpr, C context);

    R visit(Block block, C context);

    R visit(Break aBreak, C context);

    R visit(CallExpr callExpr, C context);

    R visit(CastExpr castExpr, C context);

    R visit(ConstExpr constExpr, C context);

    R visit(Continue aContinue, C context);

    R visit(EmptyStmt emptyStmt, C context);

    R visit(FieldCallExpr fieldCallExpr, C context);

    R visit(ForEachStmt forEachStmt, C context);

    R visit(ForStmt forStmt, C context);

    R visit(FunctionStmt functionStmt, C context);

    R visit(GroupExpr groupExpr, C context);

    R visit(Identifier identifier, C context);

    R visit(IdExpr idExpr, C context);

    R visit(IfStmt ifStmt, C context);

    R visit(ImportStmt importStmt, C context);

    R visit(LambdaExpr lambdaExpr, C context);

    R visit(ListExpr listExpr, C context);

    R visit(LocalVarDeclareStmt localVarDeclareStmt, C context);

    R visit(MacroStmt macroStmt, C context);

    R visit(NewExpr newExpr, C context);

    R visit(PrefixUnaryOpExpr prefixUnaryOpExpr, C context);

    R visit(ReturnStmt returnStmt, C context);

    R visit(SuffixUnaryOpExpr suffixUnaryOpExpr, C context);

    R visit(TernaryExpr ternaryExpr, C context);

    R visit(TryCatchStmt tryCatchStmt, C context);

    R visit(TypeExpr typeExpr, C context);

    R visit(WhileStmt whileStmt, C context);
}
