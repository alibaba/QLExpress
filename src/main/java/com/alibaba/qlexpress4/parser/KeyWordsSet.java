package com.alibaba.qlexpress4.parser;

public class KeyWordsSet {

    public static final String FOR = "for";

    public static final String IF = "if";

    public static final String ELSE = "else";

    public static final String IN = "in";

    public static final String LIKE = "like";

    public static final String WHILE = "while";

    public static final String BREAK = "break";

    public static final String CONTINUE = "continue";

    public static final String RETURN = "return";

    public static final String FUNCTION = "function";

    public static final String MACRO = "macro";

    public static final String IMPORT = "import";

    // embed unboxed data type
    public static final String BYTE = "byte";

    public static final String SHORT = "short";

    public static final String INT = "int";

    public static final String LONG = "long";

    public static final String FLOAT = "float";

    public static final String DOUBLE = "double";

    public static final String CHAR = "char";

    public static final String BOOL = "boolean";

    public static final String AND = "and";

    public static final String OR = "or";

    public static final String INSTANCEOF = "instanceof";

    // special values
    public static final String NULL = "null";

    public static final String TRUE = "true";

    public static final String FALSE = "false";

    // unuseful now, but reserve them for future
    public static final String CLASS = "class";
    public static final String NEW = "new";
    public static final String THIS = "this";

    public static boolean isKeyWord(String word) {
        switch (word) {
            case FOR:
            case IF:
            case ELSE:
            case IN:
            case LIKE:
            case WHILE:
            case BREAK:
            case CONTINUE:
            case RETURN:
            case FUNCTION:
            case MACRO:
            case IMPORT:
            case BYTE:
            case SHORT:
            case INT:
            case LONG:
            case FLOAT:
            case DOUBLE:
            case CHAR:
            case BOOL:
            case AND:
            case OR:
            case INSTANCEOF:
            case NULL:
            case TRUE:
            case FALSE:
            case CLASS:
            case NEW:
            case THIS:
                return true;
            default:
                return false;
        }
    }

}
