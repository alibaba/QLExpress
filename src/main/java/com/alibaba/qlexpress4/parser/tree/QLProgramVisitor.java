package com.alibaba.qlexpress4.parser.tree;

/**
 * Author: DQinYuan
 */
public interface QLProgramVisitor<R, C> {

    R visit(Program program, C context);

    R visit(StmtList stmtList, C context);

    R visit(IndexCallExpr indexCallExpr, C context);

    R visit(AssignExpr assignExpr, C context);

    R visit(BinaryOpExpr binaryOpExpr, C context);

    R visit(Block block, C context);

    R visit(Break aBreak, C context);

    R visit(CallExpr callExpr, C context);

    R visit(CastExpr castExpr, C context);

    R visit(ConstExpr constExpr, C context);

    R visit(Continue aContinue, C context);

    R visit(GetFieldExpr getFieldExpr, C context);

    R visit(ForEachStmt forEachStmt, C context);

    R visit(ForStmt forStmt, C context);

    R visit(FunctionStmt functionStmt, C context);

    R visit(GroupExpr groupExpr, C context);

    R visit(Identifier identifier, C context);

    R visit(IdExpr idExpr, C context);

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

    R visit(TryCatch tryCatch, C context);

    R visit(TypeExpr typeExpr, C context);

    R visit(WhileStmt whileStmt, C context);

    R visit(IfExpr ifExpr, C context);

    R visit(GetMethodExpr getMethodExpr, C context);

    R visit(MapExpr mapExpr, C context);

    R visit(MultiNewArrayExpr newArrayDimsExpr, C context);

    R visit(NewArrayExpr newArrayExpr, C context);
}
