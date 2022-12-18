package com.alibaba.qlexpress4.exception;

import com.alibaba.qlexpress4.DefaultClassSupplier;
import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.QLPrecedences;
import com.alibaba.qlexpress4.parser.ImportManager;
import com.alibaba.qlexpress4.parser.QLParser;
import com.alibaba.qlexpress4.parser.Scanner;
import com.alibaba.qlexpress4.parser.Token;
import com.alibaba.qlexpress4.parser.TokenType;
import com.alibaba.qlexpress4.parser.tree.*;
import com.alibaba.qlexpress4.runtime.MetaClass;
import org.junit.Test;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

/**
 * Author: DQinYuan
 */
public class QLParserTest {

    @Test
    public void arrayTypeTest() {
        Program p0 = parse("Object[] a;");
        assertEquals(Object[].class,
                ((LocalVarDeclareStmt) p0.getStmtList().get(0)).getVarDecl().getType().getClz());
        Program p1 = parse("Object[][][] a;");
        assertEquals(Object[][][].class,
                ((LocalVarDeclareStmt) p1.getStmtList().get(0)).getVarDecl().getType().getClz());

        Program p2 = parse("Map<int[][][], long[]> a;");
        List<DeclTypeArgument> typeArguments = ((LocalVarDeclareStmt) p2.getStmtList().get(0)).getVarDecl()
                .getType().getTypeArguments();
        assertEquals(Integer[][][].class, typeArguments.get(0).getType().getClz());
        assertEquals(Long[].class, typeArguments.get(1).getType().getClz());
    }

