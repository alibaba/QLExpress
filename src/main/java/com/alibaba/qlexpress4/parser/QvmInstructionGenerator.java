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
public class QvmInstructionGenerator implements QLProgramVisitor<Void, VisitingScope> {

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

    private final QVm qVm;

    private final String script;

    private final SymbolWiseAdvisor advisor;

    private final List<QLInstruction> instructionList = new ArrayList<>();

    private int blockCounter = 0;
    private int forCounter = 0;
    private int ifCounter = 0;
    private int lambdaCounter = 0;
    private int macroCounter = 0;
    private int ternaryCounter = 0;
    private int tryCounter = 0;
    private int whileCounter = 0;

    public QvmInstructionGenerator(String prefix, QVm qVm, String script, SymbolWiseAdvisor advisor) {
        this.prefix = prefix;
        this.qVm = qVm;
        this.script = script;
        this.advisor = advisor;
    }

    @Override
    public Void visit(Program program, VisitingScope visitingScope) {
        program.getStmtList().accept(advisor, visitingScope);
        return null;
    }

    @Override
    public Void visit(StmtList stmtList, VisitingScope visitingScope) {
        for (Stmt stmt : stmtList.getStmts()) {
            stmt.accept(advisor, visitingScope);
            if (stmt instanceof Expr) {
                // pop if no acceptor
                instructionList.add(new PopInstruction(newReporterByNode(stmt)));
            }
        }
        return null;
    }

    @Override
    public Void visit(IndexCallExpr indexCallExpr, VisitingScope visitingScope) {
        indexCallExpr.getTarget().accept(advisor, visitingScope);
        indexCallExpr.getIndex().accept(advisor, visitingScope);
        instructionList.add(new IndexInstruction(newReporterByNode(indexCallExpr)));
        return null;
    }

    @Override
    public Void visit(AssignExpr assignExpr, VisitingScope visitingScope) {
        assignExpr.getLeft().accept(advisor, visitingScope);
        assignExpr.getRight().accept(advisor, visitingScope);
        instructionList.add(new OperatorInstruction(newReporterByNode(assignExpr), OperatorFactory
                .getOperator(assignExpr.getKeyToken().getLexeme())));
        return null;
    }

    @Override
    public Void visit(BinaryOpExpr binaryOpExpr, VisitingScope visitingScope) {
        instructionList.add(new OperatorInstruction(newReporterByNode(binaryOpExpr), OperatorFactory
                .getOperator(binaryOpExpr.getKeyToken().getLexeme())));
        return null;
    }

    @Override
    public Void visit(Block block, VisitingScope visitingScope) {
        instructionList.add(new ConstInstruction(newReporterByNode(block),
                generateLambda(blockLambdaName(), block.getStmtList(), visitingScope)));
        instructionList.add(new CallInstruction(newReporterByNode(block), 0));
        return null;
    }

    @Override
    public Void visit(Break aBreak, VisitingScope visitingScope) {
        instructionList.add(new BreakInstruction(newReporterByNode(aBreak)));
        return null;
    }

    @Override
    public Void visit(CallExpr callExpr, VisitingScope visitingScope) {
        Expr target = callExpr.getTarget();
        if (target instanceof IdExpr) {
            // macro can be called by `macroName()`
            IdExpr idExpr = (IdExpr) target;
            instructionList.add(new LoadInstruction(newReporterByNode(target), idExpr.getKeyToken().getLexeme()));
        } else if (target instanceof FieldCallExpr) {
            // method invoke
            FieldCallExpr fieldCallExpr = (FieldCallExpr) target;
            fieldCallExpr.getExpr().accept(advisor, visitingScope);
            callExpr.getArguments().forEach(arg -> arg.accept(advisor, visitingScope));
            Identifier attribute = fieldCallExpr
                    .getAttribute();
            instructionList.add(new MethodInvokeInstruction(newReporterByToken(attribute.getKeyToken()),
                    attribute.getId(), callExpr.getArguments().size()));
        } else {
            // evaluated lambda
            target.accept(advisor, visitingScope);
            callExpr.getArguments().forEach(arg -> arg.accept(advisor, visitingScope));
            instructionList.add(new CallInstruction(newReporterByNode(callExpr),
                    callExpr.getArguments().size()));
        }

        return null;
    }

    @Override
    public Void visit(CastExpr castExpr, VisitingScope visitingScope) {
        castExpr.getTypeExpr().accept(advisor, visitingScope);
        castExpr.getTarget().accept(advisor, visitingScope);
        instructionList.add(new CastInstruction(newReporterByNode(castExpr)));
        return null;
    }

    @Override
    public Void visit(ConstExpr constExpr, VisitingScope visitingScope) {
        instructionList.add(new ConstInstruction(newReporterByNode(constExpr), constExpr.getConstValue()));
        return null;
    }

