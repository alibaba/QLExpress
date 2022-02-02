package com.alibaba.qlexpress4.parser;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.QLPrecedences;
import com.alibaba.qlexpress4.exception.QLSyntaxException;
import com.alibaba.qlexpress4.parser.tree.AssignExpr;
import com.alibaba.qlexpress4.parser.tree.BinaryOpExpr;
import com.alibaba.qlexpress4.parser.tree.Block;
import com.alibaba.qlexpress4.parser.tree.ConstExpr;
import com.alibaba.qlexpress4.parser.tree.DeclType;
import com.alibaba.qlexpress4.parser.tree.DeclTypeArgument;
import com.alibaba.qlexpress4.parser.tree.Expr;
import com.alibaba.qlexpress4.parser.tree.FieldCallExpr;
import com.alibaba.qlexpress4.parser.tree.CallExpr;
import com.alibaba.qlexpress4.parser.tree.ForEachStmt;
import com.alibaba.qlexpress4.parser.tree.ForStmt;
import com.alibaba.qlexpress4.parser.tree.FunctionStmt;
import com.alibaba.qlexpress4.parser.tree.GroupExpr;
import com.alibaba.qlexpress4.parser.tree.IdExpr;
import com.alibaba.qlexpress4.parser.tree.Identifier;
import com.alibaba.qlexpress4.parser.tree.ImportStmt;
import com.alibaba.qlexpress4.parser.tree.LambdaExpr;
import com.alibaba.qlexpress4.parser.tree.ListExpr;
import com.alibaba.qlexpress4.parser.tree.LocalVarDeclareStmt;
import com.alibaba.qlexpress4.parser.tree.NewExpr;
import com.alibaba.qlexpress4.parser.tree.PrefixUnaryOpExpr;
import com.alibaba.qlexpress4.parser.tree.Program;
import com.alibaba.qlexpress4.parser.tree.Stmt;
import com.alibaba.qlexpress4.parser.tree.SuffixUnaryOpExpr;
import com.alibaba.qlexpress4.parser.tree.TernaryExpr;
import com.alibaba.qlexpress4.parser.tree.TypeExpr;
import com.alibaba.qlexpress4.parser.tree.VarDecl;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class QLParserTest {

    @Test
    public void assignTest() {
        // assign is right-associative
        Program p0 = parse("a = b += 10");
        AssignExpr assignExpr = (AssignExpr) p0.getStmtList().get(0);
        assertEquals("a", assignExpr.getLeft().getKeyToken().getLexeme());
        assertEquals(TokenType.ASSIGN, assignExpr.getKeyToken().getType());
        AssignExpr nestAssign = (AssignExpr) assignExpr.getRight();
        assertEquals(TokenType.ADD_ASSIGN, nestAssign.getKeyToken().getType());
        assertEquals("b", nestAssign.getLeft().getKeyToken().getLexeme());
        assertEquals("10", nestAssign.getRight().getKeyToken().getLexeme());
    }

    @Test
    public void ternaryTest() {
        Program p0 = parse("10<9? 99: 8");
        TernaryExpr ternaryExpr = (TernaryExpr) p0.getStmtList().get(0);
        BinaryOpExpr condition = (BinaryOpExpr) ternaryExpr.getCondition();
        assertEquals("10", condition.getLeft().getKeyToken().getLexeme());
        assertEquals("9", condition.getRight().getKeyToken().getLexeme());
        assertEquals("99", ternaryExpr.getThenExpr().getKeyToken().getLexeme());
        assertEquals("8", ternaryExpr.getElseExpr().getKeyToken().getLexeme());

        Program p1 = parse("a = 10>9? 999: 88");
        AssignExpr assignExpr = (AssignExpr) p1.getStmtList().get(0);
        assertEquals("a", assignExpr.getLeft().getKeyToken().getLexeme());
        TernaryExpr rightExpr = (TernaryExpr) assignExpr.getRight();
        assertTrue(rightExpr.getCondition() instanceof BinaryOpExpr);
        assertEquals("999", rightExpr.getThenExpr().getKeyToken().getLexeme());
        assertEquals("88", rightExpr.getElseExpr().getKeyToken().getLexeme());

        Program p2 = parse("10>9? 3>=2? 22: 33: 88");
        TernaryExpr thenNestParentTernary = (TernaryExpr) p2.getStmtList().get(0);
        assertTrue(thenNestParentTernary.getThenExpr() instanceof TernaryExpr);
        assertTrue(thenNestParentTernary.getElseExpr() instanceof ConstExpr);

        Program p3 = parse("10>9? 88: 3>2? 19: 23");
        TernaryExpr elseNestParentTernary = (TernaryExpr) p3.getStmtList().get(0);
        assertTrue(elseNestParentTernary.getThenExpr() instanceof ConstExpr);
        assertTrue(elseNestParentTernary.getElseExpr() instanceof TernaryExpr);

        // ?: take precedence over assign, so equals to `(10>9? a)=(10: b)=99`, which can not find match `:` to `?`
        // this expression is also invalid in Java
        assertErrReport("10>9? a=10: b=99", "[Error: can not find ':' to match '?']\n" +
                "[Near: 10>9? a=10: b=9]\n" +
                "           ^\n" +
                "[Line: 1, Column: 5]");
    }

    @Test
    public void importTest() {
        Program p0 = parse("import a.b.Cc;");
        List<Stmt> stmtList = p0.getStmtList();
        ImportStmt importStmt = (ImportStmt) stmtList.get(0);
        assertEquals(ImportStmt.ImportType.FIXED, importStmt.getImportType());
        assertEquals("a.b.Cc", importStmt.getPath());
        assertEquals("Cc", importStmt.getKeyToken().getLexeme());

        Program p1 = parse("import ab.bb.*;");
        List<Stmt> stmtList1 = p1.getStmtList();
        ImportStmt importStmt1 = (ImportStmt) stmtList1.get(0);
        assertEquals(ImportStmt.ImportType.PREFIX, importStmt1.getImportType());
        assertEquals("ab.bb", importStmt1.getPath());
        assertEquals("bb", importStmt1.getKeyToken().getLexeme());
        assertFalse(importStmt1.isStaticImport());

        Program p2 = parse("import static ab.Assert.*;");
        List<Stmt> stmtList2 = p2.getStmtList();
        ImportStmt importStmt2 = (ImportStmt) stmtList2.get(0);
        assertTrue(importStmt2.isStaticImport());

        assertErrReport("import a.b.cc int d", "[Error: expect '.' between package]\n" +
                "[Near: rt a.b.cc int d]\n" +
                "                 ^^^\n" +
                "[Line: 1, Column: 15]");
        assertErrReport("import;", "[Error: invalid package to import]\n" +
                "[Near: import;]\n" +
                "             ^\n" +
                "[Line: 1, Column: 7]");
    }

    @Test
    public void functionStmtTest() {
        assertErrReport("function test(weew",
                "[Error: can not find ')' to match it]\n" +
                        "[Near: ction test(weew]\n" +
                        "                 ^\n" +
                        "[Line: 1, Column: 14]");
        assertErrReport("function ttt(int if)",
                "[Error: invalid variable name]\n" +
                        "[Near: n ttt(int if)]\n" +
                        "                 ^^\n" +
                        "[Line: 1, Column: 18]");

        Program program = parse("function myTest(int a, boolean b, MyClz myC) {}");
        FunctionStmt functionStmt = (FunctionStmt) program.getStmtList().get(0);
        assertEquals("myTest", functionStmt.getName().getKeyToken().getLexeme());
        List<VarDecl> params = functionStmt.getParams();
        VarDecl param0 = params.get(0);
        assertEquals("java.lang.Integer", param0.getType().getType().getKeyToken().getLiteral());
        assertEquals("a", param0.getVariable().getKeyToken().getLexeme());
        VarDecl param1 = params.get(1);
        assertEquals("java.lang.Boolean", param1.getType().getType().getKeyToken().getLiteral());
        assertEquals("b", param1.getVariable().getKeyToken().getLexeme());
        VarDecl param2 = params.get(2);
        assertEquals("MyClz", param2.getType().getType().getKeyToken().getLexeme());
        assertEquals("myC", param2.getVariable().getKeyToken().getLexeme());
    }

    @Test
    public void expressionTest() {
        Program program = parse("1+2*(4+5)");
        BinaryOpExpr binaryOpExpr = (BinaryOpExpr) program.getStmtList().get(0);
        assertEquals(TokenType.ADD, binaryOpExpr.getKeyToken().getType());
        assertEquals("1", binaryOpExpr.getLeft().getKeyToken().getLexeme());
        BinaryOpExpr right = (BinaryOpExpr) binaryOpExpr.getRight();
        assertEquals(TokenType.MUL, right.getKeyToken().getType());
        assertEquals("2", right.getLeft().getKeyToken().getLexeme());
        BinaryOpExpr group = (BinaryOpExpr) ((GroupExpr) right.getRight()).getExpr();
        assertEquals(TokenType.ADD, group.getKeyToken().getType());
        assertEquals("4", group.getLeft().getKeyToken().getLexeme());
        assertEquals("5", group.getRight().getKeyToken().getLexeme());

        Program program1 = parse("a<2 && b>3");
        assertTrue(program1.getStmtList().get(0) instanceof BinaryOpExpr);
    }

    @Test
    public void bitMoveTest() {
        Program lShift = parse("a << 2");
        BinaryOpExpr lShiftExpr = (BinaryOpExpr) lShift.getStmtList().get(0);
        assertEquals(TokenType.LSHIFT, lShiftExpr.getKeyToken().getType());

        Program rShift = parse("a >> 2");
        BinaryOpExpr rShiftExpr = (BinaryOpExpr) rShift.getStmtList().get(0);
        assertEquals(TokenType.RSHIFT, rShiftExpr.getKeyToken().getType());

        Program urShift = parse("a >>> 2");
        BinaryOpExpr urShiftExpr = (BinaryOpExpr) urShift.getStmtList().get(0);
        assertEquals(TokenType.URSHIFT, urShiftExpr.getKeyToken().getType());

        assertErrReport("a < <", "[Error: invalid expression]\n" +
                "[Near: a < <]\n" +
                "           ^\n" +
                "[Line: 1, Column: 5]");
        assertErrReport("a <<", "[Error: invalid expression]\n" +
                "[Near: a <<]\n" +
                "         ^^\n" +
                "[Line: 1, Column: 3]");
        assertErrReport("a >>", "[Error: invalid expression]\n" +
                "[Near: a >>]\n" +
                "         ^^\n" +
                "[Line: 1, Column: 3]");
        assertErrReport("a >>>", "[Error: invalid expression]\n" +
                "[Near: a >>>]\n" +
                "         ^^^\n" +
                "[Line: 1, Column: 3]");
        assertErrReport("a >> > 1", "[Error: invalid expression]\n" +
                "[Near: a >> > 1]\n" +
                "            ^\n" +
                "[Line: 1, Column: 6]");
    }

    @Test
    public void expressionPrefixTest() {
        Program program = parse("++4+5--");
        BinaryOpExpr binaryOpExpr = (BinaryOpExpr) program.getStmtList().get(0);
        assertEquals(TokenType.ADD, binaryOpExpr.getKeyToken().getType());
        assertTrue(binaryOpExpr.getLeft() instanceof PrefixUnaryOpExpr);
        assertEquals(TokenType.INC, binaryOpExpr.getLeft().getKeyToken().getType());
        PrefixUnaryOpExpr left = (PrefixUnaryOpExpr) binaryOpExpr.getLeft();
        assertEquals("4", left.getExpr().getKeyToken().getLexeme());
        assertTrue(binaryOpExpr.getRight() instanceof SuffixUnaryOpExpr);
        assertEquals(TokenType.DEC, binaryOpExpr.getRight().getKeyToken().getType());
        SuffixUnaryOpExpr right = (SuffixUnaryOpExpr) binaryOpExpr.getRight();
        assertEquals("5", right.getExpr().getKeyToken().getLexeme());

        Program program1 = parse("i--");
        assertTrue(program1.getStmtList().get(0) instanceof SuffixUnaryOpExpr);
    }

    @Test
    public void expressionPrefixSuffixTest() {
        // equivalent to ++(4--)
        Program program = parse("++4--");
        PrefixUnaryOpExpr prefixUnaryOpExpr = (PrefixUnaryOpExpr) program.getStmtList().get(0);
        assertEquals(TokenType.INC, prefixUnaryOpExpr.getKeyToken().getType());
        SuffixUnaryOpExpr suffixUnaryOpExpr = (SuffixUnaryOpExpr) prefixUnaryOpExpr.getExpr();
        assertEquals(TokenType.DEC, suffixUnaryOpExpr.getKeyToken().getType());
        assertEquals("4", suffixUnaryOpExpr.getExpr().getKeyToken().getLexeme());
    }

    @Test
    public void typeCastTest() {
        Program program = parse("(int)-4");
        CallExpr castExpr = (CallExpr) program.getStmtList().get(0);
        assertTrue(castExpr.getTarget() instanceof TypeExpr);
        assertEquals("java.lang.Integer", castExpr.getTarget().getKeyToken().getLiteral());
        PrefixUnaryOpExpr prefixUnaryOpExpr = (PrefixUnaryOpExpr) castExpr.getArguments().get(0);
        assertEquals(TokenType.SUB, prefixUnaryOpExpr.getKeyToken().getType());
        assertEquals("4", prefixUnaryOpExpr.getExpr().getKeyToken().getLexeme());

        Program program1 = parse("(Integer)-4");
        BinaryOpExpr binaryOpExpr = (BinaryOpExpr) program1.getStmtList().get(0);
        assertEquals("Integer", ((GroupExpr) binaryOpExpr.getLeft()).getExpr().getKeyToken().getLexeme());

        assertErrReport("(int)i+++(Long)++i", "[Error: invalid expression]\n" +
                "[Near: ++(Long)++i]\n" +
                "                 ^\n" +
                "[Line: 1, Column: 18]");

        Program program2 = parse("+(int)3L");
        PrefixUnaryOpExpr unaryOpExpr = (PrefixUnaryOpExpr) program2.getStmtList().get(0);
        assertEquals(TokenType.ADD, unaryOpExpr.getKeyToken().getType());
        assertEquals("3L", ((CallExpr) unaryOpExpr.getExpr()).getArguments().get(0).getKeyToken().getLexeme());

        Program program3 = parse("(Integer) a.test");
        CallExpr castFieldCallExpr = (CallExpr) program3.getStmtList().get(0);
        assertTrue(((GroupExpr) castFieldCallExpr.getTarget()).getExpr() instanceof IdExpr);
        assertTrue(castFieldCallExpr.getArguments().get(0) instanceof FieldCallExpr);
    }

    @Test
    public void typeCastGroupTest() {
        Program program2 = parse("(int)i+++(long)++i");
        BinaryOpExpr binaryOpExpr2 = (BinaryOpExpr) program2.getStmtList().get(0);
        CallExpr leftCast = (CallExpr) binaryOpExpr2.getLeft();
        assertTrue(((GroupExpr) leftCast.getTarget()).getExpr() instanceof TypeExpr);
        assertTrue(leftCast.getArguments().get(0) instanceof SuffixUnaryOpExpr);
        CallExpr rightCast = (CallExpr) binaryOpExpr2.getRight();
        assertTrue(rightCast.getTarget() instanceof TypeExpr);
        assertTrue(rightCast.getArguments().get(0) instanceof PrefixUnaryOpExpr);
    }

    @Test
    public void lambdaTest() {
        Program program = parse("(a, b, c) -> c+d");
        LambdaExpr lambdaExpr = (LambdaExpr) program.getStmtList().get(0);
        BinaryOpExpr binaryOpExpr = (BinaryOpExpr) lambdaExpr.getExprBody();
        assertEquals(TokenType.ADD, binaryOpExpr.getKeyToken().getType());
        assertEquals(Arrays.asList("a", "b", "c"), lambdaExpr.getParameters().stream()
                .map(VarDecl::getVariable)
                .map(Identifier::getKeyToken)
                .map(Token::getLexeme)
                .collect(Collectors.toList()));

        Program programLambdaOperand = parse("c testOp (a, b, String c) -> c+d");
        BinaryOpExpr testOp = (BinaryOpExpr) programLambdaOperand.getStmtList().get(0);
        assertTrue(testOp.getLeft() instanceof IdExpr);
        assertTrue(testOp.getRight() instanceof LambdaExpr);

        Program singleParamLambda = parse("a -> a+1");
        LambdaExpr singleParamLambdaExpr = (LambdaExpr) singleParamLambda.getStmtList().get(0);
        assertEquals(1, singleParamLambdaExpr.getParameters().size());
        assertEquals("a", singleParamLambdaExpr.getParameters().get(0).getVariable()
                .getKeyToken().getLexeme());

        Program genericLambda = parse("(List<String> l) -> l.get(0)");
        LambdaExpr genericLambdaExpr = (LambdaExpr) genericLambda.getStmtList().get(0);
        VarDecl varDecl = genericLambdaExpr.getParameters().get(0);
        assertEquals(1, varDecl.getType().getTypeArguments().size());

        assertErrReport("cc (a, b, c) -> c+d", "[Error: invalid expression]\n" +
                "[Near: cc (a, b, c) -]\n" +
                "          ^\n" +
                "[Line: 1, Column: 4]");
    }

    @Test
    public void lambdaForceTest() {
        Program program = parse("1+((Function) (a, b, c) -> c+d).apply(1)+2");
        BinaryOpExpr binaryOpExpr = (BinaryOpExpr) program.getStmtList().get(0);
        assertTrue(binaryOpExpr.getLeft() instanceof BinaryOpExpr);
        BinaryOpExpr left = (BinaryOpExpr) binaryOpExpr.getLeft();
        assertTrue(left.getLeft() instanceof ConstExpr);
        CallExpr leftRight = (CallExpr) left.getRight();
        FieldCallExpr fieldCallExpr = (FieldCallExpr) leftRight.getTarget();
        CallExpr castExpr = (CallExpr) ((GroupExpr) fieldCallExpr.getExpr()).getExpr();
        assertEquals("Function", ((GroupExpr) castExpr.getTarget()).getExpr().getKeyToken().getLexeme());
        assertTrue(castExpr.getArguments().get(0) instanceof LambdaExpr);

        Program program1 = parse("(Function) x -> x+2");
        CallExpr cast1 = (CallExpr) program1.getStmtList().get(0);
        GroupExpr target1 = (GroupExpr) cast1.getTarget();
        assertEquals("Function", target1.getExpr().getKeyToken().getLexeme());
        assertTrue(cast1.getArguments().get(0) instanceof LambdaExpr);

        Program program2 = parse("(Function<Integer, Long>) x -> x+2");
        CallExpr callExpr = (CallExpr) program2.getStmtList().get(0);
        assertTrue(callExpr.getArguments().get(0) instanceof LambdaExpr);
        TypeExpr typeExpr = (TypeExpr) ((GroupExpr) callExpr.getTarget()).getExpr();
        DeclType declType = typeExpr.getDeclType();
        assertEquals("Function", declType.getType().getKeyToken().getLexeme());
        assertEquals(Arrays.asList("Integer", "Long"), declType.getTypeArguments().stream()
                .map(DeclTypeArgument::getType)
                .map(DeclType::getType)
                .map(Identifier::getKeyToken)
                .map(Token::getLexeme)
                .collect(Collectors.toList()));
    }

    @Test
    public void fieldCallTest() {
        Program program = parse("1+a.c+2");
        BinaryOpExpr binaryOpExpr = (BinaryOpExpr) program.getStmtList().get(0);
        BinaryOpExpr left = (BinaryOpExpr) binaryOpExpr.getLeft();
        FieldCallExpr fieldCallExpr = (FieldCallExpr) left.getRight();
        assertTrue(fieldCallExpr.getExpr() instanceof IdExpr);
        assertEquals("a", fieldCallExpr.getExpr().getKeyToken().getLexeme());
        assertEquals("c", fieldCallExpr.getAttribute().getKeyToken().getLexeme());

        assertErrReport("a.(1+2)", "[Error: invalid field call]\n" +
                "[Near: a.(1+2)]\n" +
                "        ^\n" +
                "[Line: 1, Column: 2]");
    }

    @Test
    public void methodCallTest() {
        Program program = parse("a.c()+d.m(1,2)");
        BinaryOpExpr binaryOpExpr = (BinaryOpExpr) program.getStmtList().get(0);
        CallExpr left = (CallExpr) binaryOpExpr.getLeft();
        assertTrue(left.getArguments().isEmpty());
        FieldCallExpr objectExpr = (FieldCallExpr) left.getTarget();
        assertEquals("a", objectExpr.getExpr().getKeyToken().getLexeme());
        assertEquals("c", objectExpr.getAttribute().getKeyToken().getLexeme());
        CallExpr right = (CallExpr) binaryOpExpr.getRight();
        assertTrue(right.getTarget() instanceof FieldCallExpr);
        assertEquals(Arrays.asList("1", "2"), right.getArguments().stream()
                .map(Expr::getKeyToken)
                .map(Token::getLexeme)
                .collect(Collectors.toList()));
    }

    @Test
    public void nestCallTest() {
        Program program = parse("fun(1)(2)(3)");
        CallExpr call3 = (CallExpr) program.getStmtList().get(0);
        assertEquals("3", call3.getArguments().get(0).getKeyToken().getLexeme());
        CallExpr call2 = (CallExpr) call3.getTarget();
        assertEquals("2", call2.getArguments().get(0).getKeyToken().getLexeme());
        CallExpr call1 = (CallExpr) call2.getTarget();
        assertEquals("1", call1.getArguments().get(0).getKeyToken().getLexeme());
        assertEquals("fun", call1.getTarget().getKeyToken().getLexeme());
    }

    @Test
    public void newTest() {
        Program program = parse("new Integer(1,2,3) + 1");
        BinaryOpExpr binaryOpExpr = (BinaryOpExpr) program.getStmtList().get(0);
        NewExpr newExpr = (NewExpr) binaryOpExpr.getLeft();
        assertEquals(3, newExpr.getArguments().size());

        assertErrReport("new Ttt(1*9-0", "[Error: can not find ')' to match]\n" +
                "[Near: new Ttt(1*9-0]\n" +
                "              ^\n" +
                "[Line: 1, Column: 8]");
    }

    @Test
    public void listLiteralTest() {
        Program program = parse("[1+2,3*4,new Integer(12),a->a+1] testOp m -> m-1");
        BinaryOpExpr binaryOpExpr = (BinaryOpExpr) program.getStmtList().get(0);
        assertTrue(binaryOpExpr.getRight() instanceof LambdaExpr);
        assertTrue(binaryOpExpr.getLeft() instanceof ListExpr);
        ListExpr listExpr = (ListExpr) binaryOpExpr.getLeft();
        assertEquals(Arrays.asList(BinaryOpExpr.class, BinaryOpExpr.class, NewExpr.class, LambdaExpr.class),
                listExpr.getElements().stream()
                        .map(Object::getClass).collect(Collectors.toList()));
    }

    @Test
    public void forTest() {
        Program program = parse("for (i = 0; i < 3; i++) a+=1");
        ForStmt forStmt = (ForStmt) program.getStmtList().get(0);
        assertTrue(forStmt.getForInit() instanceof AssignExpr);
        assertTrue(forStmt.getCondition() instanceof BinaryOpExpr);
        assertTrue(forStmt.getForUpdate() instanceof SuffixUnaryOpExpr);
        assertTrue(forStmt.getBody() instanceof AssignExpr);

        Program program1 = parse("int a=0;" +
                "for (ii : [12,3,4,4]) {" +
                "   a+=ii" +
                "}");
        assertTrue(program1.getStmtList().get(0) instanceof LocalVarDeclareStmt);
        ForEachStmt forEachStmt = (ForEachStmt) program1.getStmtList().get(1);
        VarDecl itVar = forEachStmt.getItVar();
        assertNull(itVar.getType());
        assertEquals("ii", itVar.getVariable().getKeyToken().getLexeme());
        assertTrue(forEachStmt.getTarget() instanceof ListExpr);
        assertTrue(forEachStmt.getBody() instanceof Block);

        Program program2 = parse("for (int iid : a.b.test()) {" +
                "   a+=iid" +
                "}");
        ForEachStmt forEachStmt2 = (ForEachStmt) program2.getStmtList().get(0);
        VarDecl itVar2 = forEachStmt2.getItVar();
        assertEquals("int", itVar2.getType().getType().getKeyToken().getLexeme());
        assertEquals("iid", itVar2.getVariable().getKeyToken().getLexeme());

        Program program3 = parse("for (int i = 0; i < 3; i++) a+=1");
        ForStmt forStmt3 = (ForStmt) program3.getStmtList().get(0);
        assertTrue(forStmt3.getForInit() instanceof LocalVarDeclareStmt);
        LocalVarDeclareStmt initStmt = (LocalVarDeclareStmt) forStmt3.getForInit();
        assertTrue(initStmt.getInitializer() instanceof ConstExpr);
        assertEquals("int", initStmt.getVarDecl().getType().getType().getKeyToken().getLexeme());
        assertEquals("i", initStmt.getVarDecl().getVariable().getKeyToken().getLexeme());
        assertEquals("0", initStmt.getInitializer().getKeyToken().getLexeme());

        Program program4 = parse("for (Entry<int, Test> iid : []);");
        ForEachStmt forEachStmtGeneric = (ForEachStmt) program4.getStmtList().get(0);
        VarDecl itVarWithGeneric = forEachStmtGeneric.getItVar();
        assertEquals(2, itVarWithGeneric.getType().getTypeArguments().size());
    }

    @Test
    public void genericTest() {
        Program program = parse("Map<String, Map<? extends Test, ? super Test2>> m;");
        LocalVarDeclareStmt localVarDeclareStmt = (LocalVarDeclareStmt) program.getStmtList().get(0);
        assertNull(localVarDeclareStmt.getInitializer());
        VarDecl varDecl = localVarDeclareStmt.getVarDecl();
        assertEquals("m", varDecl.getVariable().getKeyToken().getLexeme());
        DeclType declType = varDecl.getType();
        assertEquals("Map", declType.getType().getKeyToken().getLexeme());
        List<DeclTypeArgument> typeArguments = declType.getTypeArguments();
        assertEquals(2, typeArguments.size());
        DeclTypeArgument declTypeArgument = typeArguments.get(0);
        assertEquals(DeclTypeArgument.Bound.NONE, declTypeArgument.getBound());
        assertEquals("String", declTypeArgument.getType().getType().getKeyToken().getLexeme());
        DeclTypeArgument declTypeArgument1 = typeArguments.get(1);
        assertEquals("Map", declTypeArgument1.getType().getType().getKeyToken().getLexeme());
        assertEquals(DeclTypeArgument.Bound.NONE, declTypeArgument1.getBound());

        List<DeclTypeArgument> nestTypeArguments = declTypeArgument1.getType().getTypeArguments();
        DeclTypeArgument extendsArgument = nestTypeArguments.get(0);
        assertEquals(DeclTypeArgument.Bound.EXTENDS, extendsArgument.getBound());
        assertEquals("Test", extendsArgument.getType().getType().getKeyToken().getLexeme());

        DeclTypeArgument superArgument = nestTypeArguments.get(1);
        assertEquals(DeclTypeArgument.Bound.SUPER, superArgument.getBound());
        assertEquals("Test2", superArgument.getType().getType().getKeyToken().getLexeme());

        // built-in type argument
        Program program0 = parse("Map<int, long> m;");
        VarDecl builtInTypeGenericVarDecl = ((LocalVarDeclareStmt) (program0.getStmtList().get(0))).getVarDecl();
        DeclType builtInTypeGenericDeclType = builtInTypeGenericVarDecl.getType();
        assertEquals("Map", builtInTypeGenericDeclType.getType().getKeyToken().getLexeme());
        List<DeclTypeArgument> builtInTypeArgs = builtInTypeGenericDeclType.getTypeArguments();
        assertEquals(TokenType.TYPE, builtInTypeArgs.get(0).getType().getType().getKeyToken().getType());
        assertEquals("int", builtInTypeArgs.get(0).getType().getType().getKeyToken().getLexeme());
        assertEquals(TokenType.TYPE, builtInTypeArgs.get(1).getType().getType().getKeyToken().getType());
        assertEquals("long", builtInTypeArgs.get(1).getType().getType().getKeyToken().getLexeme());
    }

    @Test
    public void localVarDeclTest() {
        assertErrReport("int if;", "[Error: invalid variable name]\n" +
                "[Near: int if;]\n" +
                "           ^^\n" +
                "[Line: 1, Column: 5]");
    }

    @Test
    public void programTest() {
        Program program = parse("a=2+3;a*9+2");
        assertEquals(2, program.getStmtList().size());
        assertTrue(program.getStmtList().get(0) instanceof AssignExpr);
        assertTrue(program.getStmtList().get(1) instanceof BinaryOpExpr);

        Program program1 = parse("a=2+3;a*9+2;");
        assertEquals(2, program1.getStmtList().size());
        assertTrue(program1.getStmtList().get(0) instanceof AssignExpr);
        assertTrue(program1.getStmtList().get(1) instanceof BinaryOpExpr);

        assertErrReport("1+1)", "[Error: expect ';' in the end of statement]\n" +
                "[Near: 1+1)]\n" +
                "          ^\n" +
                "[Line: 1, Column: 4]");
        assertErrReport("int a = 2", "[Error: expect ';' in the end of statement]\n" +
                "[Near: int a = 2]\n" +
                "               ^\n" +
                "[Line: 1, Column: 9]");
    }

    @Test
    public void blockTest() {
        Program program = parse("{23}");
        Block block = (Block) program.getStmtList().get(0);
        assertTrue(block.getStmtList().get(0) instanceof ConstExpr);

        Program program0 = parse("1;{a=2+3;a*9+2};3");
        Block block0 = (Block) program0.getStmtList().get(1);
        assertTrue(block0.getStmtList().get(0) instanceof AssignExpr);
        assertTrue(block0.getStmtList().get(1) instanceof BinaryOpExpr);

        Program program1 = parse("{23;}");
        Block block1 = (Block) program1.getStmtList().get(0);
        assertTrue(block1.getStmtList().get(0) instanceof ConstExpr);

        Program program2 = parse("{23;" +
                "{" +
                "  a = 10;" +
                "  b = 100;" +
                "}" +
                "c=1000+1+b" +
                "}");
        Block block2 = (Block) program2.getStmtList().get(0);
        assertTrue(block2.getStmtList().get(0) instanceof ConstExpr);
        assertTrue(block2.getStmtList().get(1) instanceof Block);
        assertTrue(block2.getStmtList().get(2) instanceof AssignExpr);

        assertErrReport("{a=123+34", "[Error: can not find '}' to match]\n" +
                "[Near: {a=123+34]\n" +
                "       ^\n" +
                "[Line: 1, Column: 1]");
    }

    private void assertErrReport(String script, String expectReport) {
        try {
            new QLParser(new HashMap<>(), new Scanner(script, QLOptions.DEFAULT_OPTIONS)).parse();
        } catch (QLSyntaxException e) {
            assertEquals(expectReport, e.getMessage());
        }
    }

    private Program parse(String script) {
        Map<String, Integer> mockUserOperator = new HashMap<>();
        mockUserOperator.put("testOp", QLPrecedences.MULTI);
        return new QLParser(mockUserOperator, new Scanner(script, QLOptions.DEFAULT_OPTIONS)).parse();
    }
}