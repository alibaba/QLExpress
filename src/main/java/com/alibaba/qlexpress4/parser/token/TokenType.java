package com.alibaba.qlexpress4.parser.token;

/**
 * Enumeration of all token types in the QLExpress language.
 * This enum categorizes tokens produced by the lexer for use by the parser.
 *
 * <p>Token types are organized into categories:
 * <ul>
 *   <li>Keywords - Reserved words in the language</li>
 *   <li>Literals - Constant values (numbers, strings, booleans)</li>
 *   <li>Operators - Symbols for operations</li>
 *   <li>Delimiters - Punctuation and structural symbols</li>
 *   <li>Identifiers - Variable and function names</li>
 *   <li>Special - EOF, whitespace, comments</li>
 * </ul>
 */
public enum TokenType {
    // ==================== Keywords ====================
    /** Control flow keywords */
    FOR("for", true), IF("if", true), ELSE("else", true), WHILE("while", true), BREAK("break", true), CONTINUE(
        "continue", true), RETURN("return", true), SWITCH("switch", true), CASE("case", true), DEFAULT("default", true),
    
    /** Function and macro keywords */
    FUNCTION("function", true), MACRO("macro", true),
    
    /** Import and access keywords */
    IMPORT("import", true), STATIC("static", true), NEW("new", true),
    
    /** Type keywords */
    BYTE("byte", true), SHORT("short", true), INT("int", true), LONG("long", true), FLOAT("float",
        true), DOUBLE("double", true), CHAR("char", true), BOOLEAN("boolean", true),
    
    /** Value literals */
    NULL("null", true), TRUE("true", true), FALSE("false", true),
    
    /** Type-related keywords */
    EXTENDS("extends", true), SUPER("super", true),
    
    /** Exception handling keywords */
    TRY("try", true), CATCH("catch", true), FINALLY("finally", true), THROW("throw", true),
    
    /** Additional keywords */
    THEN("then", true),
    
    /** Reserved for future use */
    CLASS("class", true), THIS("this", true),
    
    // ==================== Literals ====================
    /** Integer literal (hex, octal, binary, decimal) */
    INTEGER_LITERAL,
    
    /** Floating point literal */
    FLOATING_POINT_LITERAL,
    
    /** Integer or floating point literal (ambiguity resolved during parsing) */
    INTEGER_OR_FLOATING_LITERAL,
    
    /** Single-quoted string literal */
    QUOTE_STRING_LITERAL,
    
    /** Double-quoted string literal (start) */
    DOUBLE_QUOTE,
    
    // ==================== Operators ====================
    /** Arithmetic operators */
    ADD("+", true), // +
    SUB("-", true), // -
    MUL("*", true), // *
    DIV("/", true), // /
    MOD("%", true), // %
    INC("++", true), // ++
    DEC("--", true), // --
    
    /** Bitwise operators */
    BIT_AND("&", true), // &
    BIT_OR("|", true), // |
    BIT_XOR("^", true), // ^
    TILDE("~", true), // ~
    LEFTSHIFT("<<", true), // <<
    RIGHSHIFT(">>", true), // >>
    URSHIFT(">>>", true), // >>>
    
    /** Logical operators */
    BANG("!", true), // !
    
    /** Comparison operators */
    GT(">", true), // >
    LT("<", true), // <
    GE(">=", true), // >=
    LE("<=", true), // <=
    NOEQ("<>", true), // <>
    
    /** Assignment operators */
    EQ("=", true), // =
    ADD_ASSIGN("+=", true), // +=
    SUB_ASSIGN("-=", true), // -=
    MUL_ASSIGN("*=", true), // *=
    DIV_ASSIGN("/=", true), // /=
    MOD_ASSIGN("%=", true), // %=
    AND_ASSIGN("&=", true), // &=
    OR_ASSIGN("|=", true), // |=
    XOR_ASSIGN("^=", true), // ^=
    LSHIFT_ASSIGN("<<=", true), // <<=
    RSHIFT_ASSIGN(">>=", true), // >>=
    URSHIFT_ASSIGN(">>>=", true), // >>=
    
    /** Other operators */
    DOT(".", true), // .
    DOTMUL(".*", true), // .*
    ARROW("->", true), // ->
    DCOLON("::", true), // ::
    OPTIONAL_CHAINING("?.", true), // ?.
    SPREAD_CHAINING("*.", true), // *.
    QUESTION("?", true), // ?
    COLON(":", true), // :
    
    /** Custom operator identifier */
    OPID,
    