    @Override
    public Void visit(Continue aContinue, VisitingScope visitingScope) {
        ErrorReporter errorReporter = newReporterByNode(aContinue);
        instructionList.add(new ConstInstruction(errorReporter, null));
        instructionList.add(new ReturnInstruction(errorReporter));
        return null;
    }

    @Override
    public Void visit(FieldCallExpr fieldCallExpr, VisitingScope visitingScope) {
        fieldCallExpr.getExpr().accept(advisor, visitingScope);
        String fieldName = fieldCallExpr.getAttribute().getId();
        instructionList.add(new GetFieldInstruction(newReporterByNode(fieldCallExpr), fieldName));
        return null;
    }

    @Override
    public Void visit(ForEachStmt forEachStmt, VisitingScope visitingScope) {
        forEachStmt.getTarget().accept(advisor, visitingScope);
        ErrorReporter forEachErrReporter = newReporterByNode(forEachStmt);
        VarDecl itVar = forEachStmt.getItVar();
        QLambda bodyLambda = generateLambda(prefix + FOR_LAMBDA_NAME_PREFIX + forCount(),
                forEachStmt.getBody() instanceof Block? ((Block) forEachStmt.getBody()).getStmtList():
                forEachStmt.getBody(),
                visitingScope, Collections.singletonList(
                        new QLambdaInner.Param(itVar.getVariable().getId(), itVar.getType().getClz())));
        instructionList.add(new ForEachInstruction(forEachErrReporter, bodyLambda));
        return null;
    }

    @Override
    public Void visit(ForStmt forStmt, VisitingScope visitingScope) {
        int forCount = forCount();
        QLambda conditionLambda = generateLambda(prefix + FOR_LAMBDA_NAME_PREFIX + forCount + CONDITION_SUFFIX,
                forStmt.getCondition(), visitingScope);
        String bodyLambdaName = prefix + FOR_LAMBDA_NAME_PREFIX + forCount + BODY_SUFFIX;
        QLambda bodyLambda = generateLambda(bodyLambdaName,
                forStmt.getBody(), visitingScope, Collections.emptyList(),
                generateNodeInstructions(bodyLambdaName, forStmt.getForUpdate(), visitingScope));
        ErrorReporter forErrReporter = newReporterByNode(forStmt);
        WhileInstruction whileInstruction = new WhileInstruction(forErrReporter,
                conditionLambda, bodyLambda);
        QLambda forLambda = generateLambda(prefix + FOR_LAMBDA_NAME_PREFIX + forCount,
                forStmt.getForInit(), visitingScope, Collections.emptyList(),
                Collections.singletonList(whileInstruction));
        instructionList.add(new ConstInstruction(forErrReporter, forLambda));
        instructionList.add(new CallInstruction(forErrReporter, 0));
        return null;
    }

    @Override
    public Void visit(FunctionStmt functionStmt, VisitingScope visitingScope) {
        String functionName = functionStmt.getName().getId();
        QLambda functionLambda = generateLambda(functionName, functionStmt.getBody(), visitingScope);
        ErrorReporter errorReporter = newReporterByNode(functionStmt);
        instructionList.add(new ConstInstruction(errorReporter, functionLambda));
        instructionList.add(new DefineLocalInstruction(errorReporter, functionName, QLambda.class));
        return null;
    }

    @Override
    public Void visit(GroupExpr groupExpr, VisitingScope visitingScope) {
        groupExpr.getExpr().accept(advisor, visitingScope);
        return null;
    }

    @Override
    public Void visit(Identifier identifier, VisitingScope visitingScope) {
        return null;
    }

    @Override
    public Void visit(IdExpr idExpr, VisitingScope visitingScope) {
        String id = idExpr.getKeyToken().getLexeme();
        if (VisitingScope.SymbolType.MACRO.equals(visitingScope.getSymbolType(id))) {
            // macro
            ErrorReporter errorReporter = newReporterByNode(idExpr);
            instructionList.add(new LoadInstruction(errorReporter, id));
            instructionList.add(new CallInstruction(errorReporter, 0));
        } else {
            instructionList.add(new LoadInstruction(newReporterByNode(idExpr), id));
        }
        return null;
    }

    @Override
    public Void visit(IfExpr ifExpr, VisitingScope visitingScope) {
        ifExpr.getCondition().accept(advisor, visitingScope);

        int ifCount = ifCount();
        QLambda thenLambda = generateLambda(prefix + IF_LAMBDA_PREFIX + ifCount + THEN_SUFFIX,
                ifExpr.getThenBranch(), visitingScope);
        instructionList.add(new IfInstruction(newReporterByNode(ifExpr), thenLambda,
                ifExpr.getElseBranch() != null? generateLambda(prefix + IF_LAMBDA_PREFIX + ifCount + ELSE_SUFFIX,
                        ifExpr.getElseBranch(), visitingScope): null));
        return null;
    }

