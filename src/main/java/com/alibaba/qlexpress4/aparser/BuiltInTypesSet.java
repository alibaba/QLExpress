package com.alibaba.qlexpress4.aparser;

public class BuiltInTypesSet {
    
    public static final String BYTE = "byte";
    
    public static final String SHORT = "short";
    
    public static final String INT = "int";
    
    public static final String LONG = "long";
    
    public static final String FLOAT = "float";
    
    public static final String DOUBLE = "double";
    
    public static final String BOOLEAN = "boolean";
    
    public static final String CHAR = "char";
    
    public static Class<?> getCls(String lexeme) {
        switch (lexeme) {
            case BYTE:
                return Byte.class;
            case SHORT:
                return Short.class;
            case INT:
                return Integer.class;
            case LONG:
                return Long.class;
            case FLOAT:
                return Float.class;
            case DOUBLE:
                return Double.class;
            case BOOLEAN:
                return Boolean.class;
            case CHAR:
                return Character.class;
            default:
                return null;
        }
    }
}
