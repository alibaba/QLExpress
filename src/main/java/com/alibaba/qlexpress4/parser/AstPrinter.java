package com.alibaba.qlexpress4.parser;

import com.alibaba.qlexpress4.parser.tree.*;

import java.util.function.Consumer;
import java.util.stream.Collectors;
import static com.alibaba.qlexpress4.utils.PrintlnUtils.printlnByCurDepth;

/**
 * Author: DQinYuan
 */
public class AstPrinter implements QLProgramVisitor<Void, Void> {

    private final Consumer<String> debug;

    private int depth = 0;

    public AstPrinter(Consumer<String> debug) {
        this.debug = debug;
    }

    @Override
    public Void visit(Program program, Void context) {
        printByCurDepth(program.getClass().getSimpleName());
        depth++;
        program.getStmtList().accept(this, context);
        return null;
    }

    @Override
    public Void visit(StmtList stmtList, Void context) {
        for (Stmt stmt : stmtList.getStmts()) {
            stmt.accept(this, context);
        }
        return null;
    }

    private <T extends SyntaxNode> void visitNode(T node, Consumer<T> innerHandler) {
        printByCurDepth(node.getClass().getSimpleName() + " " + node.getKeyToken().getLexeme());
        depth++;
        innerHandler.accept(node);
        depth--;
    }

    @Override
    public Void visit(IndexCallExpr indexCallExpr, Void context) {
        visitNode(indexCallExpr, expr -> {
            expr.getTarget().accept(this, context);
            expr.getIndex().accept(this, context);
        });
        return null;
    }

    @Override
    public Void visit(AssignExpr assignExpr, Void context) {
        visitNode(assignExpr, expr -> {
            expr.getLeft().accept(this, context);
            expr.getRight().accept(this, context);
        });
        return null;
    }

    @Override
    public Void visit(BinaryOpExpr binaryOpExpr, Void context) {
        visitNode(binaryOpExpr, expr -> {
            expr.getLeft().accept(this, context);
            expr.getRight().accept(this, context);
        });
        return null;
    }

    @Override
    public Void visit(Block block, Void context) {
        visitNode(block, blockNode -> {
            blockNode.getStmtList().accept(this, context);
        });
        return null;
    }

    @Override
    public Void visit(Break aBreak, Void context) {
        printByCurDepth("break");
        return null;
    }

    @Override
    public Void visit(CallExpr callExpr, Void context) {
        visitNode(callExpr, expr -> {
            expr.getTarget().accept(this, context);
            for (Expr argument : expr.getArguments()) {
                argument.accept(this, context);
            }
        });
        return null;
    }

    @Override
    public Void visit(CastExpr castExpr, Void context) {
        visitNode(castExpr, expr -> {
            expr.getTypeExpr().accept(this, context);
            expr.getTarget().accept(this, context);
        });
        return null;
    }

    @Override
    public Void visit(ConstExpr constExpr, Void context) {
        Object constValue = constExpr.getConstValue();
        printByCurDepth(constValue == null? "null": constValue.toString());
        return null;
    }

    @Override
    public Void visit(Continue aContinue, Void context) {
        printByCurDepth("continue");
        return null;
    }

    @Override
    public Void visit(GetFieldExpr getFieldExpr, Void context) {
        visitNode(getFieldExpr, expr -> {
            getFieldExpr.getExpr().accept(this, context);
            getFieldExpr.getAttribute().accept(this, context);
        });
        return null;
    }

    @Override
    public Void visit(ForEachStmt forEachStmt, Void context) {
        visitNode(forEachStmt, stmt -> {
            stmt.getItVar().getVariable().accept(this, context);
            stmt.getTarget().accept(this, context);
            stmt.getBody().accept(this, context);
        });
        return null;
    }

    @Override
    public Void visit(ForStmt forStmt, Void context) {
        visitNode(forStmt, stmt -> {
            stmt.getForInit().accept(this, context);
            stmt.getCondition().accept(this, context);
            stmt.getForUpdate().accept(this, context);
            stmt.getBody().accept(this, context);
        });
        return null;
    }

    @Override
    public Void visit(FunctionStmt functionStmt, Void context) {
        visitNode(functionStmt, stmt -> {
            functionStmt.getName().accept(this, context);
            for (VarDecl param : functionStmt.getParams()) {
                param.getVariable().accept(this, context);
            }
            functionStmt.getBody().accept(this, context);
        });
        return null;
    }

    @Override
    public Void visit(GroupExpr groupExpr, Void context) {
        visitNode(groupExpr, expr -> {
            expr.getExpr().accept(this, context);
        });
        return null;
    }

