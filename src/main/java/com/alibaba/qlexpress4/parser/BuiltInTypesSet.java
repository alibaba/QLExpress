package com.alibaba.qlexpress4.parser;

public class BuiltInTypesSet {

    public static final String BYTE = "byte";
    public static final String BYTE_LITERAL = "java.lang.Byte";

    public static final String SHORT = "short";
    public static final String SHORT_LITERAL = "java.lang.Short";

    public static final String INT = "int";
    public static final String INT_LITERAL = "java.lang.Integer";

    public static final String LONG = "long";
    public static final String LONG_LITERAL = "java.lang.Long";

    public static final String FLOAT = "float";
    public static final String FLOAT_LITERAL = "java.lang.Float";

    public static final String DOUBLE = "double";
    public static final String DOUBLE_LITERAL = "java.lang.Double";

    public static final String BOOLEAN = "boolean";
    public static final String BOOLEAN_LITERAL = "java.lang.Boolean";

    public static final String CHAR = "char";
    public static final String CHAR_LITERAL = "java.lang.Character";

    public static String getLiteral(String lexeme) {
        switch (lexeme) {
            case BYTE:
                return BYTE_LITERAL;
            case SHORT:
                return SHORT_LITERAL;
            case INT:
                return INT_LITERAL;
            case LONG:
                return LONG_LITERAL;
            case FLOAT:
                return FLOAT_LITERAL;
            case DOUBLE:
                return DOUBLE_LITERAL;
            case BOOLEAN:
                return BOOLEAN_LITERAL;
            case CHAR:
                return CHAR_LITERAL;
            default:
                return null;
        }
    }
}
