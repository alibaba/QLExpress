package com.alibaba.qlexpress4.parser;

public class SplitCharsSet {

    public static final char CARET = '^';

    public static final char TILDE = '~';

    public static final char LPAREN = '(';

    public static final char RPAREN = ')';

    public static final char LBRACE = '{';

    public static final char RBRACE = '}';

    public static final char LBRACK = '[';

    public static final char RBRACK = ']';

    public static final char QUESTION = '?';

    public static final char COLON = ':';

    public static final char SEMI = ';';

    public static final char COMMA = ',';

    public static final char DOT = '.';

    public static final char ADD = '+';

    public static final char MINUS = '-';

    public static final char AND = '&';

    public static final char OR = '|';

    public static final char MULTI = '*';

    public static final char MOD = '%';

    public static final char EQ = '=';

    public static final char BANG = '!';

    public static final char LE = '<';

    public static final char GE = '>';

    public static final char DIVIDE = '/';

    public static final char SQUOTE = '\'';

    public static final char DQUOTE = '"';

    public static boolean isSplitChar(char c) {
        switch (c) {
            case CARET:
            case TILDE:
            case LPAREN:
            case RPAREN:
            case LBRACE:
            case RBRACE:
            case LBRACK:
            case RBRACK:
            case QUESTION:
            case COLON:
            case SEMI:
            case COMMA:
            case DOT:
            case ADD:
            case MINUS:
            case AND:
            case OR:
            case MULTI:
            case MOD:
            case EQ:
            case BANG:
            case LE:
            case GE:
            case DIVIDE:
            case SQUOTE:
            case DQUOTE:
                return true;
            default:
                return false;
        }
    }
}
