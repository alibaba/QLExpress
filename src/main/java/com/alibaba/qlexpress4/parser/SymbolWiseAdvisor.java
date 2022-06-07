package com.alibaba.qlexpress4.parser;

import com.alibaba.qlexpress4.exception.QLException;
import com.alibaba.qlexpress4.parser.tree.*;

import java.util.function.Function;

import static com.alibaba.qlexpress4.parser.VisitingScope.SymbolType;

/**
 * Author: DQinYuan
 */
public class SymbolWiseAdvisor implements QLProgramVisitor<Void, VisitingScope> {

    private final String script;

    private final QLProgramVisitor<?, VisitingScope> downStream;

    public SymbolWiseAdvisor(String script,
                             Function<SymbolWiseAdvisor, QLProgramVisitor<?, VisitingScope>> downStreamSupplier) {
        this.script = script;
        this.downStream = downStreamSupplier.apply(this);
    }

    @Override
    public Void visit(Program program, VisitingScope visitingScope) {
        downStream.visit(program, new VisitingScope(null));
        return null;
    }

    @Override
    public Void visit(StmtList stmtList, VisitingScope context) {
        downStream.visit(stmtList, context);
        return null;
    }

    @Override
    public Void visit(IndexCallExpr indexCallExpr, VisitingScope visitingScope) {
        downStream.visit(indexCallExpr, visitingScope);
        return null;
    }

    @Override
    public Void visit(AssignExpr assignExpr, VisitingScope visitingScope) {
        if (TokenType.ASSIGN.equals(assignExpr.getKeyToken().getType()) && assignExpr.getLeft() instanceof  IdExpr) {
            IdExpr assigned = (IdExpr) assignExpr.getLeft();
            visitingScope.put(assigned.getKeyToken().getLexeme(), SymbolType.INTERNAL_VAR);
        }
        downStream.visit(assignExpr, visitingScope);
        return null;
    }

    @Override
    public Void visit(BinaryOpExpr binaryOpExpr, VisitingScope visitingScope) {
        downStream.visit(binaryOpExpr, visitingScope);
        return null;
    }

    @Override
    public Void visit(Block block, VisitingScope visitingScope) {
        downStream.visit(block, new VisitingScope(visitingScope));
        return null;
    }

    @Override
    public Void visit(Break aBreak, VisitingScope visitingScope) {
        downStream.visit(aBreak, visitingScope);
        return null;
    }

    @Override
    public Void visit(CallExpr callExpr, VisitingScope visitingScope) {
        if (callExpr.getTarget() instanceof IdExpr) {
            IdExpr target = (IdExpr) callExpr.getTarget();
            String funcName = target.getKeyToken().getLexeme();
            if (!visitingScope.globalContains(funcName)) {
                visitingScope.put(funcName, SymbolType.EXTERNAL_FUNC);
            }
        }
        downStream.visit(callExpr, visitingScope);
        return null;
    }

    @Override
    public Void visit(CastExpr castExpr, VisitingScope visitingScope) {
        downStream.visit(castExpr, visitingScope);
        return null;
    }

    @Override
    public Void visit(ConstExpr constExpr, VisitingScope visitingScope) {
        downStream.visit(constExpr, visitingScope);
        return null;
    }

    @Override
    public Void visit(Continue aContinue, VisitingScope visitingScope) {
        downStream.visit(aContinue, visitingScope);
        return null;
    }

    @Override
    public Void visit(EmptyStmt emptyStmt, VisitingScope visitingScope) {
        downStream.visit(emptyStmt, visitingScope);
        return null;
    }

    @Override
    public Void visit(FieldCallExpr fieldCallExpr, VisitingScope visitingScope) {
        downStream.visit(fieldCallExpr, visitingScope);
        return null;
    }

    @Override
    public Void visit(ForEachStmt forEachStmt, VisitingScope visitingScope) {
        downStream.visit(forEachStmt, visitingScope);
        return null;
    }

    @Override
    public Void visit(ForStmt forStmt, VisitingScope visitingScope) {
        downStream.visit(forStmt, visitingScope);
        return null;
    }