    // ==================== Delimiters ====================
    /** Parentheses */
    LPAREN("(", true), // (
    RPAREN(")", true), // )
    
    /** Braces */
    LBRACE("{", true), // {
    RBRACE("}", true), // }
    
    /** Brackets */
    LBRACK("[", true), // [
    RBRACK("]", true), // ]
    
    /** Other delimiters */
    SEMI(";", true), // ;
    COMMA(",", true), // ,
    
    // ==================== Identifiers ====================
    /** Regular identifier */
    ID,
    
    /** Selector variable (for interpolation) */
    SELECTOR_VARIABLE,
    
    /** Selector start (${ or #{) */
    SELECTOR_START,
    
    // ==================== Special ====================
    /** End of file */
    EOF,
    
    /** Newline character(s) */
    NEWLINE,
    
    /** Whitespace (spaces, tabs) - typically skipped */
    WS,
    
    /** Comment - typically skipped */
    COMMENT,
    
    /** Line comment - typically skipped */
    LINE_COMMENT,
    
    /** Catch-all for unrecognized characters */
    CATCH_ALL;
    
    private final String text;
    
    private final boolean hasFixedText;
    
    TokenType() {
        this.text = null;
        this.hasFixedText = false;
    }
    
    TokenType(String text) {
        this.text = text;
        this.hasFixedText = false;
    }
    
    TokenType(String text, boolean hasFixedText) {
        this.text = text;
        this.hasFixedText = hasFixedText;
    }
    
    /**
     * Returns the fixed text representation of this token type,
     * or null if the token type doesn't have a fixed text.
     *
     * @return the fixed text, or null if not applicable
     */
    public String getText() {
        return text;
    }
    
    /**
     * Returns true if this token type has a fixed text representation.
     *
     * @return true if this token type has fixed text
     */
    public boolean hasFixedText() {
        return hasFixedText;
    }
    
    /**
     * Checks if the given text matches this token type's fixed text.
     *
     * @param text the text to check
     * @return true if the text matches this token type's fixed text
     */
    public boolean matches(String text) {
        return hasFixedText && this.text.equals(text);
    }
    
    /**
     * Checks if this token type is a keyword.
     *
     * @return true if this token type is a keyword
     */
    public boolean isKeyword() {
        return hasFixedText && Character.isJavaIdentifierStart(text.charAt(0)) && Character.isLetter(text.charAt(0));
    }
    
    /**
     * Checks if this token type is an operator.
     *
     * @return true if this token type is an operator
     */
    public boolean isOperator() {
        switch (this) {
            case ADD:
            case SUB:
            case MUL:
            case DIV:
            case MOD:
            case INC:
            case DEC:
            case BIT_AND:
            case BIT_OR:
            case BIT_XOR:
            case TILDE:
            case LEFTSHIFT:
            case RIGHSHIFT:
            case URSHIFT:
            case BANG:
            case GT:
            case LT:
            case GE:
            case LE:
            case NOEQ:
            case EQ:
            case ADD_ASSIGN:
            case SUB_ASSIGN:
            case MUL_ASSIGN:
            case DIV_ASSIGN:
            case MOD_ASSIGN:
            case AND_ASSIGN:
            case OR_ASSIGN:
            case XOR_ASSIGN:
            case LSHIFT_ASSIGN:
            case RSHIFT_ASSIGN:
            case URSHIFT_ASSIGN:
            case DOT:
            case DOTMUL:
            case ARROW:
            case DCOLON:
            case OPTIONAL_CHAINING:
            case SPREAD_CHAINING:
            case QUESTION:
            case COLON:
            case OPID:
                return true;
            default:
                return false;
        }
    }
    
    /**
     * Checks if this token type is a literal.
     *
     * @return true if this token type is a literal
     */
    public boolean isLiteral() {
        switch (this) {
            case INTEGER_LITERAL:
            case FLOATING_POINT_LITERAL:
            case INTEGER_OR_FLOATING_LITERAL:
            case QUOTE_STRING_LITERAL:
            case NULL:
            case TRUE:
            case FALSE:
                return true;
            default:
                return false;
        }
    }
    
    /**
     * Checks if this token type is a delimiter.
     *
     * @return true if this token type is a delimiter
     */
    public boolean isDelimiter() {
        switch (this) {
            case LPAREN:
            case RPAREN:
            case LBRACE:
            case RBRACE:
            case LBRACK:
            case RBRACK:
            case SEMI:
            case COMMA:
                return true;
            default:
                return false;
        }
    }
}
