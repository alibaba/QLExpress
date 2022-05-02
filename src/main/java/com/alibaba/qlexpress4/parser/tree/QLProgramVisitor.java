package com.alibaba.qlexpress4.parser.tree;

/**
 * Author: DQinYuan
 */
public interface QLProgramVisitor<R, S> {

    R visit(ArrayCallExpr arrayCallExpr);

    R visit(AssignExpr assignExpr);

    R visit(BinaryOpExpr binaryOpExpr);

    R visit(Block block);

    R visit(Break aBreak);

    R visit(CallExpr callExpr);

    R visit(CastExpr castExpr);

    R visit(ConstExpr constExpr);

    R visit(Continue aContinue);

    R visit(EmptyStmt emptyStmt);

    R visit(FieldCallExpr fieldCallExpr);

    R visit(ForEachStmt forEachStmt);

    R visit(ForStmt forStmt);

    R visit(FunctionStmt functionStmt);

    R visit(GroupExpr groupExpr);

    R visit(Identifier identifier);

    R visit(IdExpr idExpr);

    R visit(IfStmt ifStmt);

    R visit(ImportStmt importStmt);

    R visit(LambdaExpr lambdaExpr);

    R visit(ListExpr listExpr);

    R visit(LocalVarDeclareStmt localVarDeclareStmt);

    R visit(MacroStmt macroStmt);

    R visit(NewExpr newExpr);

    R visit(PrefixUnaryOpExpr prefixUnaryOpExpr);

    R visit(Program program);

    R visit(ReturnStmt aReturnStmt);

    R visit(SuffixUnaryOpExpr suffixUnaryOpExpr);

    R visit(TernaryExpr ternaryExpr);

    R visit(TryCatchStmt tryCatchStmt);

    R visit(TypeExpr typeExpr);

    R visit(WhileStmt whileStmt);
}