    @Override
    public Void visit(ImportStmt importStmt, VisitingScope visitingScope) {
        // import statement has been handled in parser
        return null;
    }

    @Override
    public Void visit(LambdaExpr lambdaExpr, VisitingScope visitingScope) {
        List<QLambdaInner.Param> paramClzes = lambdaExpr.getParameters().stream()
                .map(varDecl -> new QLambdaInner.Param(varDecl.getVariable().getId(),
                        varDecl.getType().getClz()))
                .collect(Collectors.toList());
        QLambda qLambda = generateLambda(lambdaName(), lambdaExpr.getExprBody() == null ? lambdaExpr.getBlockBody() :
                        lambdaExpr.getExprBody(), visitingScope, paramClzes);
        instructionList.add(new ConstInstruction(newReporterByNode(lambdaExpr), qLambda));
        return null;
    }

    @Override
    public Void visit(ListExpr listExpr, VisitingScope visitingScope) {
        List<Expr> elementExprs = listExpr.getElements();
        elementExprs.forEach(expr -> expr.accept(advisor, visitingScope));
        instructionList.add(new NewListInstruction(newReporterByNode(listExpr), elementExprs.size()));
        return null;
    }

    @Override
    public Void visit(LocalVarDeclareStmt localVarDeclareStmt, VisitingScope visitingScope) {
        ErrorReporter errorReporter = newReporterByNode(localVarDeclareStmt);
        String varName = localVarDeclareStmt.getVarDecl().getVariable().getId();
        if (localVarDeclareStmt.getInitializer() != null) {
            localVarDeclareStmt.getInitializer().accept(advisor, visitingScope);
        } else {
            instructionList.add(new ConstInstruction(errorReporter, null));
        }
        instructionList.add(new DefineLocalInstruction(errorReporter, varName,
                localVarDeclareStmt.getVarDecl().getType().getClz()));
        return null;
    }

    @Override
    public Void visit(MacroStmt macroStmt, VisitingScope visitingScope) {
        QLambda macroLambda = generateLambda(macroLambdaName(), macroStmt.getBody(), visitingScope);
        ErrorReporter errorReporter = newReporterByNode(macroStmt);
        instructionList.add(new ConstInstruction(errorReporter, macroLambda));
        instructionList.add(new DefineLocalInstruction(errorReporter,
                macroStmt.getName().getId(), QLambda.class));
        return null;
    }

    @Override
    public Void visit(NewExpr newExpr, VisitingScope visitingScope) {
        List<Expr> arguments = newExpr.getArguments();
        arguments.forEach(argExpr -> argExpr.accept(advisor, visitingScope));

        Class<?> clz = newExpr.getClazz().getClz();
        instructionList.add(new NewInstruction(newReporterByNode(newExpr), clz, arguments.size()));
        return null;
    }

    @Override
    public Void visit(PrefixUnaryOpExpr prefixUnaryOpExpr, VisitingScope visitingScope) {
        prefixUnaryOpExpr.getExpr().accept(advisor, visitingScope);
        String op = prefixUnaryOpExpr.getKeyToken().getLexeme();
        instructionList.add(new UnaryInstruction(newReporterByNode(prefixUnaryOpExpr),
                OperatorFactory.getPrefixUnaryOperator(op)));
        return null;
    }

    @Override
    public Void visit(SuffixUnaryOpExpr suffixUnaryOpExpr, VisitingScope visitingScope) {
        suffixUnaryOpExpr.getExpr().accept(advisor, visitingScope);
        String op = suffixUnaryOpExpr.getKeyToken().getLexeme();
        instructionList.add(new UnaryInstruction(newReporterByNode(suffixUnaryOpExpr),
                OperatorFactory.getSuffixUnaryOperator(op)));
        return null;
    }

    @Override
    public Void visit(ReturnStmt returnStmt, VisitingScope visitingScope) {
        ErrorReporter errorReporter = newReporterByNode(returnStmt);
        if (returnStmt.getExpr() != null) {
            returnStmt.getExpr().accept(advisor, visitingScope);
        } else {
            instructionList.add(new ConstInstruction(errorReporter, null));
        }

        instructionList.add(new ReturnInstruction(errorReporter));
        return null;
    }