    @Test
    public void arrayInitTest() {
        Program p0 = parse("Object[] a = new Object[10];");
        LocalVarDeclareStmt localVarDeclareStmt = (LocalVarDeclareStmt) p0.getStmtList().get(0);
        MultiNewArrayExpr newArrayExpr = (MultiNewArrayExpr) localVarDeclareStmt.getInitializer();
        assertEquals(Collections.singletonList(10), newArrayExpr.getDims().stream()
                .map(ConstExpr.class::cast)
                .map(ConstExpr::getConstValue)
                .collect(Collectors.toList()));
        assertEquals(Object.class, newArrayExpr.getClz());

        Program p1 = parse("new Object[10][3][4][][]");
        MultiNewArrayExpr newArrayExpr1 = (MultiNewArrayExpr) p1.getStmtList().get(0);
        assertEquals(Arrays.asList(10, 3, 4), newArrayExpr1.getDims().stream()
                .map(ConstExpr.class::cast)
                .map(ConstExpr::getConstValue)
                .collect(Collectors.toList()));
        assertEquals(Object[][].class, newArrayExpr1.getClz());

        Program p2 = parse("new int[] {1,2,3,4}");
        NewArrayExpr newArrayExpr2 = (NewArrayExpr) p2.getStmtList().get(0);
        assertEquals(Integer.class, newArrayExpr2.getClz());
        assertEquals(4, newArrayExpr2.getValues().size());
    }

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
    }

    @Test
    public void importTest() {
        Program p0 = parse("import a.b.Cc;");
        StmtList stmtList = p0.getStmtList();
        ImportStmt importStmt = (ImportStmt) stmtList.get(0);
        assertEquals(ImportStmt.ImportType.FIXED, importStmt.getImportType());
        assertEquals("a.b.Cc", importStmt.getPath());
        assertEquals("Cc", importStmt.getKeyToken().getLexeme());

        Program p1 = parse("import ab.bb.*;");
        StmtList stmtList1 = p1.getStmtList();
        ImportStmt importStmt1 = (ImportStmt) stmtList1.get(0);
        assertEquals(ImportStmt.ImportType.PREFIX, importStmt1.getImportType());
        assertEquals("ab.bb", importStmt1.getPath());
        assertEquals("bb", importStmt1.getKeyToken().getLexeme());
        assertFalse(importStmt1.isStaticImport());

        assertErrReport("import static ab.Assert.*;", "[Error: not support 'import static']\n" +
                "[Near: import static ab...]\n" +
                "       ^^^^^^\n" +
                "[Line: 1, Column: 1]");

        assertErrReport("import a.b.cc\n" +
                "int d;", "[Error: missing ';' at the end of statement]\n" +
                "[Near: ...rt a.b.cc int d;]\n" +
                "                    ^^^\n" +
                "[Line: 2, Column: 1]");
        assertErrReport("import;", "[Error: invalid package at import]\n" +
                "[Near: import;]\n" +
                "             ^\n" +
                "[Line: 1, Column: 7]");
        assertErrReport("import a.b.c", "[Error: missing ';' at the end of statement]\n" +
                "[Near: ...mport a.b.c]\n" +
                "                    ^\n" +
                "[Line: 1, Column: 12]");
        assertErrReport("import *;", "[Error: invalid package at import]\n" +
                "[Near: import *;]\n" +
                "              ^\n" +
                "[Line: 1, Column: 8]");

        // import 语句必须在开头
        assertErrReport("1+1;import *;", "[Error: import statement must at the beginning of script]\n" +
                "[Near: 1+1;import *;]\n" +
                "           ^^^^^^\n" +
                "[Line: 1, Column: 5]");
    }

    @Test
    public void functionStmtTest() {
        assertErrReport("function test(weew",
                "[Error: can not find ')' to match it]\n" +
                        "[Near: ...ction test(weew]\n" +
                        "                    ^\n" +
                        "[Line: 1, Column: 14]");
        assertErrReport("function ttt(int if)",
                "[Error: expect ',' between parameters]\n" +
                        "[Near: ...n ttt(int if)]\n" +
                        "                    ^^\n" +
                        "[Line: 1, Column: 18]");

        Program program = parse("function myTest(int a, boolean b, String myC) {}");
        FunctionStmt functionStmt = (FunctionStmt) program.getStmtList().get(0);
        assertEquals("myTest", functionStmt.getName().getKeyToken().getLexeme());
        List<VarDecl> params = functionStmt.getParams();
        VarDecl param0 = params.get(0);
        assertEquals(Integer.class, param0.getType().getClz());
        assertEquals("a", param0.getVariable().getKeyToken().getLexeme());
        VarDecl param1 = params.get(1);
        assertEquals(Boolean.class, param1.getType().getClz());
        assertEquals("b", param1.getVariable().getKeyToken().getLexeme());
        VarDecl param2 = params.get(2);
        assertEquals(String.class, param2.getType().getClz());
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

        Program program2 = parse("true && false + null");
        BinaryOpExpr andOp = (BinaryOpExpr) program2.getStmtList().get(0);
        assertTrue((Boolean) andOp.getLeft().getKeyToken().getLiteral());
        BinaryOpExpr addOp = (BinaryOpExpr) andOp.getRight();
        assertNull(addOp.getRight().getKeyToken().getLiteral());
        assertFalse((Boolean) addOp.getLeft().getKeyToken().getLiteral());

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

        Program program2 = parse("++++i");
        PrefixUnaryOpExpr prefixUnaryOpExpr = (PrefixUnaryOpExpr) program2.getStmtList().get(0);
        assertTrue(prefixUnaryOpExpr.getExpr() instanceof PrefixUnaryOpExpr);

        Program program3 = parse("i++++");
        SuffixUnaryOpExpr suffixUnaryOpExpr = (SuffixUnaryOpExpr) program3.getStmtList().get(0);
        assertTrue(suffixUnaryOpExpr.getExpr() instanceof SuffixUnaryOpExpr);
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
        CastExpr castExpr = (CastExpr) program.getStmtList().get(0);
        assertTrue(castExpr.getTypeExpr() instanceof TypeExpr);
        assertEquals("java.lang.Integer", castExpr.getTypeExpr().getKeyToken().getLiteral());
        PrefixUnaryOpExpr prefixUnaryOpExpr = (PrefixUnaryOpExpr) castExpr.getTarget();
        assertEquals(TokenType.SUB, prefixUnaryOpExpr.getKeyToken().getType());
        assertEquals("4", prefixUnaryOpExpr.getExpr().getKeyToken().getLexeme());

        Program program1 = parse("(Integer)-4");
        BinaryOpExpr binaryOpExpr = (BinaryOpExpr) program1.getStmtList().get(0);
        assertEquals("Integer", ((GroupExpr) binaryOpExpr.getLeft()).getExpr().getKeyToken().getLexeme());

        // "(long) ++i" is valid and "(Long) ++i" is invalid, just like java
        // "(Long) ++i" equals to "((Long)++) i", so it is invalid
        assertErrReport("(int)i+++(Long)++i", "[Error: missing ';' at the end of statement]\n" +
                "[Near: ...++(Long)++i]\n" +
                "                    ^\n" +
                "[Line: 1, Column: 18]");

        Program program2 = parse("+(int)3L");
        PrefixUnaryOpExpr unaryOpExpr = (PrefixUnaryOpExpr) program2.getStmtList().get(0);
        assertEquals(TokenType.ADD, unaryOpExpr.getKeyToken().getType());
        assertEquals("3L", ((CastExpr) unaryOpExpr.getExpr()).getTarget().getKeyToken().getLexeme());

        Program program3 = parse("(Integer) a.test");
        CastExpr castFieldCallExpr = (CastExpr) program3.getStmtList().get(0);
        assertTrue(castFieldCallExpr.getTypeExpr() instanceof TypeExpr);
        assertTrue(castFieldCallExpr.getTarget() instanceof GetFieldExpr);
    }

    @Test
    public void typeCastGroupTest() {
        Program program2 = parse("(int)i+++(long)++i");
        BinaryOpExpr binaryOpExpr2 = (BinaryOpExpr) program2.getStmtList().get(0);
        CastExpr leftCast = (CastExpr) binaryOpExpr2.getLeft();
        assertTrue(leftCast.getTypeExpr() instanceof TypeExpr);
        assertTrue(leftCast.getTarget() instanceof SuffixUnaryOpExpr);
        CastExpr rightCast = (CastExpr) binaryOpExpr2.getRight();
        assertTrue(rightCast.getTypeExpr() instanceof TypeExpr);
        assertTrue(rightCast.getTarget() instanceof PrefixUnaryOpExpr);

        Program program3 = parse("(int)(long)i");
        CastExpr castExpr = (CastExpr) program3.getStmtList().get(0);
        assertEquals("java.lang.Integer", castExpr.getTypeExpr().getKeyToken().getLiteral());
        assertTrue(castExpr.getTarget() instanceof CastExpr);
        CastExpr nestCast = (CastExpr) castExpr.getTarget();
        assertEquals("java.lang.Long", nestCast.getTypeExpr().getKeyToken().getLiteral());
        assertTrue(nestCast.getTarget() instanceof IdExpr);

        Program program4 = parse("(Integer)(Long)i");
        CastExpr castExpr4 = (CastExpr) program4.getStmtList().get(0);
        assertEquals("Integer", castExpr4.getTypeExpr().getKeyToken().getLexeme());
        assertTrue(castExpr4.getTarget() instanceof CastExpr);
        CastExpr nestCast4 = (CastExpr) castExpr4.getTarget();
        assertEquals("Long", nestCast4.getTypeExpr().getKeyToken().getLexeme());
        assertTrue(nestCast4.getTarget() instanceof IdExpr);
    }

    @Test
    public void lambdaTest() {
        Program program = parse("(a, b, c) -> c+d");
        LambdaExpr lambdaExpr = (LambdaExpr) program.getStmtList().get(0);
        BinaryOpExpr binaryOpExpr = (BinaryOpExpr) lambdaExpr.getBody();
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
                "[Near: cc (a, b, c) -...]\n" +
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
        GetFieldExpr getFieldExpr = (GetFieldExpr) leftRight.getTarget();
        CastExpr castExpr = (CastExpr) ((GroupExpr) getFieldExpr.getExpr()).getExpr();
        assertEquals("Function", castExpr.getTypeExpr().getKeyToken().getLexeme());
        assertTrue(castExpr.getTarget() instanceof LambdaExpr);

        Program program1 = parse("(Function) x -> x+2");
        CastExpr cast1 = (CastExpr) program1.getStmtList().get(0);
        assertEquals("Function", cast1.getTypeExpr().getKeyToken().getLexeme());
        assertTrue(cast1.getTarget() instanceof LambdaExpr);

        Program program2 = parse("(Function<Integer, Long>) x -> x+2");
        CastExpr castExpr2 = (CastExpr) program2.getStmtList().get(0);
        assertTrue(castExpr2.getTarget() instanceof LambdaExpr);
        TypeExpr typeExpr = (TypeExpr) castExpr2.getTypeExpr();
        DeclType declType = typeExpr.getDeclType();
        assertEquals(Function.class, declType.getClz());
        assertEquals(Arrays.asList(Integer.class, Long.class), declType.getTypeArguments().stream()
                .map(DeclTypeArgument::getType)
                .map(DeclType::getClz)
                .collect(Collectors.toList()));
    }

    @Test
    public void fieldCallTest() {
        Program program = parse("1+a.c+2");
        BinaryOpExpr binaryOpExpr = (BinaryOpExpr) program.getStmtList().get(0);
        BinaryOpExpr left = (BinaryOpExpr) binaryOpExpr.getLeft();
        GetFieldExpr getFieldExpr = (GetFieldExpr) left.getRight();
        assertTrue(getFieldExpr.getExpr() instanceof IdExpr);
        assertEquals("a", getFieldExpr.getExpr().getKeyToken().getLexeme());
        assertEquals("c", getFieldExpr.getAttribute().getKeyToken().getLexeme());

        assertErrReport("a.(1+2)", "[Error: invalid field]\n" +
                "[Near: a.(1+2)]\n" +
                "         ^\n" +
                "[Line: 1, Column: 3]");
    }

    @Test
    public void methodCallTest() {
        Program program = parse("a.c()+d.m(1,2)");
        BinaryOpExpr binaryOpExpr = (BinaryOpExpr) program.getStmtList().get(0);
        CallExpr left = (CallExpr) binaryOpExpr.getLeft();
        assertTrue(left.getArguments().isEmpty());
        GetFieldExpr objectExpr = (GetFieldExpr) left.getTarget();
        assertEquals("a", objectExpr.getExpr().getKeyToken().getLexeme());
        assertEquals("c", objectExpr.getAttribute().getKeyToken().getLexeme());
        CallExpr right = (CallExpr) binaryOpExpr.getRight();
        assertTrue(right.getTarget() instanceof GetFieldExpr);
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
        assertEquals(Integer.class, newExpr.getClazz().getClz());

        Program program1 = parse("List<Integer> l = new ArrayList<>();");
        LocalVarDeclareStmt varDeclareStmt = (LocalVarDeclareStmt) program1.getStmtList().get(0);
        NewExpr rightExpr = (NewExpr) varDeclareStmt.getInitializer();
        assertEquals(ArrayList.class, rightExpr.getClazz().getClz());
        assertEquals(0, rightExpr.getClazz().getTypeArguments().size());

        Program program2 = parse("Map<long> l = new Map<long>();");
        LocalVarDeclareStmt varDeclareStmt2 = (LocalVarDeclareStmt) program2.getStmtList().get(0);
        NewExpr rightExpr2 = (NewExpr) varDeclareStmt2.getInitializer();
        assertEquals(Map.class, rightExpr2.getClazz().getClz());
        assertEquals(1, rightExpr2.getClazz().getTypeArguments().size());

        Program program3 = parse("java.lang.String<long> l = new java.lang.String<long>();");
        LocalVarDeclareStmt varDeclareStmt3 = (LocalVarDeclareStmt) program3.getStmtList().get(0);
        assertEquals(String.class,
                varDeclareStmt3.getVarDecl().getType().getClz());
        NewExpr rightExpr3 = (NewExpr) varDeclareStmt3.getInitializer();
        assertEquals(String.class, rightExpr3.getClazz().getClz());

        assertErrReport("new Ttt(1*9-0", "[Error: can not find class: Ttt]\n" +
                "[Near: new Ttt(1*9-0]\n" +
                "           ^^^\n" +
                "[Line: 1, Column: 5]");
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

        assertErrReport("[1,2,3", "[Error: can not find ']' to match]\n" +
                "[Near: [1,2,3]\n" +
                "       ^\n" +
                "[Line: 1, Column: 1]");
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
                "   a+=ii;" +
                "}");
        assertTrue(program1.getStmtList().get(0) instanceof LocalVarDeclareStmt);
        ForEachStmt forEachStmt = (ForEachStmt) program1.getStmtList().get(1);
        VarDecl itVar = forEachStmt.getItVar();
        assertNull(itVar.getType());
        assertEquals("ii", itVar.getVariable().getKeyToken().getLexeme());
        assertTrue(forEachStmt.getTarget() instanceof ListExpr);
        assertTrue(forEachStmt.getBody() instanceof Block);

        Program program2 = parse("for (int iid : a.b.test()) {" +
                "   a+=iid;" +
                "}");
        ForEachStmt forEachStmt2 = (ForEachStmt) program2.getStmtList().get(0);
        VarDecl itVar2 = forEachStmt2.getItVar();
        assertEquals(Integer.class, itVar2.getType().getClz());
        assertEquals("iid", itVar2.getVariable().getKeyToken().getLexeme());

        Program program3 = parse("for (int i = 0; i < 3; i++) a+=1");
        ForStmt forStmt3 = (ForStmt) program3.getStmtList().get(0);
        assertTrue(forStmt3.getForInit() instanceof LocalVarDeclareStmt);
        LocalVarDeclareStmt initStmt = (LocalVarDeclareStmt) forStmt3.getForInit();
        assertTrue(initStmt.getInitializer() instanceof ConstExpr);
        assertEquals(Integer.class, initStmt.getVarDecl().getType().getClz());
        assertEquals("i", initStmt.getVarDecl().getVariable().getKeyToken().getLexeme());
        assertEquals("0", initStmt.getInitializer().getKeyToken().getLexeme());

        Program program4 = parse("import java.util.function.Consumer;for (Consumer<long, int> iid : []);");
        ForEachStmt forEachStmtGeneric = (ForEachStmt) program4.getStmtList().get(1);
        VarDecl itVarWithGeneric = forEachStmtGeneric.getItVar();
        assertEquals(2, itVarWithGeneric.getType().getTypeArguments().size());
    }

    @Test
    public void genericTest() {
        Program program = parse("Map<String, Map<? extends String, ? super Number>> m;");
        LocalVarDeclareStmt localVarDeclareStmt = (LocalVarDeclareStmt) program.getStmtList().get(0);
        assertNull(localVarDeclareStmt.getInitializer());
        VarDecl varDecl = localVarDeclareStmt.getVarDecl();
        assertEquals("m", varDecl.getVariable().getKeyToken().getLexeme());
        DeclType declType = varDecl.getType();
        assertEquals(Map.class, declType.getClz());
        List<DeclTypeArgument> typeArguments = declType.getTypeArguments();
        assertEquals(2, typeArguments.size());
        DeclTypeArgument declTypeArgument = typeArguments.get(0);
        assertEquals(DeclTypeArgument.Bound.NONE, declTypeArgument.getBound());
        assertEquals(String.class, declTypeArgument.getType().getClz());
        DeclTypeArgument declTypeArgument1 = typeArguments.get(1);
        assertEquals(Map.class, declTypeArgument1.getType().getClz());
        assertEquals(DeclTypeArgument.Bound.NONE, declTypeArgument1.getBound());

        List<DeclTypeArgument> nestTypeArguments = declTypeArgument1.getType().getTypeArguments();
        DeclTypeArgument extendsArgument = nestTypeArguments.get(0);
        assertEquals(DeclTypeArgument.Bound.EXTENDS, extendsArgument.getBound());
        assertEquals(String.class, extendsArgument.getType().getClz());

        DeclTypeArgument superArgument = nestTypeArguments.get(1);
        assertEquals(DeclTypeArgument.Bound.SUPER, superArgument.getBound());
        assertEquals(Number.class, superArgument.getType().getClz());

        // built-in type argument
        Program program0 = parse("Map<int, long> m;");
        VarDecl builtInTypeGenericVarDecl = ((LocalVarDeclareStmt) (program0.getStmtList().get(0))).getVarDecl();
        DeclType builtInTypeGenericDeclType = builtInTypeGenericVarDecl.getType();
        assertEquals(Map.class, builtInTypeGenericDeclType.getClz());
        List<DeclTypeArgument> builtInTypeArgs = builtInTypeGenericDeclType.getTypeArguments();
        assertEquals(Integer.class, builtInTypeArgs.get(0).getType().getClz());
        assertEquals(Long.class, builtInTypeArgs.get(1).getType().getClz());

        Program program1 = parse("(List<?>)");
        GroupExpr groupExpr = (GroupExpr) program1.getStmtList().get(0);
        TypeExpr typeExpr = (TypeExpr) groupExpr.getExpr();
        DeclType questionType = typeExpr.getDeclType().getTypeArguments().get(0).getType();
        assertEquals(TokenType.QUESTION, questionType.getKeyToken().getType());
    }

    @Test
    public void programTest() {
        Program program = parse("a=2+3;a*9+2");
        assertEquals(2, program.getStmtList().getStmts().size());
        assertTrue(program.getStmtList().get(0) instanceof AssignExpr);
        assertTrue(program.getStmtList().get(1) instanceof BinaryOpExpr);

        Program program1 = parse("a=2+3;a*9+2;");
        assertEquals(2, program1.getStmtList().getStmts().size());
        assertTrue(program1.getStmtList().get(0) instanceof AssignExpr);
        assertTrue(program1.getStmtList().get(1) instanceof BinaryOpExpr);

        assertErrReport("1+1)", "[Error: missing ';' at the end of statement]\n" +
                "[Near: 1+1)]\n" +
                "          ^\n" +
                "[Line: 1, Column: 4]");
        assertErrReport("int a = 2", "[Error: missing ';' at variable declare]\n" +
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
        assertEquals(3, program0.getStmtList().getStmts().size());
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
        parse("2 + {24}");
        parse("return {10+1};");
        parse("a = {" +
                "   1+1" +
                "}");

        assertErrReport("{a=123+34", "[Error: missing '}' at block]\n" +
                "[Near: {a=123+34]\n" +
                "       ^\n" +
                "[Line: 1, Column: 1]");
    }

    @Test
    public void arrayCallTest() {
        Program program = parse("a[12]");
        IndexCallExpr indexCallExpr = (IndexCallExpr) program.getStmtList().get(0);
        assertTrue(indexCallExpr.getTarget() instanceof IdExpr);
        assertTrue(indexCallExpr.getIndex() instanceof ConstExpr);

        Program program1 = parse("a[12] = 123");
        AssignExpr assignExpr = (AssignExpr) program1.getStmtList().get(0);
        assertTrue(assignExpr.getLeft() instanceof IndexCallExpr);
        assertTrue(assignExpr.getRight() instanceof ConstExpr);

        assertErrReport("a.b[1+34", "[Error: can not find ']' to match]\n" +
                "[Near: a.b[1+34]\n" +
                "          ^\n" +
                "[Line: 1, Column: 4]");
    }

    @Test
    public void methodRefTest() {
        Program program = parse("Math::abs testOp s::charAt");
        BinaryOpExpr binaryOpExpr = (BinaryOpExpr) program.getStmtList().get(0);
        assertTrue(binaryOpExpr.getLeft() instanceof GetMethodExpr);
        assertTrue(binaryOpExpr.getRight() instanceof GetMethodExpr);
        GetMethodExpr leftGetMethod = (GetMethodExpr) binaryOpExpr.getLeft();
        assertEquals("abs", leftGetMethod.getAttribute().getKeyToken().getLexeme());
        assertEquals("Math", leftGetMethod.getExpr().getKeyToken().getLexeme());

        GetMethodExpr rightGetMethod = (GetMethodExpr) binaryOpExpr.getRight();
        assertEquals("charAt", rightGetMethod.getAttribute().getKeyToken().getLexeme());
        assertEquals("s", rightGetMethod.getExpr().getKeyToken().getLexeme());
    }

    @Test
    public void parseClsQualifiedNameTest() {
        Program programClsFirst = parse("int java = 10;java.util.ArrayList");
        assertEquals(ArrayList.class, ((MetaClass) ((ConstExpr) programClsFirst.getStmtList().get(1))
                .getConstValue()).getClz());

        assertErrReport("Map<java.cc.String, Integer> m;", "[Error: can not find class: java.cc.String]\n" +
                "[Near: ...p<java.cc.String, Integer>...]\n" +
                "                    ^^^^^^\n" +
                "[Line: 1, Column: 13]");
        assertErrReport("com.util.ArrayList l;", "[Error: can not find class: com.util.ArrayList]\n" +
                "[Near: com.util.ArrayList l;]\n" +
                "                ^^^^^^^^^\n" +
                "[Line: 1, Column: 10]");
        assertErrReport("BiConsumer bi;", "[Error: can not find class: BiConsumer]\n" +
                "[Near: BiConsumer bi;]\n" +
                "       ^^^^^^^^^^\n" +
                "[Line: 1, Column: 1]");
    }

    @Test
    public void tryCatchStmtTest() {
        Program program = parse("try {" +
                "    100" +
                "} catch (IllegalStateException | IndexOutOfBoundsException e) {" +
                "} final {" +
                "}");
        TryCatch tryCatch = (TryCatch) program.getStmtList().get(0);
        assertEquals(1, tryCatch.getBody().getStmtList().getStmts().size());
        assertEquals(Arrays.asList(IllegalStateException.class, IndexOutOfBoundsException.class),
                tryCatch.getTryCatch().get(0)
                        .getExceptions().stream()
                        .map(DeclType::getClz)
                        .collect(Collectors.toList()));
        assertEquals(0, tryCatch.getTryFinal().getStmtList().getStmts().size());
    }

    @Test
    public void ifTest() {
        Program program = parse("if (true) {" +
                "    1" +
                "}" +
                "a = 10");
        Stmt stmt = program.getStmtList().get(0);
        assertTrue(stmt instanceof IfExpr);
        parse("2 + if (true) {\n" +
                "    10\n" +
                "} else {\n" +
                "    1\n" +
                "};");
        parse("return if (true) {\n" +
                "    10\n" +
                "} else {\n" +
                "    1\n" +
                "};");
    }

    @Test
    public void precedenceTest() {
        Program ifExprProgram = parse("a = if (1==1) {1} else {2} + 1");
        AssignExpr assignExpr = (AssignExpr) ifExprProgram.getStmtList().get(0);
        assertTrue(assignExpr.getRight() instanceof BinaryOpExpr);
    }

    @Test
    public void breakContinueInLoopTest() {
        parse("for (i = 0; i < 3; i++) {" +
                "  try {" +
                "    if (a>1) {break;} else {continue;}" +
                "  } catch (Exception e) {" +
                "  }" +
                "}");
        assertErrReport("break", "[Error: 'break' keyword must in loop]\n" +
                "[Near: break]\n" +
                "       ^^^^^\n" +
                "[Line: 1, Column: 1]");
        assertErrReport("continue", "[Error: 'continue' keyword must in loop]\n" +
                "[Near: continue]\n" +
                "       ^^^^^^^^\n" +
                "[Line: 1, Column: 1]");
        assertErrReport("if (true) {continue}", "[Error: 'continue' keyword must in loop]\n" +
                "[Near: ...f (true) {continue}]\n" +
                "                    ^^^^^^^^\n" +
                "[Line: 1, Column: 12]");
        assertErrReport("for (i = 0; i < 3; i++) {a = () -> {break;}}",
                "[Error: 'break' keyword must in loop]\n" +
                        "[Near: ... = () -> {break;}}]\n" +
                        "                    ^^^^^\n" +
                        "[Line: 1, Column: 37]");
        assertErrReport("for (i = 0; i < 3; i++) {1+if (a>1) {break;} else {continue;}}",
                "[Error: 'break' keyword must in loop]\n" +
                        "[Near: ...if (a>1) {break;} else {c...]\n" +
                        "                    ^^^^^\n" +
                        "[Line: 1, Column: 38]");
        parse("for (i = 0; i < 3; i++) {if (a>1) {break;} else {continue;}}");
    }

    private void assertErrReport(String script, String expectReport) {
        try {
            new QLParser(new HashMap<>(), new com.alibaba.qlexpress4.parser.Scanner(script, QLOptions.DEFAULT_OPTIONS),
                    ImportManager.buildGlobalImportManager(Arrays.asList(
                            ImportManager.importPack("java.lang"),
                            ImportManager.importPack("java.util"),
                            ImportManager.importCls("java.util.function.Function")
                    )), DefaultClassSupplier.INSTANCE).parse();
            fail();
        } catch (QLSyntaxException e) {
            assertEquals(expectReport, e.getMessage());
        }
    }

    private Program parse(String script) {
        Map<String, Integer> mockUserOperator = new HashMap<>();
        mockUserOperator.put("testOp", QLPrecedences.MULTI);
        return new QLParser(mockUserOperator, new Scanner(script, QLOptions.DEFAULT_OPTIONS),
                ImportManager.buildGlobalImportManager(Arrays.asList(
                        ImportManager.importPack("java.lang"),
                        ImportManager.importPack("java.util"),
                        ImportManager.importCls("java.util.function.Function")
                )), DefaultClassSupplier.INSTANCE).parse();
    }

}
