package com.alibaba.qlexpress4.parser;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.exception.QLSyntaxException;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

/**
 * Author: DQinYuan
 */
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
        // precise
        // int, int max value, long max value, value exceed long
        String scriptInt = "12323\n 2147483647 9223372036854775807 18446744073709552000";
        // double value, double max value, value exceed double,min ,.1, 1.,
        String scriptDouble = "1.1 1.7976931348623157E308 2.7976931348623157E308*1.-.1";
        // hex, binary, oct, e notation,
        String scriptHex = "0xfff 0b11 072 12e1 12.1E2";
        // 10l, 10L, 10d, 10.313D, 10.2f 10F
        String scriptNumSuffix = "10l 10L 10d 10.313D 10.2f 10F";

        List<Token> intTokens = scanner2List(new Scanner(scriptInt, QLOptions.builder().precise(true).build()));
        assertEquals("12323", intTokens.get(0).getLexeme());
        assertEquals(0, ((BigInteger) intTokens.get(0).getLiteral()).compareTo(new BigInteger("12323")));
        assertEquals(5, intTokens.get(0).getPos());
        assertEquals(1, intTokens.get(0).getLine());
        assertEquals(6, intTokens.get(0).getCol());

        assertEquals("2147483647", intTokens.get(1).getLexeme());
        assertEquals(0, ((BigInteger) intTokens.get(1).getLiteral()).compareTo(new BigInteger("2147483647")));
        assertEquals(17, intTokens.get(1).getPos());
        assertEquals(2, intTokens.get(1).getLine());
        assertEquals(12, intTokens.get(1).getCol());

        assertEquals("9223372036854775807", intTokens.get(2).getLexeme());
        assertEquals(0, ((BigInteger) intTokens.get(2).getLiteral())
                .compareTo(new BigInteger("9223372036854775807")));

        assertEquals("18446744073709552000", intTokens.get(3).getLexeme());
        assertEquals(0, ((BigInteger) intTokens.get(3).getLiteral())
                .compareTo(new BigInteger("18446744073709552000")));

        List<Token> doubleTokens = scanner2List(new Scanner(scriptDouble, QLOptions.builder().precise(true).build()));
        assertEquals("1.1", doubleTokens.get(0).getLexeme());
        assertEquals(0, ((BigDecimal) doubleTokens.get(0).getLiteral()).compareTo(new BigDecimal("1.1")));
        assertEquals("1.7976931348623157E308", doubleTokens.get(1).getLexeme());
        assertEquals(0, ((BigDecimal) doubleTokens.get(1).getLiteral())
                .compareTo(BigDecimal.valueOf(Double.MAX_VALUE)));
        assertEquals("2.7976931348623157E308", doubleTokens.get(2).getLexeme());
        assertEquals(0, ((BigDecimal) doubleTokens.get(2).getLiteral())
                .compareTo(new BigDecimal("2.7976931348623157E308")));
        assertEquals("1.", doubleTokens.get(4).getLexeme());
        assertEquals(0, ((BigDecimal) doubleTokens.get(4).getLiteral())
                .compareTo(new BigDecimal("1.0")));
        assertEquals(".1", doubleTokens.get(6).getLexeme());
        assertEquals(0, ((BigDecimal) doubleTokens.get(6).getLiteral()).compareTo(new BigDecimal("0.1")));

        List<Token> hexTokens = scanner2List(new Scanner(scriptHex, QLOptions.builder().precise(true).build()));
        assertEquals("0xfff", hexTokens.get(0).getLexeme());
        assertEquals(0, ((BigInteger) hexTokens.get(0).getLiteral()).compareTo(new BigInteger("4095")));
        assertEquals("0b11", hexTokens.get(1).getLexeme());
        assertEquals(0, ((BigInteger) hexTokens.get(1).getLiteral()).compareTo(new BigInteger("3")));
        assertEquals("072", hexTokens.get(2).getLexeme());
        assertEquals(0, ((BigInteger) hexTokens.get(2).getLiteral()).compareTo(new BigInteger("58")));
        assertEquals("12e1", hexTokens.get(3).getLexeme());
        assertEquals(0, ((BigDecimal) hexTokens.get(3).getLiteral()).compareTo(new BigDecimal("120")));
        assertEquals("12.1E2", hexTokens.get(4).getLexeme());
        assertEquals(0, ((BigDecimal) hexTokens.get(4).getLiteral()).compareTo(new BigDecimal("1210")));

        List<Token> suffixTokens = scanner2List(new Scanner(scriptNumSuffix, QLOptions.builder()
                .precise(true).build()));
        assertEquals("10l", suffixTokens.get(0).getLexeme());
        assertEquals(10L, suffixTokens.get(0).getLiteral());
        assertEquals("10L", suffixTokens.get(1).getLexeme());
        assertEquals(10L, suffixTokens.get(1).getLiteral());
        assertEquals(10d, suffixTokens.get(2).getLiteral());
        assertEquals(10.313d, suffixTokens.get(3).getLiteral());
        assertEquals(10.2f, suffixTokens.get(4).getLiteral());
        assertEquals(10f, suffixTokens.get(5).getLiteral());

        // not precise
        List<Token> intTokensNoPrecise = scanner2List(new Scanner(scriptInt, QLOptions.builder().precise(false).build()));
        assertEquals(12323, intTokensNoPrecise.get(0).getLiteral());
        assertEquals(2147483647, intTokensNoPrecise.get(1).getLiteral());
        assertEquals(9223372036854775807L, intTokensNoPrecise.get(2).getLiteral());
        assertEquals(0, ((BigInteger) intTokensNoPrecise.get(3).getLiteral())
                .compareTo(new BigInteger("18446744073709552000")));

        List<Token> doubleTokensNoPrecise = scanner2List(new Scanner(scriptDouble,
                QLOptions.builder().precise(false).build()));
        assertEquals(1.1d, doubleTokensNoPrecise.get(0).getLiteral());
        assertEquals(Double.MAX_VALUE, doubleTokensNoPrecise.get(1).getLiteral());
        assertEquals(0, ((BigDecimal) doubleTokensNoPrecise.get(2).getLiteral())
                .compareTo(new BigDecimal("2.7976931348623157E308")));
        assertEquals(1., doubleTokensNoPrecise.get(4).getLiteral());
        assertEquals(.1, doubleTokensNoPrecise.get(6).getLiteral());

        List<Token> hexTokensNoPrecise = scanner2List(new Scanner(scriptHex, QLOptions.builder().precise(false).build()));
        assertEquals(4095, hexTokensNoPrecise.get(0).getLiteral());
        assertEquals(3, hexTokensNoPrecise.get(1).getLiteral());
        assertEquals(58, hexTokensNoPrecise.get(2).getLiteral());
        assertEquals(120d, hexTokensNoPrecise.get(3).getLiteral());
        assertEquals(1210d, hexTokensNoPrecise.get(4).getLiteral());

        List<Token> suffixTokensNoPrecise = scanner2List(new Scanner(scriptNumSuffix, QLOptions.builder()
                .precise(true).build()));
        assertEquals(10L, suffixTokensNoPrecise.get(0).getLiteral());
        assertEquals(10L, suffixTokensNoPrecise.get(1).getLiteral());
        assertEquals(10d, suffixTokensNoPrecise.get(2).getLiteral());
        assertEquals(10.313d, suffixTokensNoPrecise.get(3).getLiteral());
        assertEquals(10.2f, suffixTokensNoPrecise.get(4).getLiteral());
        assertEquals(10f, suffixTokensNoPrecise.get(5).getLiteral());

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
        assertErrReport("1e", "[Error: invalid e-notation number]\n" +
                "[Near: 1e]\n" +
                "       ^^\n" +
                "[Line: 1, Column: 1]");
        assertErrReport("10.2l", "[Error: invalid number]\n" +
                "[Near: 10.2l]\n" +
                "       ^^^^^\n" +
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