    @Override
    public Void visit(Identifier identifier, Void context) {
        printByCurDepth(identifier.getId());
        return null;
    }

    @Override
    public Void visit(IdExpr idExpr, Void context) {
        printByCurDepth(idExpr.getKeyToken().getLexeme());
        return null;
    }

    @Override
    public Void visit(ImportStmt importStmt, Void context) {
        printByCurDepth(importStmt.getClass().getSimpleName() + " " + importStmt.getPath());
        return null;
    }

    @Override
    public Void visit(LambdaExpr lambdaExpr, Void context) {
        visitNode(lambdaExpr, expr -> {
            for (VarDecl parameter : expr.getParameters()) {
                parameter.getVariable().accept(this, context);
            }
            expr.getBody().accept(this, context);
        });
        return null;
    }

    @Override
    public Void visit(ListExpr listExpr, Void context) {
        visitNode(listExpr, expr -> {
            for (Expr element : expr.getElements()) {
                element.accept(this, context);
            }
        });
        return null;
    }

    @Override
    public Void visit(LocalVarDeclareStmt localVarDeclareStmt, Void context) {
        visitNode(localVarDeclareStmt, stmt -> {
            localVarDeclareStmt.getVarDecl().getVariable().accept(this, context);
            localVarDeclareStmt.getInitializer().accept(this, context);
        });
        return null;
    }

    @Override
    public Void visit(MacroStmt macroStmt, Void context) {
        visitNode(macroStmt, stmt -> {
            stmt.getName().accept(this, context);
            stmt.getBody().accept(this, context);
        });
        return null;
    }

    @Override
    public Void visit(NewExpr newExpr, Void context) {
        visitNode(newExpr, expr -> {
            printByCurDepth(expr.getClazz().getClz().getSimpleName());
            for (Expr argument : expr.getArguments()) {
                argument.accept(this, context);
            }
        });
        return null;
    }

    @Override
    public Void visit(PrefixUnaryOpExpr prefixUnaryOpExpr, Void context) {
        visitNode(prefixUnaryOpExpr, expr ->
                expr.getExpr().accept(this, context));
        return null;
    }

    @Override
    public Void visit(ReturnStmt returnStmt, Void context) {
        visitNode(returnStmt, stmt ->
                stmt.getExpr().accept(this, context));
        return null;
    }

    @Override
    public Void visit(SuffixUnaryOpExpr suffixUnaryOpExpr, Void context) {
        visitNode(suffixUnaryOpExpr, expr -> expr.getExpr()
                .accept(this, context));
        return null;
    }

    @Override
    public Void visit(TernaryExpr ternaryExpr, Void context) {
        visitNode(ternaryExpr, expr -> {
            expr.getCondition().accept(this, context);
            expr.getThenExpr().accept(this, context);
            expr.getElseExpr().accept(this, context);
        });
        return null;
    }

    @Override
    public Void visit(TryCatch tryCatch, Void context) {
        visitNode(tryCatch, expr -> {
            expr.getBody().accept(this, context);
            for (TryCatch.CatchClause catchClause : expr.getTryCatch()) {
                printByCurDepth(catchClause.getExceptions().stream()
                        .map(declType -> declType.getClz().getSimpleName())
                        .collect(Collectors.joining("|"))
                        + " " + catchClause.getVariable().getId());
                catchClause.getBody().accept(this, context);
            }
            if (expr.getTryFinal() != null) {
                expr.getTryFinal().getStmtList().accept(this, context);
            }
        });
        return null;
    }

    @Override
    public Void visit(TypeExpr typeExpr, Void context) {
        printByCurDepth(typeExpr.getDeclType().getClz().getSimpleName() + ".class");
        return null;
    }

    @Override
    public Void visit(WhileStmt whileStmt, Void context) {
        visitNode(whileStmt, stmt -> {
            stmt.getCondition().accept(this, context);
            stmt.getBody().accept(this, context);
        });
        return null;
    }

    @Override
    public Void visit(IfExpr ifExpr, Void context) {
        visitNode(ifExpr, expr -> {
            expr.getCondition().accept(this, context);
            expr.getThenBranch().accept(this, context);
            if (expr.getElseBranch() != null) {
                expr.getElseBranch().accept(this, context);
            }
        });
        return null;
    }

    @Override
    public Void visit(GetMethodExpr getMethodExpr, Void context) {
        visitNode(getMethodExpr, expr -> {
            expr.getExpr().accept(this, context);
            expr.getAttribute().accept(this, context);
        });
        return null;
    }

    private void printByCurDepth(String str) {
        printlnByCurDepth(depth, str, debug);
    }
}