    @Override
    public Void visit(FunctionStmt functionStmt, VisitingScope visitingScope) {
        String functionName = functionStmt.getName().getId();
        if (visitingScope.localContains(functionName)) {
            duplicateSymbolException(functionStmt.getName().getKeyToken(), functionName);
        }
        visitingScope.put(functionName, SymbolType.FUNC);
        downStream.visit(functionStmt, visitingScope);
        return null;
    }

    @Override
    public Void visit(GroupExpr groupExpr, VisitingScope visitingScope) {
        downStream.visit(groupExpr, visitingScope);
        return null;
    }

    @Override
    public Void visit(Identifier identifier, VisitingScope visitingScope) {
        downStream.visit(identifier, visitingScope);
        return null;
    }

    @Override
    public Void visit(IdExpr idExpr, VisitingScope visitingScope) {
        String id = idExpr.getKeyToken().getLexeme();
        if (!visitingScope.globalContains(id)) {
            visitingScope.put(id, SymbolType.EXTERNAL_VAR);
        }
        downStream.visit(idExpr, visitingScope);
        return null;
    }

    @Override
    public Void visit(IfStmt ifStmt, VisitingScope visitingScope) {
        downStream.visit(ifStmt, visitingScope);
        return null;
    }

    @Override
    public Void visit(ImportStmt importStmt, VisitingScope visitingScope) {
        downStream.visit(importStmt, visitingScope);
        return null;
    }

    @Override
    public Void visit(LambdaExpr lambdaExpr, VisitingScope visitingScope) {
        downStream.visit(lambdaExpr, visitingScope);
        return null;
    }

    @Override
    public Void visit(ListExpr listExpr, VisitingScope visitingScope) {
        downStream.visit(listExpr, visitingScope);
        return null;
    }

    @Override
    public Void visit(LocalVarDeclareStmt localVarDeclareStmt, VisitingScope visitingScope) {
        String varName = localVarDeclareStmt.getVarDecl().getVariable().getId();
        if (visitingScope.localContains(varName)) {
            duplicateSymbolException(localVarDeclareStmt.getVarDecl().getVariable().getKeyToken(), varName);
        }
        visitingScope.put(varName, SymbolType.INTERNAL_VAR);
        downStream.visit(localVarDeclareStmt, visitingScope);
        return null;
    }

    @Override
    public Void visit(MacroStmt macroStmt, VisitingScope visitingScope) {
        String macroName = macroStmt.getName().getId();
        if (visitingScope.localContains(macroName)) {
            duplicateSymbolException(macroStmt.getName().getKeyToken(), macroName);
        }
        visitingScope.put(macroName, SymbolType.MACRO);
        downStream.visit(macroStmt, visitingScope);
        return null;
    }

    @Override
    public Void visit(NewExpr newExpr, VisitingScope visitingScope) {
        downStream.visit(newExpr, visitingScope);
        return null;
    }

    @Override
    public Void visit(PrefixUnaryOpExpr prefixUnaryOpExpr, VisitingScope visitingScope) {
        downStream.visit(prefixUnaryOpExpr, visitingScope);
        return null;
    }

    @Override
    public Void visit(ReturnStmt returnStmt, VisitingScope visitingScope) {
        downStream.visit(returnStmt, visitingScope);
        return null;
    }

    @Override
    public Void visit(SuffixUnaryOpExpr suffixUnaryOpExpr, VisitingScope visitingScope) {
        downStream.visit(suffixUnaryOpExpr, visitingScope);
        return null;
    }

    @Override
    public Void visit(TernaryExpr ternaryExpr, VisitingScope visitingScope) {
        downStream.visit(ternaryExpr, visitingScope);
        return null;
    }

    @Override
    public Void visit(TryCatchStmt tryCatchStmt, VisitingScope visitingScope) {
        downStream.visit(tryCatchStmt, visitingScope);
        return null;
    }

    @Override
    public Void visit(TypeExpr typeExpr, VisitingScope visitingScope) {
        downStream.visit(typeExpr, visitingScope);
        return null;
    }

    @Override
    public Void visit(WhileStmt whileStmt, VisitingScope visitingScope) {
        downStream.visit(whileStmt, visitingScope);
        return null;
    }

    private void duplicateSymbolException(Token token, String symbolName) {
        throw QLException.reportParserErr(script, token,
                "DUPLICATE_SYMBOL_DECLARE", "symbol " + symbolName + " is duplicate declared.");
    }
}
