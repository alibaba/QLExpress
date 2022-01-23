package com.alibaba.qlexpress4.parser;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.exception.QLSyntaxException;
import com.alibaba.qlexpress4.parser.tree.BinaryOpExpr;
import com.alibaba.qlexpress4.parser.tree.CastExpr;
import com.alibaba.qlexpress4.parser.tree.Expr;
import com.alibaba.qlexpress4.parser.tree.FieldCallExpr;
import com.alibaba.qlexpress4.parser.tree.FunctionStmt;
import com.alibaba.qlexpress4.parser.tree.IdExpr;
import com.alibaba.qlexpress4.parser.tree.Identifier;
import com.alibaba.qlexpress4.parser.tree.ImportStmt;
import com.alibaba.qlexpress4.parser.tree.LambdaExpr;
import com.alibaba.qlexpress4.parser.tree.MethodCallExpr;
import com.alibaba.qlexpress4.parser.tree.PrefixUnaryOpExpr;
import com.alibaba.qlexpress4.parser.tree.Program;
import com.alibaba.qlexpress4.parser.tree.Stmt;
import com.alibaba.qlexpress4.parser.tree.SuffixUnaryOpExpr;
import com.alibaba.qlexpress4.parser.tree.VarDecl;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class QLParserTest {

    @Test
    public void invalidImportTest() {
        String script = "import a.b.cc int d";
        assertErrReport(script, "[Error: import must end with ';']\n" +
                "[Near: mport a.b.cc int d]\n" +
                "                 ^^\n" +
                "[Line: 1, Column: 12]");

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
    }

    @Test
    public void functionStmtTest() {
        assertErrReport("function test(weew",
                "[Error: incomplete parameter list, miss ')']\n" +
                        "[Near: ction test(weew]\n" +
                        "                 ^\n" +
                        "[Line: 1, Column: 14]");
        assertErrReport("function ttt(int if)",
                "[Error: invalid parameter name]\n" +
                        "[Near: n ttt(int if)]\n" +
                        "                 ^^\n" +
                        "[Line: 1, Column: 18]");

        Program program = parse("function myTest(int a, boolean b, MyClz myC) {}");
        FunctionStmt functionStmt = (FunctionStmt) program.getStmtList().get(0);
        assertEquals("myTest", functionStmt.getName().getKeyToken().getLexeme());
        List<VarDecl> params = functionStmt.getParams();
        VarDecl param0 = params.get(0);
        assertEquals("java.lang.Integer", param0.getType().getKeyToken().getLiteral());
        assertEquals("a", param0.getVariable().getKeyToken().getLexeme());
        VarDecl param1 = params.get(1);
        assertEquals("java.lang.Boolean", param1.getType().getKeyToken().getLiteral());
        assertEquals("b", param1.getVariable().getKeyToken().getLexeme());
        VarDecl param2 = params.get(2);
        assertEquals("MyClz", param2.getType().getKeyToken().getLiteral());
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
        BinaryOpExpr group = (BinaryOpExpr) right.getRight();
        assertEquals(TokenType.ADD, group.getKeyToken().getType());
        assertEquals("4", group.getLeft().getKeyToken().getLexeme());
        assertEquals("5", group.getRight().getKeyToken().getLexeme());
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
        assertEquals("java.lang.Integer", castExpr.getCastTarget().getKeyToken().getLiteral());
        PrefixUnaryOpExpr prefixUnaryOpExpr = (PrefixUnaryOpExpr) castExpr.getExpr();
        assertEquals(TokenType.SUB, prefixUnaryOpExpr.getKeyToken().getType());
        assertEquals("4", prefixUnaryOpExpr.getExpr().getKeyToken().getLexeme());

        Program program1 = parse("(Integer)-4");
        BinaryOpExpr binaryOpExpr = (BinaryOpExpr) program1.getStmtList().get(0);
        assertEquals("Integer", binaryOpExpr.getLeft().getKeyToken().getLexeme());
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
        MethodCallExpr left = (MethodCallExpr) binaryOpExpr.getLeft();
        assertTrue(left.getArguments().isEmpty());
        FieldCallExpr objectExpr = (FieldCallExpr) left.getObjectExpr();
        assertEquals("a", objectExpr.getExpr().getKeyToken().getLexeme());
        assertEquals("c", objectExpr.getAttribute().getKeyToken().getLexeme());
        MethodCallExpr right = (MethodCallExpr) binaryOpExpr.getRight();
        assertTrue(right.getObjectExpr() instanceof FieldCallExpr);
        assertEquals(Arrays.asList("1", "2"), right.getArguments().stream()
                .map(Expr::getKeyToken)
                .map(Token::getLexeme)
                .collect(Collectors.toList()));
    }

    private void assertErrReport(String script, String expectReport) {
        try {
            new QLParser(new HashMap<>(), new Scanner(script, QLOptions.DEFAULT_OPTIONS)).parse();
        } catch (QLSyntaxException e) {
            assertEquals(expectReport, e.getMessage());
        }
    }

    private Program parse(String script) {
        return new QLParser(new HashMap<>(), new Scanner(script, QLOptions.DEFAULT_OPTIONS)).parse();
    }
}