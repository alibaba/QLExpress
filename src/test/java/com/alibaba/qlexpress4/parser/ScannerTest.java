package com.alibaba.qlexpress4.parser;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.exception.QLSyntaxException;
import com.alibaba.qlexpress4.parser.tree.Program;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class ScannerTest {

    @Test
    public void stringTest() {
        Token token = new Scanner("'ccc\\tmb'", QLOptions.DEFAULT_OPTIONS).next();
        assertEquals("ccc\\tmb", token.getLiteral());
        assertEquals("'ccc\\tmb'", token.getLexeme());
        assertEquals(9, token.getPos());
        assertEquals(10, token.getCol());

        Token token1 = new Scanner("\"ccc\\tmb\"", QLOptions.DEFAULT_OPTIONS).next();
        assertEquals("ccc\tmb", token1.getLiteral());
        assertEquals("\"ccc\\tmb\"", token1.getLexeme());

        Token token2 = new Scanner("\"aaa\" 2323", QLOptions.DEFAULT_OPTIONS).next();
        assertEquals(5, token2.getPos());
        assertEquals(6, token2.getCol());

        // row string syntax error
        assertErrReport("\"ddwd\" 'wewew\nefef'", "[Error: ''(raw string) line break]\n" +
                "[Near: \"ddwd\" 'wewew efef']\n" +
                "              ^^^^^^^\n" +
                "[Line: 1, Column: 8]");
        assertErrReport("'dfefef", "[Error: ''(raw string) not close]\n" +
                "[Near: 'dfefef]\n" +
                "       ^^^^^^^\n" +
                "[Line: 1, Column: 1]");

        // string syntax error
        assertErrReport("'ddwd' \"wewew\nefef\"", "[Error: \"\"(string) line break]\n" +
                "[Near: 'ddwd' \"wewew efef\"]\n" +
                "              ^^^^^^^\n" +
                "[Line: 1, Column: 8]");
        assertErrReport("\"dfefef", "[Error: \"\"(string) not close]\n" +
                "[Near: \"dfefef]\n" +
                "       ^^^^^^^\n" +
                "[Line: 1, Column: 1]");
    }

    @Test
    public void importTest() {
        Token token = new Scanner("import", QLOptions.DEFAULT_OPTIONS).next();
        assertEquals(TokenType.KEY_WORD, token.getType());
        assertEquals("import", token.getLexeme());
    }

    @Test
    public void number() {
        Scanner scanner = new Scanner("12323 777", QLOptions.DEFAULT_OPTIONS);
        Token token = scanner.next();
        assertEquals("12323", token.getLexeme());
        assertEquals(0, ((BigDecimal) token.getLiteral()).compareTo(new BigDecimal("12323")));
        assertEquals(5, token.getPos());
        assertEquals(6, token.getCol());

        Token token1 = scanner.next();
        assertEquals("777", token1.getLexeme());
        assertEquals(0, ((BigDecimal) token1.getLiteral()).compareTo(new BigDecimal("777")));
        assertEquals(9, token1.getPos());
        assertEquals(10, token1.getCol());

        // not precise
        QLOptions qlOptions = QLOptions.builder().precise(false).build();
        Scanner scanner1 = new Scanner("123 7.77\n 3.4f 3.4F 3.5d 3.5D", qlOptions);
        Token token11 = scanner1.next();
        assertEquals("123", token11.getLexeme());
        assertTrue(token11.getLiteral() instanceof Integer);
        assertEquals(123, token11.getLiteral());

        Token token12 = scanner1.next();
        assertEquals("7.77", token12.getLexeme());
        assertTrue(token12.getLiteral() instanceof Double);
        assertEquals(7.77, token12.getLiteral());

        Token token13 = scanner1.next();
        assertEquals("3.4f", token13.getLexeme());
        assertTrue(token13.getLiteral() instanceof Float);
        assertEquals(3.4f, token13.getLiteral());
        assertEquals(2, token13.getLine());
        assertEquals(6, token13.getCol());

        Token token14 = scanner1.next();
        assertEquals("3.4F", token14.getLexeme());
        assertTrue(token14.getLiteral() instanceof Float);
        assertEquals(3.4f, token14.getLiteral());

        Token token15 = scanner1.next();
        assertEquals("3.5d", token15.getLexeme());
        assertTrue(token15.getLiteral() instanceof Double);
        assertEquals(3.5d, token15.getLiteral());

        Token token16 = scanner1.next();
        assertEquals("3.5D", token16.getLexeme());
        assertTrue(token16.getLiteral() instanceof Double);
        assertEquals(3.5d, token16.getLiteral());

        // split char test
        Scanner scanner2 = new Scanner("1+2*(4+5)", QLOptions.DEFAULT_OPTIONS);
        assertEquals(Arrays.asList("1", "+", "2", "*", "(", "4", "+", "5", ")"), scanner2List(scanner2).stream()
                .map(Token::getLexeme).collect(Collectors.toList()));

        // syntax error
        assertErrReport("12 1ab", "[Error: invalid number]\n" +
                "[Near: 12 1ab]\n" +
                "          ^^\n" +
                "[Line: 1, Column: 4]");
        assertErrReport("5.1a", "[Error: invalid number]\n" +
                "[Near: 5.1a]\n" +
                "       ^^^^\n" +
                "[Line: 1, Column: 1]");

        Scanner scanner3 = new Scanner("3.", QLOptions.builder().precise(false).build());
        Token doubleToken3 = scanner3.next();
        assertEquals(3.0, doubleToken3.getLiteral());
    }

    @Test
    public void idTest() {
        Scanner scanner = new Scanner("abc if while ifff", QLOptions.DEFAULT_OPTIONS);
        List<Token> tList = scanner2List(scanner);
        assertEquals(TokenType.ID, tList.get(0).getType());
        assertEquals("abc", tList.get(0).getLexeme());
        assertEquals(3, tList.get(0).getPos());
        assertEquals(4, tList.get(0).getCol());

        assertEquals(TokenType.KEY_WORD, tList.get(1).getType());
        assertEquals("if", tList.get(1).getLexeme());
        assertEquals(TokenType.KEY_WORD, tList.get(2).getType());
        assertEquals("while", tList.get(2).getLexeme());
        assertEquals(TokenType.ID, tList.get(3).getType());
        assertEquals("ifff", tList.get(3).getLexeme());
    }

    @Test
    public void functionTest() {
        Scanner scanner = new Scanner("function add(a, b)", QLOptions.DEFAULT_OPTIONS);
        List<Token> tokens = scanner2List(scanner);
        List<String> tokenLexemeList = tokens
                .stream().map(Token::getLexeme).collect(Collectors.toList());
        assertEquals(Arrays.asList("function", "add", "(", "a", ",", "b", ")"), tokenLexemeList);
        assertEquals(13, tokens.get(2).getPos());
        assertEquals(14, tokens.get(2).getCol());
    }

    @Test
    public void genericTest() {
        Scanner scanner = new Scanner("Map<<S, Map<C>>>",
                QLOptions.DEFAULT_OPTIONS);
        List<Token> tokens = scanner2List(scanner);
        List<String> tokenLexemeList = tokens
                .stream().map(Token::getLexeme).collect(Collectors.toList());
        assertEquals(Arrays.asList("Map", "<", "<", "S", ",", "Map", "<", "C", ">", ">", ">"), tokenLexemeList);
        assertEquals(Arrays.asList(4, 5, 6, 7, 8, 12, 13, 14, 15, 16, 17), tokens.stream().map(Token::getCol)
                .collect(Collectors.toList()));

        Scanner scanner1 = new Scanner("a < <",
                QLOptions.DEFAULT_OPTIONS);
        assertEquals(Arrays.asList("a", "<", "<"), scanner2Strings(scanner1));
    }

    private List<Token> scanner2List(Scanner scanner) {
        Token t;
        List<Token> res = new ArrayList<>();
        while ((t = scanner.next()) != null) {
            res.add(t);
        }
        return res;
    }

    private List<String> scanner2Strings(Scanner scanner) {
        return scanner2List(scanner).stream()
                .map(Token::getLexeme)
                .collect(Collectors.toList());
    }

    private void assertErrReport(String script, String expectReport) {
        try {
            Scanner scanner = new Scanner(script, QLOptions.DEFAULT_OPTIONS);
            while (scanner.next() != null);
        } catch (QLSyntaxException e) {
            assertEquals(expectReport, e.getMessage());
        }
    }

}