    @Override
    public Void visit(TernaryExpr ternaryExpr, VisitingScope visitingScope) {
        ternaryExpr.getCondition().accept(advisor, visitingScope);

        int ternaryCount = ternaryCount();
        QLambda thenLambda = generateLambda(prefix + TERNARY_PREFIX + ternaryCount + THEN_SUFFIX,
                ternaryExpr.getThenExpr(), visitingScope);
        QLambda elseLambda = generateLambda(prefix + TERNARY_PREFIX + ternaryCount + ELSE_SUFFIX,
                ternaryExpr.getElseExpr(), visitingScope);
        instructionList.add(new IfInstruction(newReporterByNode(ternaryExpr), thenLambda, elseLambda));
        return null;
    }

    @Override
    public Void visit(TryCatch tryCatchExpr, VisitingScope visitingScope) {
        int tryCount = tryCount();
        QLambda bodyLambda = generateLambda(prefix + TRY_LAMBDA_PREFIX + tryCount,
                tryCatchExpr.getBody(), visitingScope);

        Map<Class<?>, QLambda> exceptionTable = new HashMap<>();
        for (int catchCount = 0; catchCount < tryCatchExpr.getTryCatch().size(); catchCount++) {
            TryCatch.CatchClause tryCatch = tryCatchExpr.getTryCatch().get(catchCount);
            String eName = tryCatch.getVariable().getId();
            String lambdaName = prefix + TRY_LAMBDA_PREFIX + catchCount + CATCH_SUFFIX;
            List<QLInstruction> catchBody = generateNodeInstructions(lambdaName, tryCatch.getBody(), visitingScope);
            for (int exceptionCount = 0; exceptionCount < tryCatch.getExceptions().size(); exceptionCount++) {
                DeclType exceptionType = tryCatch.getExceptions().get(exceptionCount);
                QLambdaInner.Param eParam = new QLambdaInner.Param(eName, exceptionType.getClz());
                QLambdaInner qLambdaInner = new QLambdaInner(qVm, lambdaName, catchBody,
                        Collections.singletonList(eParam));
                exceptionTable.put(exceptionType.getClz(), qLambdaInner);
            }
        }

        instructionList.add(new TryCatchInstruction(newReporterByNode(tryCatchExpr), bodyLambda, exceptionTable,
                tryCatchExpr.getTryFinal() != null?
                        generateLambda(prefix + TRY_LAMBDA_PREFIX + tryCount + FINAL_SUFFIX,
                                tryCatchExpr, visitingScope) :
                        null)
        );
        return null;
    }

    @Override
    public Void visit(TypeExpr typeExpr, VisitingScope visitingScope) {
        instructionList.add(new ConstInstruction(newReporterByNode(typeExpr),
                typeExpr.getDeclType().getClz()));
        return null;
    }

    @Override
    public Void visit(WhileStmt whileStmt, VisitingScope visitingScope) {
        int whileCount = whileCount();
        QLambda conditionLambda = generateLambda(prefix + WHILE_PREFIX + whileCount + CONDITION_SUFFIX,
                whileStmt.getCondition(), visitingScope);
        QLambda bodyLambda = generateLambda(prefix + WHILE_PREFIX + whileCount + BODY_SUFFIX,
                whileStmt.getBody(), visitingScope);
        instructionList.add(new WhileInstruction(newReporterByNode(whileStmt), conditionLambda, bodyLambda));
        return null;
    }

    public List<QLInstruction> getInstructionList() {
        return instructionList;
    }

    private QLambda generateLambda(String name, SyntaxNode targetNode, VisitingScope visitingScope) {
        return generateLambda(name, targetNode, visitingScope, Collections.emptyList(), Collections.emptyList());
    }

    private QLambda generateLambda(String name, SyntaxNode targetNode, VisitingScope visitingScope,
                                   List<QLambdaInner.Param> paramsType) {
        return generateLambda(name, targetNode, visitingScope, paramsType, Collections.emptyList());
    }

    private QLambda generateLambda(String name, SyntaxNode targetNode, VisitingScope visitingScope,
                                   List<QLambdaInner.Param> paramsType, List<QLInstruction> extra) {
        List<QLInstruction> instructionList = generateNodeInstructions(name, targetNode, visitingScope);
        instructionList.addAll(extra);
        return new QLambdaInner(qVm, name, instructionList, paramsType);
    }

    private List<QLInstruction> generateNodeInstructions(String name, SyntaxNode targetNode, VisitingScope visitingScope) {
        FunctionHolder<SymbolWiseAdvisor, QLProgramVisitor<?, VisitingScope>> qvmInstructionGeneratorHolder =
                new FunctionHolder<>(symbolAdvisor -> new QvmInstructionGenerator(name + "_",
                        qVm,  script, symbolAdvisor));
        SymbolWiseAdvisor symbolWiseAdvisor = new SymbolWiseAdvisor(script, qvmInstructionGeneratorHolder);
        targetNode.accept(symbolWiseAdvisor, visitingScope);
        return ((QvmInstructionGenerator) qvmInstructionGeneratorHolder.getResult()).getInstructionList();
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
