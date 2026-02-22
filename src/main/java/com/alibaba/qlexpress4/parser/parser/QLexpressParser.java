package com.alibaba.qlexpress4.parser.parser;

import com.alibaba.qlexpress4.parser.token.Token;
import com.alibaba.qlexpress4.parser.token.TokenType;
import com.alibaba.qlexpress4.parser.ast.*;
import com.alibaba.qlexpress4.parser.ast.ExpressionNode;
import com.alibaba.qlexpress4.parser.ast.TernaryNode;
import com.alibaba.qlexpress4.parser.ast.LambdaNode;
import com.alibaba.qlexpress4.parser.ast.MethodCallNode;
import com.alibaba.qlexpress4.parser.ast.ConstructorCallNode;
import com.alibaba.qlexpress4.parser.ast.ArrayAccessNode;
import com.alibaba.qlexpress4.parser.ast.CastNode;
import com.alibaba.qlexpress4.parser.ast.InstanceOfNode;
import com.alibaba.qlexpress4.parser.ast.TypeNode;
import com.alibaba.qlexpress4.parser.ast.ParameterNode;
import com.alibaba.qlexpress4.aparser.ParserOperatorManager;
import com.alibaba.qlexpress4.runtime.operator.OperatorManager;
import com.alibaba.qlexpress4.QLPrecedences;

import java.math.BigInteger;
import java.util.List;
import java.util.Collections;
import java.util.ArrayList;

/**
 * Hand-written recursive descent parser for QLExpress language.
 *
 * <p>This parser consumes a stream of tokens from the lexer and produces an AST.
 * It implements:
 * <ul>
 *   <li>Token stream management with lookahead</li>
 *   <li>Error handling with line/column information</li>
 *   <li>Basic parsing methods (peek, consume, expect, match)</li>
 *   <li>Unary operator parsing (prefix and suffix)</li>
 *   <li>Binary operator parsing with proper precedence</li>
 * </ul>
 */
public class QLexpressParser {
    private final List<Token> tokens;
    
    private int position;
    
    private Token lastToken;
    
    private final ParserOperatorManager operatorManager;
    
    /**
     * Creates a new parser for the given token stream.
     * Uses default OperatorManager for operator precedence and type resolution.
     *
     * @param tokens the token stream from the lexer
     */
    public QLexpressParser(List<Token> tokens) {
        this(tokens, new OperatorManager());
    }
    
    /**
     * Creates a new parser for the given token stream with custom operator manager.
     *
     * @param tokens the token stream from the lexer
     * @param operatorManager the operator manager for precedence and type resolution
     */
    public QLexpressParser(List<Token> tokens, ParserOperatorManager operatorManager) {
        this.tokens = tokens != null ? tokens : Collections.emptyList();
        this.position = 0;
        this.operatorManager = operatorManager != null ? operatorManager : new OperatorManager();
    }
    
    /**
     * Parses the entire token stream as a program.
     *
     * @return the root ProgramNode of the AST
     * @throws ParseException if parsing fails
     */
    public ProgramNode parseProgram()
        throws ParseException {
        int line = 1, column = 1;
        String source = null;
        if (!tokens.isEmpty()) {
            Token first = tokens.get(0);
            line = first.getLine();
            column = first.getColumn();
            source = first.getSource();
        }
        
        // Parse statements until EOF
        List<StatementNode> statements = new ArrayList<>();
        
        // Skip leading newlines
        skipNewlines();
        
        while (!isEOF()) {
            StatementNode stmt = parseStatement();
            if (stmt != null) {
                statements.add(stmt);
            }
            // Skip newlines between statements
            skipNewlines();
        }
        
        return new ProgramNode(line, column, source, statements);
    }
    
    // ==================== Expression Parsing ====================
    
    /**
     * Parses a primary expression.
     * <p>
     * Primary expressions include:
     * <ul>
     *   <li>Literals (numbers, strings, booleans, null)</li>
     *   <li>Identifiers (variable names)</li>
     *   <li>Parenthesized expressions</li>
     *   <li>Lambda expressions</li>
     *   <li>Type casts</li>
     *   <li>Constructor calls (new)</li>
     *   <li>Type literals (primitive types)</li>
     *   <li>List literals</li>
     *   <li>Map literals</li>
     *   <li>Block expressions</li>
     * </ul>
     *
     * @return the parsed expression node
     * @throws ParseException if parsing fails
     */
    public ExpressionNode parsePrimary()
        throws ParseException {
        Token current = peek();
        if (current == null) {
            throw error("Unexpected end of input, expected expression");
        }

        // Check for lambda expression first (can start with ID or LPAREN)
        if (shouldParseLambda()) {
            return parseLambda();
        }

        switch (current.getType()) {
            case INTEGER_LITERAL:
            case FLOATING_POINT_LITERAL:
            case INTEGER_OR_FLOATING_LITERAL:
            case QUOTE_STRING_LITERAL:
            case DOUBLE_QUOTE:
            case TRUE:
            case FALSE:
            case NULL:
                return parseLiteral();

            case ID:
            case FUNCTION:
            case CASE:
            case DEFAULT:
                return parsePrimaryWithIdentifier();

            case LPAREN:
                return parseParenthesizedOrCast();

            case NEW:
                return parseConstructorCall();

            case BYTE:
            case SHORT:
            case INT:
            case LONG:
            case FLOAT:
            case DOUBLE:
            case CHAR:
            case BOOLEAN:
                return parseTypeLiteral();

            case LBRACK:
                return parseListLiteral();

            case LBRACE:
                return parseBraceExpression();

            case SELECTOR_START:
                // Handle custom selector interpolation like ${var} or #{var}
                return parseSelectorStart();

            // These statements can also be used as expressions in QLExpress
            case TRY:
                return parseTryCatch();

            case IF:
                return parseIf();

            case SWITCH:
                // SWITCH can be used as an identifier or as a switch expression
                // Check if followed by LPAREN to determine which
                if (peek(1) != null && peek(1).getType() == TokenType.LPAREN) {
                    return parseSwitch();
                }
                return parsePrimaryWithIdentifier();

            default:
                throw error("Expected expression but found " + current.getType());
        }
    }
    
    /**
     * Parses a literal expression.
     *
     * @return the LiteralNode
     * @throws ParseException if parsing fails
     */
    private ExpressionNode parseLiteral()
        throws ParseException {
        Token token = consume();
        Object value;
        
        switch (token.getType()) {
            case INTEGER_LITERAL:
                value = parseIntegerLiteral(token.getValue());
                break;
            case FLOATING_POINT_LITERAL:
                value = parseFloatLiteral(token.getValue());
                break;
            case INTEGER_OR_FLOATING_LITERAL:
                // Need to determine if this is actually an integer or float
                String strValue = token.getValue();
                if (strValue.contains(".") || strValue.contains("e") || strValue.contains("E") || strValue.endsWith("f")
                    || strValue.endsWith("F") || strValue.endsWith("d") || strValue.endsWith("D")) {
                    value = parseFloatLiteral(strValue);
                }
                else {
                    value = parseIntegerLiteral(strValue);
                }
                break;
            case QUOTE_STRING_LITERAL:
                value = parseStringLiteral(token.getValue());
                break;
            case DOUBLE_QUOTE:
                // Double-quoted strings are already processed by the lexer
                value = token.getValue();
                break;
            case TRUE:
                value = Boolean.TRUE;
                break;
            case FALSE:
                value = Boolean.FALSE;
                break;
            case NULL:
                value = null;
                break;
            default:
                throw error("Expected literal but found " + token.getType());
        }
        
        return new LiteralNode(token.getLine(), token.getColumn(), token.getSource(), value);
    }
    
    /**
     * Parses an identifier expression.
     *
     * @return the IdentifierNode
     * @throws ParseException if parsing fails
     */
    private ExpressionNode parseIdentifier()
        throws ParseException {
        Token token = expect(TokenType.ID);
        return new IdentifierNode(token.getLine(), token.getColumn(), token.getSource(), token.getValue());
    }
    
    /**
     * Parses a parenthesized expression.
     *
     * @return the inner expression node
     * @throws ParseException if parsing fails
     */
    private ExpressionNode parseParenthesizedExpression()
        throws ParseException {
        Token lparen = expect(TokenType.LPAREN);
        
        // Skip newlines inside parentheses
        skipNewlines();
        
        ExpressionNode expr = parseBinary(0);
        
        // Skip newlines before closing paren
        skipNewlines();
        
        expect(TokenType.RPAREN);
        
        return expr;
    }
    
    /**
     * Parses an integer literal from string value.
     * Handles hex (0x), binary (0b), octal (0), and decimal formats.
     * Uses BigInteger to handle arbitrarily large integers.
     *
     * @param value the string value from the token
     * @return the parsed integer (as Integer, Long, or BigInteger)
     */
    private Object parseIntegerLiteral(String value) {
        if (value == null || value.isEmpty()) {
            return 0;
        }

        value = value.replace("_", ""); // Remove digit separators

        // Constants for type detection
        final BigInteger MAX_INTEGER = BigInteger.valueOf(Integer.MAX_VALUE);
        final BigInteger MAX_LONG = BigInteger.valueOf(Long.MAX_VALUE);
        final BigInteger MIN_LONG = BigInteger.valueOf(Long.MIN_VALUE);
        final BigInteger MIN_INTEGER = BigInteger.valueOf(Integer.MIN_VALUE);

        // Check for type suffix
        boolean isLong = false;
        if (value.endsWith("l") || value.endsWith("L")) {
            isLong = true;
            value = value.substring(0, value.length() - 1);
        }

        // Parse as BigInteger first to handle arbitrarily large numbers
        BigInteger parsedValue;
        if (value.startsWith("0x") || value.startsWith("0X")) {
            // Hexadecimal
            parsedValue = new BigInteger(value.substring(2), 16);
        }
        else if (value.startsWith("0b") || value.startsWith("0B")) {
            // Binary
            parsedValue = new BigInteger(value.substring(2), 2);
        }
        else if (value.length() > 1 && value.charAt(0) == '0') {
            // Octal
            parsedValue = new BigInteger(value, 8);
        }
        else {
            // Decimal
            parsedValue = new BigInteger(value);
        }

        if (isLong) {
            // With 'l' or 'L' suffix, always return long (or BigInteger if too large)
            if (parsedValue.compareTo(MAX_LONG) > 0 || parsedValue.compareTo(MIN_LONG) < 0) {
                return parsedValue; // Return BigInteger for values outside Long range
            }
            return parsedValue.longValue();
        }
        else {
            // Auto type: return Integer if fits, Long if fits in long, otherwise BigInteger
            if (parsedValue.compareTo(MAX_INTEGER) <= 0 && parsedValue.compareTo(MIN_INTEGER) >= 0) {
                return parsedValue.intValue();
            }
            else if (parsedValue.compareTo(MAX_LONG) <= 0 && parsedValue.compareTo(MIN_LONG) >= 0) {
                return parsedValue.longValue();
            }
            else {
                return parsedValue; // Return BigInteger for very large values
            }
        }
    }
    
    /**
     * Parses a floating point literal from string value.
     *
     * @param value the string value from the token
     * @return the parsed number (as Double or Float)
     */
    private Object parseFloatLiteral(String value) {
        if (value == null || value.isEmpty()) {
            return 0.0;
        }
        
        value = value.replace("_", ""); // Remove digit separators
        
        // Check for type suffix
        boolean isFloat = value.endsWith("f") || value.endsWith("F");
        boolean isDouble = value.endsWith("d") || value.endsWith("D");
        if (isFloat || isDouble) {
            value = value.substring(0, value.length() - 1);
        }
        
        double parsedValue = Double.parseDouble(value);
        
        if (isFloat) {
            return (float)parsedValue;
        }
        else if (isDouble) {
            return parsedValue;
        }
        else {
            // Default to Double if no suffix
            return parsedValue;
        }
    }
    
    /**
     * Parses a string literal, processing escape sequences.
     *
     * @param value the string value from the token (including quotes)
     * @return the processed string without quotes
     */
    private String parseStringLiteral(String value) {
        if (value == null || value.isEmpty()) {
            return "";
        }
        
        // Remove surrounding quotes
        if (value.length() >= 2) {
            char quote = value.charAt(0);
            if (quote == '\'' || quote == '"') {
                value = value.substring(1, value.length() - 1);
            }
        }
        
        // Process escape sequences
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if (c == '\\' && i + 1 < value.length()) {
                char next = value.charAt(++i);
                switch (next) {
                    case 'n':
                        sb.append('\n');
                        break;
                    case 'r':
                        sb.append('\r');
                        break;
                    case 't':
                        sb.append('\t');
                        break;
                    case 'b':
                        sb.append('\b');
                        break;
                    case 'f':
                        sb.append('\f');
                        break;
                    case '\'':
                        sb.append('\'');
                        break;
                    case '"':
                        sb.append('"');
                        break;
                    case '\\':
                        sb.append('\\');
                        break;
                    default:
                        // Unknown escape, keep as-is
                        sb.append('\\').append(next);
                        break;
                }
            }
            else {
                sb.append(c);
            }
        }
        
        return sb.toString();
    }
    
    /**
     * Skips newline tokens if present.
     */
    private void skipNewlines() {
        while (match(TokenType.NEWLINE)) {
            try {
                consume();
            }
            catch (ParseException e) {
                // Should not happen since we checked with match()
                break;
            }
        }
    }
    
    // ==================== Ternary Expression Parsing ====================
    
    /**
     * Checks if the current token could be the start of a lambda expression.
     * Lambdas can start with:
     * - An identifier followed by ARROW
     * - LPAREN followed by parameters and ARROW
     *
     * @return true if this looks like a lambda
     */
    private boolean shouldParseLambda() {
        Token current = peek();
        if (current == null) {
            return false;
        }
        // Lambda must start with ID or LPAREN
        if (current.getType() == TokenType.LPAREN) {
            // Check if there's an ARROW after the closing paren
            // Find the matching RPAREN and check if next token is ARROW
            int depth = 1;
            int offset = 1; // Start after the current LPAREN
            while (depth > 0 && offset < 100) { // Limit lookahead
                Token t = peek(offset);
                if (t == null) {
                    return false;
                }
                if (t.getType() == TokenType.LPAREN) {
                    depth++;
                }
                else if (t.getType() == TokenType.RPAREN) {
                    depth--;
                }
                offset++;
            }
            // After finding matching RPAREN, check if next is ARROW
            Token afterRParen = peek(offset);
            return afterRParen != null && afterRParen.getType() == TokenType.ARROW;
        }
        if (current.getType() == TokenType.ID) {
            // Check if next token is ARROW
            Token next = peek(1);
            return next != null && next.getType() == TokenType.ARROW;
        }
        return false;
    }
    
    // ==================== Lambda Expression Parsing ====================
    
    /**
     * Parses a lambda expression.
     * <p>
     * Lambda expressions have the form:
     * <ul>
     *   <li>parameter -&gt; expression</li>
     *   <li>parameter -&gt; { statements }</li>
     *   <li>(param1, param2, ...) -&gt; expression</li>
     *   <li>(param1, param2, ...) -&gt; { statements }</li>
     * </ul>
     *
     * @return the LambdaNode
     * @throws ParseException if parsing fails
     */
    private ExpressionNode parseLambda()
        throws ParseException {
        int line = peek().getLine();
        int column = peek().getColumn();
        String source = peek().getSource();
        
        // Parse parameters
        List<ParameterNode> parameters = parseLambdaParameters();
        
        // Expect arrow
        skipNewlines();
        if (!match(TokenType.ARROW)) {
            throw error("Expected '->' in lambda expression");
        }
        consume();
        skipNewlines();
        
        // Parse body (expression or block)
        Node body;
        if (match(TokenType.LBRACE)) {
            body = parseBlock();
        }
        else {
            body = parseExpression();
        }
        
        return new LambdaNode(line, column, source, parameters, body);
    }
    
    /**
     * Parses lambda parameters.
     * <p>
     * Lambda parameters can be:
     * <ul>
     *   <li>A single identifier: param</li>
     *   <li>A parenthesized list: (param1, param2, ...)</li>
     * </ul>
     *
     * @return the list of parameters
     * @throws ParseException if parsing fails
     */
    private List<ParameterNode> parseLambdaParameters()
        throws ParseException {
        List<ParameterNode> parameters = new ArrayList<>();
        
        if (match(TokenType.LPAREN)) {
            consume();
            skipNewlines();
            
            if (!match(TokenType.RPAREN)) {
                parameters.add(parseLambdaParameter());
                skipNewlines();
                while (match(TokenType.COMMA)) {
                    consume();
                    skipNewlines();
                    parameters.add(parseLambdaParameter());
                    skipNewlines();
                }
            }
            
            expect(TokenType.RPAREN);
        }
        else {
            // Single parameter without parentheses
            Token param = expect(TokenType.ID);
            parameters.add(new ParameterNode(null, param.getValue()));
        }
        
        return parameters;
    }
    
    /**
     * Parses a single lambda parameter.
     * <p>
     * Lambda parameters can have optional type: [Type] name
     *
     * @return the parameter node
     * @throws ParseException if parsing fails
     */
    private ParameterNode parseLambdaParameter()
        throws ParseException {
        String typeName = null;
        String paramName;
        
        // Check if this is a typed parameter
        // Look ahead to see if we have (TypeKeyword or ID) followed by ID
        Token current = peek();
        if (current != null && (current.getType() == TokenType.ID || isTypeKeywordToken(current.getType()))) {
            Token next = peek(1);
            if (next != null && next.getType() == TokenType.ID) {
                // This is a typed parameter like "int x" or "String name"
                if (isTypeKeywordToken(current.getType())) {
                    typeName = current.getValue();
                    consume();
                }
                else {
                    // Check if it's a known type keyword by value
                    if (isTypeKeyword(current.getValue())) {
                        typeName = current.getValue();
                        consume();
                    }
                    else {
                        // Could be qualified type like "java.lang.String"
                        typeName = parseQualifiedTypeName();
                    }
                }
                Token name = expect(TokenType.ID);
                paramName = name.getValue();
            }
            else {
                // Just an identifier parameter (no type)
                Token param = expect(TokenType.ID);
                paramName = param.getValue();
            }
        }
        else {
            Token param = expect(TokenType.ID);
            paramName = param.getValue();
        }
        
        return new ParameterNode(typeName, paramName);
    }
    
    /**
     * Parses a qualified type name (e.g., "java.lang.String").
     * Also handles type keywords (int, long, etc.).
     *
     * @return the qualified type name
     * @throws ParseException if parsing fails
     */
    private String parseQualifiedTypeName()
        throws ParseException {
        StringBuilder sb = new StringBuilder();
        
        // Check for type keyword first
        Token current = peek();
        if (current != null && isTypeKeywordToken(current.getType())) {
            sb.append(consume().getValue());
        }
        else {
            sb.append(expect(TokenType.ID).getValue());
        }
        
        while (match(TokenType.DOT)) {
            consume();
            // After dot, we expect an ID (type keywords can't follow dots)
            sb.append(".").append(expect(TokenType.ID).getValue());
        }
        
        return sb.toString();
    }
    
    /**
     * Checks if the given token type is a type keyword.
     *
     * @param type the token type to check
     * @return true if it's a type keyword token
     */
    private boolean isTypeKeywordToken(TokenType type) {
        return type == TokenType.BYTE || type == TokenType.SHORT || type == TokenType.INT || type == TokenType.LONG
            || type == TokenType.FLOAT || type == TokenType.DOUBLE || type == TokenType.CHAR
            || type == TokenType.BOOLEAN;
    }
    
    /**
     * Checks if the given string is a type keyword.
     *
     * @param value the string to check
     * @return true if it's a type keyword
     */
    private boolean isTypeKeyword(String value) {
        if (value == null) {
            return false;
        }
        return value.equals("byte") || value.equals("short") || value.equals("int") || value.equals("long")
            || value.equals("float") || value.equals("double") || value.equals("char") || value.equals("boolean")
            || value.equals("String") || value.equals("Object") || value.equals("Integer") || value.equals("Long")
            || value.equals("Float") || value.equals("Double") || value.equals("Boolean") || value.equals("Character");
    }
    
    // ==================== Method Call and Path Parsing ====================
    
    /**
     * Parses a primary expression starting with an identifier.
     * This handles identifiers, method calls, and field access.
     *
     * @return the expression node
     * @throws ParseException if parsing fails
     */
    private ExpressionNode parsePrimaryWithIdentifier()
        throws ParseException {
        Token idToken = peek();
        String name = idToken.getValue();
        consume();
        
        IdentifierNode identifier =
            new IdentifierNode(idToken.getLine(), idToken.getColumn(), idToken.getSource(), name);
        
        // Check for method call: id(...)
        skipNewlines();
        if (match(TokenType.LPAREN)) {
            // Parse method call with identifier as the target
            return parseMethodCallWithIdentifier(identifier);
        }
        
        // Check for path operations (field access, array access, etc.)
        return parsePath(identifier);
    }
    
    /**
     * Parses a method call starting with an identifier as the method name.
     * <p>
     * Method calls have the form: methodName(args) or for chained calls: target.methodName(args)
     *
     * @param identifier the identifier node (for simple calls) or null (for chained calls)
     * @return the MethodCallNode
     * @throws ParseException if parsing fails
     */
    private ExpressionNode parseMethodCallWithIdentifier(IdentifierNode identifier)
        throws ParseException {
        skipNewlines();
        Token lparen = expect(TokenType.LPAREN);
        skipNewlines();
        
        List<ExpressionNode> arguments = new ArrayList<>();
        if (!match(TokenType.RPAREN)) {
            arguments.add(parseExpression());
            skipNewlines();
            while (match(TokenType.COMMA)) {
                consume();
                skipNewlines();
                arguments.add(parseExpression());
                skipNewlines();
            }
        }
        
        expect(TokenType.RPAREN);
        
        // Create the method call node
        MethodCallNode methodCall = new MethodCallNode(lparen.getLine(), lparen.getColumn(), lparen.getSource(), null,
            identifier.getName(), arguments);
        
        // Check for method chaining: .method(...)
        return parsePath(methodCall);
    }
    
    /**
     * Parses a path part (method call, field access, array access, method reference).
     * This is called after parsing a primary expression.
     *
     * @param target the target expression
     * @return the expression node with path applied
     * @throws ParseException if parsing fails
     */
    private ExpressionNode parsePath(ExpressionNode target)
        throws ParseException {
        while (true) {
            skipNewlines();
            Token current = peek();
            
            if (current == null) {
                break;
            }
            
            switch (current.getType()) {
                case DOT: {
                    consume();
                    skipNewlines();
                    Token member = consumeFieldIdentifier();

                    // Check for method call
                    skipNewlines();
                    if (match(TokenType.LPAREN)) {
                        consume(); // Consume LPAREN
                        List<ExpressionNode> arguments = parseArgumentListBody();
                        target = new MethodCallNode(member.getLine(), member.getColumn(), member.getSource(), target,
                            member.getValue(), arguments);
                    }
                    else {
                        // Field access - create FieldAccessNode
                        target = new FieldAccessNode(member.getLine(), member.getColumn(), member.getSource(),
                            target, member.getValue(), false);
                    }
                    break;
                }

                case OPTIONAL_CHAINING: {
                    // ?. operator for optional chaining
                    Token opToken = consume();
                    skipNewlines();
                    Token member = consumeFieldIdentifier();

                    // Check for method call
                    skipNewlines();
                    if (match(TokenType.LPAREN)) {
                        consume(); // Consume LPAREN
                        List<ExpressionNode> arguments = parseArgumentListBody();
                        // TODO: Create OptionalMethodCallNode
                        // For now, use regular MethodCallNode
                        target = new MethodCallNode(member.getLine(), member.getColumn(), member.getSource(), target,
                            member.getValue(), arguments);
                    }
                    else {
                        // Optional field access - create optional FieldAccessNode
                        target = new FieldAccessNode(member.getLine(), member.getColumn(), member.getSource(),
                            target, member.getValue(), true);
                    }
                    break;
                }

                case SPREAD_CHAINING: {
                    // *. operator for spread chaining
                    Token opToken = consume();
                    skipNewlines();
                    Token member = consumeFieldIdentifier();

                    // Check for method call
                    skipNewlines();
                    if (match(TokenType.LPAREN)) {
                        consume(); // Consume LPAREN
                        List<ExpressionNode> arguments = parseArgumentListBody();
                        // TODO: Create SpreadMethodCallNode
                        // For now, use regular MethodCallNode
                        target = new MethodCallNode(member.getLine(), member.getColumn(), member.getSource(), target,
                            member.getValue(), arguments);
                    }
                    else {
                        // Spread field access - TODO: Implement SpreadGetFieldInstruction
                        // For now, create regular FieldAccessNode
                        target = new FieldAccessNode(member.getLine(), member.getColumn(), member.getSource(),
                            target, member.getValue(), false);
                    }
                    break;
                }

                case DCOLON: {
                    // :: operator for method reference
                    Token opToken = consume();
                    skipNewlines();
                    Token methodName = consumeFieldIdentifier();
                    // Method reference like "obj::method" creates a MethodReferenceNode
                    // This will be handled by GetMethodInstruction at runtime
                    target = new MethodReferenceNode(opToken.getLine(), opToken.getColumn(), opToken.getSource(),
                        target, methodName.getValue());
                    break;
                }
                
                case LBRACK:
                    target = parseArrayAccess(target);
                    break;
                
                default:
                    return target;
            }
        }
        
        return target;
    }
    
    /**
     * Parses the body of an argument list (without the leading LPAREN).
     * This is used when we've already consumed the LPAREN.
     *
     * @return the list of argument expressions
     * @throws ParseException if parsing fails
     */
    private List<ExpressionNode> parseArgumentListBody()
        throws ParseException {
        List<ExpressionNode> arguments = new ArrayList<>();
        
        // LPAREN already consumed
        skipNewlines();
        
        if (!match(TokenType.RPAREN)) {
            arguments.add(parseExpression());
            skipNewlines();
            while (match(TokenType.COMMA)) {
                consume();
                skipNewlines();
                arguments.add(parseExpression());
                skipNewlines();
            }
        }
        
        expect(TokenType.RPAREN);
        return arguments;
    }
    
    /**
     * Parses an argument list for method calls.
     *
     * @return the list of argument expressions
     * @throws ParseException if parsing fails
     */
    private List<ExpressionNode> parseArgumentList()
        throws ParseException {
        List<ExpressionNode> arguments = new ArrayList<>();
        
        expect(TokenType.LPAREN);
        return parseArgumentListBody();
    }
    
    // ==================== Constructor Call Parsing ====================
    
    /**
     * Parses a constructor call.
     * <p>
     * Constructor calls have the form: new TypeName(args)
     *
     * @return the ConstructorCallNode
     * @throws ParseException if parsing fails
     */
    private ExpressionNode parseConstructorCall()
        throws ParseException {
        Token newToken = expect(TokenType.NEW);
        skipNewlines();
        
        // Parse type name
        String typeName = parseQualifiedTypeName();
        skipNewlines();
        
        // Check for constructor call with arguments
        if (match(TokenType.LPAREN)) {
            List<ExpressionNode> arguments = parseArgumentList();
            return new ConstructorCallNode(newToken.getLine(), newToken.getColumn(), newToken.getSource(), typeName,
                arguments);
        }
        
        throw error("Expected '(' after type name in constructor call");
    }
    
    // ==================== Array Access Parsing ====================
    
    /**
     * Parses an array access expression.
     * <p>
     * Array access can be:
     * <ul>
     *   <li>array[index] - single index access</li>
     *   <li>array[start:end] - slice with start and end</li>
     *   <li>array[start:] - slice with start only</li>
     *   <li>array[:end] - slice with end only</li>
     *   <li>array[:] - empty slice (all elements)</li>
     * </ul>
     *
     * @param array the array expression
     * @return the ArrayAccessNode or ArraySliceNode
     * @throws ParseException if parsing fails
     */
    private ExpressionNode parseArrayAccess(ExpressionNode array)
        throws ParseException {
        Token lbracket = expect(TokenType.LBRACK);
        skipNewlines();
        
        // Check if this is a slice (has colon) or single index access
        // We need to look ahead to see if there's a COLON after the index expression
        // But we can't parse the full expression first because we need to detect the colon
        
        // Check for empty brackets [] or starting colon [:...]
        if (match(TokenType.RBRACK)) {
            // Empty brackets [] - treat as array access with no index (error?)
            consume();
            return new ArrayAccessNode(lbracket.getLine(), lbracket.getColumn(), lbracket.getSource(), array, null);
        }
        
        if (match(TokenType.COLON)) {
            // Slice with no start: [:end]
            consume();
            skipNewlines();
            ExpressionNode end = null;
            if (!match(TokenType.RBRACK)) {
                end = parseExpression();
            }
            skipNewlines();
            expect(TokenType.RBRACK);
            return new ArraySliceNode(lbracket.getLine(), lbracket.getColumn(), lbracket.getSource(), array, null, end);
        }
        
        // Parse the first expression (could be start of slice or single index)
        ExpressionNode firstExpr = parseExpression();
        skipNewlines();
        
        if (match(TokenType.COLON)) {
            // This is a slice: [start:end] or [start:]
            consume();
            skipNewlines();
            ExpressionNode end = null;
            if (!match(TokenType.RBRACK)) {
                end = parseExpression();
            }
            skipNewlines();
            expect(TokenType.RBRACK);
            return new ArraySliceNode(lbracket.getLine(), lbracket.getColumn(), lbracket.getSource(), array, firstExpr,
                end);
        }
        
        // Single index access: array[index]
        expect(TokenType.RBRACK);
        return new ArrayAccessNode(lbracket.getLine(), lbracket.getColumn(), lbracket.getSource(), array, firstExpr);
    }
    
    // ==================== Type Cast Parsing ====================
    
    /**
     * Parses a parenthesized expression or type cast.
     * <p>
     * Type casts have the form: (Type)expression
     *
     * @return the expression node
     * @throws ParseException if parsing fails
     */
    private ExpressionNode parseParenthesizedOrCast()
        throws ParseException {
        Token lparen = expect(TokenType.LPAREN);
        skipNewlines();
        
        // Check if this is a type cast
        // Look ahead to see if we have a type followed by )
        if (isTypeStart(peek())) {
            String typeName = parseQualifiedTypeName();
            skipNewlines();
            
            if (match(TokenType.RPAREN)) {
                consume();
                // This is a type cast
                ExpressionNode expression = parseUnary();
                return new CastNode(lparen.getLine(), lparen.getColumn(), lparen.getSource(), typeName, expression);
            }
        }
        
        // Not a cast, parse as regular parenthesized expression
        ExpressionNode expr = parseBinary(0);
        
        skipNewlines();
        expect(TokenType.RPAREN);
        
        return expr;
    }
    
    /**
     * Checks if the current token is the start of a type.
     *
     * @param token the token to check
     * @return true if it's the start of a type
     */
    private boolean isTypeStart(Token token) {
        if (token == null) {
            return false;
        }
        if (token.getType() == TokenType.ID) {
            return isTypeKeyword(token.getValue());
        }
        return token.getType() == TokenType.BYTE || token.getType() == TokenType.SHORT
            || token.getType() == TokenType.INT || token.getType() == TokenType.LONG
            || token.getType() == TokenType.FLOAT || token.getType() == TokenType.DOUBLE
            || token.getType() == TokenType.CHAR || token.getType() == TokenType.BOOLEAN;
    }
    
    // ==================== Type Literal Parsing ====================
    
    /**
     * Parses a type literal (primitive type reference).
     *
     * @return the TypeNode
     * @throws ParseException if parsing fails
     */
    private ExpressionNode parseTypeLiteral()
        throws ParseException {
        Token typeToken = consume();
        String typeName = typeToken.getValue();
        return new TypeNode(typeToken.getLine(), typeToken.getColumn(), typeToken.getSource(), typeName);
    }

    /**
     * Parses a selector interpolation expression (e.g., ${var} or #{var} or #[var]).
     * <p>
     * Selector expressions are used for string interpolation with custom selectors.
     * The content inside the selector is treated as a variable name (identifier), even if it's a number.
     * <p>
     * The SELECTOR_START token value contains the full selector expression including the
     * variable content (e.g., "${/a/aa}" or "${var}"). We extract the variable name from it.
     *
     * @return the IdentifierNode for the selector variable
     * @throws ParseException if parsing fails
     */
    private ExpressionNode parseSelectorStart()
        throws ParseException {
        Token selectorStart = expect(TokenType.SELECTOR_START);
        String selectorValue = selectorStart.getValue();

        if (selectorValue == null || selectorValue.isEmpty()) {
            throw error("Expected variable name after selector start");
        }

        int line = selectorStart.getLine();
        int column = selectorStart.getColumn();
        String source = selectorStart.getSource();

        // Extract the variable name from the selector expression
        // Selector value format: "${content}" or "#{content}" or "$[content]" or "#[content]"
        // We need to extract just the content part
        String variableName = extractSelectorVariableName(selectorValue);
        if (variableName == null || variableName.isEmpty()) {
            throw error("Expected variable name after selector start but found: " + selectorValue);
        }

        // Create an identifier node for the variable
        return new IdentifierNode(line, column, source, variableName);
    }

    /**
     * Extracts the variable name from a selector expression.
     * <p>
     * Examples:
     * <ul>
     *   <li>"${var}" -> "var"</li>
     *   <li>"${/a/aa}" -> "/a/aa"</li>
     *   <li>"#{0}" -> "0"</li>
     *   <li>"$[abc]" -> "abc"</li>
     * </ul>
     *
     * @param selectorValue the selector expression (e.g., "${var}")
     * @return the variable name, or null if extraction fails
     */
    private String extractSelectorVariableName(String selectorValue) {
        if (selectorValue == null || selectorValue.length() < 3) {
            return null;
        }

        // Find the selector start (${, #{, $[, #[)
        String start = null;
        String end = null;

        if (selectorValue.startsWith("${")) {
            start = "${";
            end = "}";
        } else if (selectorValue.startsWith("#{")) {
            start = "#{";
            end = "}";
        } else if (selectorValue.startsWith("$[")) {
            start = "$[";
            end = "]";
        } else if (selectorValue.startsWith("#[")) {
            start = "#[";
            end = "]";
        }

        if (start == null || !selectorValue.startsWith(start)) {
            return null;
        }

        // Check if the selector ends properly
        if (!selectorValue.endsWith(end)) {
            // Selector might be unterminated - return what we have
            return selectorValue.substring(start.length());
        }

        // Extract the variable name (between start and end)
        return selectorValue.substring(start.length(), selectorValue.length() - end.length());
    }

    // ==================== List Literal Parsing ====================
    
    /**
     * Parses a list literal.
     * <p>
     * List literals have the form: [item1, item2, ...]
     *
     * @return the expression node
     * @throws ParseException if parsing fails
     */
    private ExpressionNode parseListLiteral()
        throws ParseException {
        Token lbracket = expect(TokenType.LBRACK);
        skipNewlines();
        
        List<ExpressionNode> elements = new ArrayList<>();
        if (!match(TokenType.RBRACK)) {
            elements.add(parseExpression());
            skipNewlines();
            while (match(TokenType.COMMA)) {
                consume();
                skipNewlines();
                elements.add(parseExpression());
                skipNewlines();
            }
        }
        
        expect(TokenType.RBRACK);
        
        // TODO: Create ListLiteralNode
        // For now, return a placeholder
        return new LiteralNode(lbracket.getLine(), lbracket.getColumn(), lbracket.getSource(), elements);
    }
    
    // ==================== Map and Block Expression Parsing ====================
    
    /**
     * Parses a brace expression (map literal or block).
     * <p>
     * Brace expressions can be:
     * <ul>
     *   <li>Map literal: {key1: value1, key2: value2, ...}</li>
     *   <li>Block: {statements}</li>
     * </ul>
     *
     * @return the expression node
     * @throws ParseException if parsing fails
     */
    private ExpressionNode parseBraceExpression()
        throws ParseException {
        Token lbrace = expect(TokenType.LBRACE);
        skipNewlines();
        
        // Check if this is a map literal or block
        // Map literal if:
        // 1. Next token is COLON (empty map entry)
        // 2. Next token is ID, DOUBLE_QUOTE, or QUOTE_STRING_LITERAL followed by COLON
        // Otherwise it's a block
        Token next = peek();
        if (next != null) {
            if (next.getType() == TokenType.COLON) {
                return parseMapLiteral(lbrace);
            }
            Token nextNext = peek(1);
            if (nextNext != null && nextNext.getType() == TokenType.COLON) {
                if (next.getType() == TokenType.ID || next.getType() == TokenType.DOUBLE_QUOTE
                    || next.getType() == TokenType.QUOTE_STRING_LITERAL) {
                    return parseMapLiteral(lbrace);
                }
            }
        }
        
        return parseBlockAfterLBrace(lbrace);
    }
    
    /**
     * Parses a map literal.
     *
     * @param lbrace the left brace token (already consumed)
     * @return the expression node
     * @throws ParseException if parsing fails
     */
    private ExpressionNode parseMapLiteral(Token lbrace)
        throws ParseException {
        // LBRACE already consumed
        skipNewlines();
        
        // Check for empty map (single :)
        if (match(TokenType.COLON)) {
            skipNewlines();
            expect(TokenType.RBRACE);
            return new MapLiteralNode(lbrace.getLine(), lbrace.getColumn(), lbrace.getSource(),
                Collections.emptyList());
        }
        
        // Parse map entries
        List<MapEntryNode> entries = new ArrayList<>();
        while (!match(TokenType.RBRACE)) {
            skipNewlines();
            
            // Parse map key
            ExpressionNode key = parseMapKey();
            skipNewlines();
            
            // Expect colon
            expect(TokenType.COLON);
            skipNewlines();
            
            // Parse map value
            ExpressionNode value = parseMapValue();
            skipNewlines();
            
            entries.add(new MapEntryNode(key, value));
            
            // Check for comma separator
            if (match(TokenType.COMMA)) {
                consume(); // Consume the comma
                skipNewlines();
            }
            // If next is RBRACE, exit loop (will be consumed after loop)
            if (match(TokenType.RBRACE)) {
                break;
            }
        }
        
        // Consume the closing RBRACE
        expect(TokenType.RBRACE);
        
        return new MapLiteralNode(lbrace.getLine(), lbrace.getColumn(), lbrace.getSource(), entries);
    }
    
    /**
     * Parses a map key (can be ID, DOUBLE_QUOTE, or QUOTE_STRING_LITERAL).
     *
     * @return the expression node for the key
     * @throws ParseException if parsing fails
     */
    private ExpressionNode parseMapKey()
        throws ParseException {
        Token current = peek();
        if (current == null) {
            throw error("Expected map key but found end of input");
        }
        
        switch (current.getType()) {
            case ID:
                Token id = consume();
                return new IdentifierNode(id.getLine(), id.getColumn(), id.getSource(), id.getValue());
            case DOUBLE_QUOTE:
            case QUOTE_STRING_LITERAL:
                return parseLiteral();
            default:
                throw error("Expected map key (identifier or string) but found " + current.getType());
        }
    }
    
    /**
     * Parses a map value.
     * Special case: if the key is '@class', the value must be a string literal.
     *
     * @return the expression node for the value
     * @throws ParseException if parsing fails
     */
    private ExpressionNode parseMapValue()
        throws ParseException {
        // For now, just parse as expression
        // TODO: Handle the '@class' special case
        return parseExpression();
    }
    
    /**
     * Parses a block statement.
     * <p>
     * Block statements have the form: { statements }
     *
     * @return the BlockNode
     * @throws ParseException if parsing fails
     */
    private BlockNode parseBlock()
        throws ParseException {
        Token lbrace = expect(TokenType.LBRACE);
        return parseBlockAfterLBrace(lbrace);
    }
    
    /**
     * Parses a block statement after the LBRACE has been consumed.
     * <p>
     * Block statements have the form: { statements }
     *
     * @param lbrace the left brace token (already consumed)
     * @return the BlockNode
     * @throws ParseException if parsing fails
     */
    private BlockNode parseBlockAfterLBrace(Token lbrace)
        throws ParseException {
        skipNewlines();
        
        List<StatementNode> statements = new ArrayList<>();
        
        // Parse statements until we hit RBRACE
        while (!match(TokenType.RBRACE) && !isEOF()) {
            StatementNode stmt = parseStatement();
            if (stmt != null) {
                statements.add(stmt);
            }
            skipNewlines();
        }
        
        expect(TokenType.RBRACE);
        
        return new BlockNode(lbrace.getLine(), lbrace.getColumn(), lbrace.getSource(), statements);
    }
    
    /**
     * Parses a statement.
     * <p>
     * Statements include:
     * <ul>
     *   <li>if-else statements</li>
     *   <li>while loops</li>
     *   <li>for loops</li>
     *   <li>switch statements</li>
     *   <li>try-catch-finally</li>
     *   <li>return, break, continue</li>
     *   <li>throw</li>
     *   <li>variable declarations</li>
     *   <li>assignments</li>
     *   <li>expression statements</li>
     *   <li>blocks</li>
     * </ul>
     *
     * @return the statement node, or null if empty statement
     * @throws ParseException if parsing fails
     */
    public StatementNode parseStatement()
        throws ParseException {
        skipNewlines();
        
        Token current = peek();
        if (current == null) {
            return null;
        }
        
        switch (current.getType()) {
            case IF:
                return parseIf();
            case WHILE:
                return parseWhile();
            case FOR:
                return parseFor();
            case SWITCH:
                // SWITCH can be used as an identifier or as a switch statement
                // Check if followed by LPAREN to determine which
                if (peek(1) != null && peek(1).getType() == TokenType.LPAREN) {
                    return parseSwitch();
                }
                // Fall through to expression parsing
                return parseExpressionStatement();
            case FUNCTION:
                // FUNCTION can be used as an identifier or as a function definition
                // Check if followed by LPAREN to determine which
                if (peek(1) != null && peek(1).getType() == TokenType.LPAREN) {
                    return parseFunctionDefinition();
                }
                // Fall through to expression parsing
                return parseExpressionStatement();
            case MACRO:
                return parseMacroDefinition();
            case IMPORT:
                return parseImport();
            case CASE:
            case DEFAULT:
                // CASE and DEFAULT can be used as identifiers
                return parseExpressionStatement();
            case TRY:
                return parseTryCatch();
            case RETURN:
                return parseReturn();
            case BREAK:
                return parseBreak();
            case CONTINUE:
                return parseContinue();
            case THROW:
                return parseThrow();
            case LBRACE:
                return parseBraceExpression();
            case SEMI:
                consume(); // Empty statement
                return null;
            default:
                // Check for variable declaration with type
                // Variable declarations can start with:
                // 1. Primitive type keywords (int, long, etc.)
                // 2. Class type names followed by another ID (String name, List items, etc.)
                if (isTypeKeywordToken(current.getType())) {
                    return parseVariableDeclaration();
                }
                // Check for class-type variable declaration (ID followed by ID and optional = or ;)
                if (current.getType() == TokenType.ID && peek(1) != null && peek(1).getType() == TokenType.ID) {
                    // Could be "String name" or similar
                    // Look ahead to verify - if the first ID is a known type or followed by ID and (= or ;), it's a var decl
                    // We'll use the isTypeKeyword check on the value
                    if (isTypeKeyword(current.getValue())) {
                        return parseVariableDeclaration();
                    }
                }
                // Otherwise, it's an expression statement or assignment
                return parseExpressionStatement();
        }
    }

    /**
     * Parses an expression statement.
     * Expression statements have the form: expression [;]
     *
     * @return the expression node
     * @throws ParseException if parsing fails
     */
    private StatementNode parseExpressionStatement() throws ParseException {
        ExpressionNode expr = parseExpression();
        // Check for statement terminator
        skipNewlines();
        if (match(TokenType.SEMI)) {
            consume();
        }
        // Expressions can be statements (assignments, method calls, etc.)
        return expr;
    }
    
    // ==================== Control Flow Statement Parsing ====================
    
    /**
     * Parses an if-else statement.
     * <p>
     * If statements have the form:
     * if (condition) thenBody [else elseBody]
     * <p>
     * The thenBody and elseBody can be:
     * <ul>
     *   <li>A block statement: { statements }</li>
     *   <li>A single statement</li>
     *   <li>An expression (for expression form)</li>
     * </ul>
     *
     * @return the IfNode
     * @throws ParseException if parsing fails
     */
    private IfNode parseIf()
        throws ParseException {
        Token ifToken = expect(TokenType.IF);
        skipNewlines();
        
        expect(TokenType.LPAREN);
        skipNewlines();
        ExpressionNode condition = parseExpression();
        skipNewlines();
        expect(TokenType.RPAREN);
        skipNewlines();
        
        // Check for optional 'then' keyword
        if (match(TokenType.THEN)) {
            consume();
            skipNewlines();
        }
        
        // Parse then body - can be block or single statement/expression
        Node thenBody;
        if (match(TokenType.LBRACE)) {
            thenBody = parseBlock();
        }
        else if (match(TokenType.IF)) {
            // Nested if statement without braces
            thenBody = parseIf();
        }
        else {
            // Single expression or statement
            thenBody = parseExpression();
        }
        
        skipNewlines();
        
        // Parse optional else clause
        Node elseBody = null;
        if (match(TokenType.ELSE)) {
            consume();
            skipNewlines();
            
            if (match(TokenType.LBRACE)) {
                elseBody = parseBlock();
            }
            else if (match(TokenType.IF)) {
                // "else if" - nested if statement
                elseBody = parseIf();
            }
            else {
                elseBody = parseExpression();
            }
        }
        
        return new IfNode(ifToken.getLine(), ifToken.getColumn(), ifToken.getSource(), condition, thenBody, elseBody);
    }
    
    /**
     * Parses a while loop statement.
     * <p>
     * While loops have the form: while (condition) { statements }
     *
     * @return the WhileNode
     * @throws ParseException if parsing fails
     */
    private WhileNode parseWhile()
        throws ParseException {
        Token whileToken = expect(TokenType.WHILE);
        skipNewlines();
        
        expect(TokenType.LPAREN);
        skipNewlines();
        ExpressionNode condition = parseExpression();
        skipNewlines();
        expect(TokenType.RPAREN);
        skipNewlines();
        
        BlockNode body = parseBlock();
        
        return new WhileNode(whileToken.getLine(), whileToken.getColumn(), whileToken.getSource(), condition, body);
    }
    
    /**
     * Parses a for loop statement.
     * <p>
     * For loops come in three forms:
     * <ul>
     *   <li>Traditional: for (init; condition; update) { statements }</li>
     *   <li>For-each: for (type var : iterable) { statements }</li>
     *   <li>For-in: for (var : iterable) { statements }</li>
     * </ul>
     *
     * @return the ForNode
     * @throws ParseException if parsing fails
     */
    private ForNode parseFor()
        throws ParseException {
        Token forToken = expect(TokenType.FOR);
        skipNewlines();
        
        expect(TokenType.LPAREN);
        skipNewlines();
        
        // Determine which type of for loop this is
        // Look ahead to see if we have "declType? varId :" pattern (for-each)
        // or "init; condition; update;" pattern (traditional)
        
        Node init = null;
        ExpressionNode condition = null;
        ExpressionNode update = null;
        boolean isForEach = false;
        
        // Check if this is a for-each loop by looking for ':' before ';'
        int lookaheadPos = 0;
        boolean foundColon = false;
        boolean foundSemicolon = false;
        
        while (lookaheadPos < 50) { // Limit lookahead
            Token t = peek(lookaheadPos);
            if (t == null || t.getType() == TokenType.RPAREN) {
                break;
            }
            if (t.getType() == TokenType.COLON) {
                foundColon = true;
                break;
            }
            if (t.getType() == TokenType.SEMI) {
                foundSemicolon = true;
                break;
            }
            lookaheadPos++;
        }
        
        isForEach = foundColon && !foundSemicolon;
        
        if (isForEach) {
            // For-each loop: for (type? var : iterable)
            // Parse optional type
            String typeName = null;
            if (isTypeKeywordToken(peek().getType())) {
                typeName = consume().getValue();
            }
            else if (match(TokenType.ID) && peek(1) != null && peek(1).getType() == TokenType.ID) {
                // Could be "String name" or similar
                typeName = consume().getValue();
            }
            
            // Variable name
            String varName;
            if (match(TokenType.ID)) {
                varName = consume().getValue();
            }
            else {
                throw error("Expected variable name in for-each loop");
            }
            
            // Expect colon
            expect(TokenType.COLON);
            skipNewlines();
            
            // Parse iterable expression
            ExpressionNode iterable = parseExpression();
            skipNewlines();
            
            expect(TokenType.RPAREN);
            skipNewlines();
            
            BlockNode body = parseBlock();
            
            // For for-each, we store the variable info differently
            // Store as VariableDeclarationNode in init
            VariableDeclarationNode varDecl = new VariableDeclarationNode(forToken.getLine(), forToken.getColumn(),
                forToken.getSource(), typeName, varName, iterable);
            
            return new ForNode(forToken.getLine(), forToken.getColumn(), forToken.getSource(), varDecl, null, null,
                body);
        }
        else {
            // Traditional for loop: for (init; condition; update)
            // Parse init
            if (match(TokenType.SEMI)) {
                // No init
                consume();
            }
            else if (isTypeKeywordToken(peek().getType())
                || (peek().getType() == TokenType.ID && isTypeKeyword(peek().getValue()))) {
                // Variable declaration init
                // Note: parseVariableDeclaration will consume the semicolon
                init = parseVariableDeclaration();
            }
            else {
                // Expression init
                init = parseExpression();
                skipNewlines();
                expect(TokenType.SEMI);
            }
            
            skipNewlines();
            
            // Parse condition
            if (!match(TokenType.SEMI)) {
                condition = parseExpression();
            }
            skipNewlines();
            expect(TokenType.SEMI);
            
            skipNewlines();
            
            // Parse update
            if (!match(TokenType.RPAREN)) {
                update = parseExpression();
            }
            
            skipNewlines();
            expect(TokenType.RPAREN);
            skipNewlines();
            
            BlockNode body = parseBlock();
            
            return new ForNode(forToken.getLine(), forToken.getColumn(), forToken.getSource(), init, condition, update,
                body);
        }
    }
    
    /**
     * Parses a switch statement.
     * <p>
     * Switch statements have the form:
     * switch (value) {
     *   case expr1: statements
     *   case expr2: statements
     *   default: statements
     * }
     *
     * @return the SwitchNode
     * @throws ParseException if parsing fails
     */
    private SwitchNode parseSwitch()
        throws ParseException {
        Token switchToken = expect(TokenType.SWITCH);
        skipNewlines();
        
        expect(TokenType.LPAREN);
        skipNewlines();
        ExpressionNode value = parseExpression();
        skipNewlines();
        expect(TokenType.RPAREN);
        skipNewlines();
        
        expect(TokenType.LBRACE);
        skipNewlines();
        
        List<SwitchCaseNode> cases = new ArrayList<>();
        
        while (!match(TokenType.RBRACE) && !isEOF()) {
            skipNewlines();
            
            if (match(TokenType.CASE)) {
                consume();
                skipNewlines();
                
                // Parse case condition (can be expression list in new syntax)
                ExpressionNode caseExpr = parseExpression();
                skipNewlines();
                
                // Expect colon or arrow
                if (match(TokenType.COLON)) {
                    consume();
                }
                else if (match(TokenType.ARROW)) {
                    consume();
                }
                else {
                    throw error("Expected ':' or '->' after case expression");
                }
                skipNewlines();
                
                // Parse case statements
                List<StatementNode> caseStatements = new ArrayList<>();
                while (!match(TokenType.CASE, TokenType.DEFAULT, TokenType.RBRACE) && !isEOF()) {
                    StatementNode stmt = parseStatement();
                    if (stmt != null) {
                        caseStatements.add(stmt);
                    }
                    skipNewlines();
                }
                
                cases.add(new SwitchCaseNode(caseExpr, caseStatements));
                
            }
            else if (match(TokenType.DEFAULT)) {
                consume();
                skipNewlines();
                
                // Expect colon or arrow
                if (match(TokenType.COLON)) {
                    consume();
                }
                else if (match(TokenType.ARROW)) {
                    consume();
                }
                else {
                    throw error("Expected ':' or '->' after default");
                }
                skipNewlines();
                
                // Parse default statements
                List<StatementNode> defaultStatements = new ArrayList<>();
                while (!match(TokenType.CASE, TokenType.DEFAULT, TokenType.RBRACE) && !isEOF()) {
                    StatementNode stmt = parseStatement();
                    if (stmt != null) {
                        defaultStatements.add(stmt);
                    }
                    skipNewlines();
                }
                
                // Default case has null condition
                cases.add(new SwitchCaseNode(null, defaultStatements));
                
            }
            else {
                // Unexpected token, skip
                consume();
            }
        }
        
        expect(TokenType.RBRACE);
        
        return new SwitchNode(switchToken.getLine(), switchToken.getColumn(), switchToken.getSource(), value, cases);
    }
    
    /**
     * Parses a try-catch-finally statement.
     * <p>
     * Try-catch statements have the form:
     * try { statements } catch (Type1|Type2 var) { statements }* [finally { statements }]
     *
     * @return the TryCatchNode
     * @throws ParseException if parsing fails
     */
    private TryCatchNode parseTryCatch()
        throws ParseException {
        Token tryToken = expect(TokenType.TRY);
        skipNewlines();

        BlockNode tryBlock = parseBlock();
        skipNewlines();

        List<CatchClauseNode> catchClauses = new ArrayList<>();

        while (match(TokenType.CATCH)) {
            Token catchToken = consume();
            skipNewlines();

            expect(TokenType.LPAREN);
            skipNewlines();

            // Parse catch parameter types and variable name
            // Format: (type1 | type2 | ... varName) or (varName)
            List<String> exceptionTypes = new ArrayList<>();
            String varName;

            // Check if we have types or just a variable name
            // Use lookahead: if current is type keyword/ID and next is ID followed by | or RPAREN, we have types
            Token current = peek();
            Token next = peek(1);
            Token afterNext = peek(2);

            if (current != null && next != null &&
                (current.getType() == TokenType.ID || isTypeKeywordToken(current.getType())) &&
                next.getType() == TokenType.ID &&
                (afterNext == null || afterNext.getType() == TokenType.BIT_OR || afterNext.getType() == TokenType.RPAREN)) {
                // We have types: type1 | type2 | ... varName
                while (true) {
                    String typeName = parseQualifiedTypeName();
                    exceptionTypes.add(typeName);
                    skipNewlines();

                    // Check for union types (|)
                    if (match(TokenType.BIT_OR)) {
                        consume();
                        skipNewlines();
                    }
                    else {
                        break;
                    }
                }

                // Variable name
                varName = expect(TokenType.ID).getValue();
            }
            else {
                // Just a variable name without types
                Token varToken = expect(TokenType.ID);
                varName = varToken.getValue();
            }

            skipNewlines();

            expect(TokenType.RPAREN);
            skipNewlines();

            BlockNode catchBlock = parseBlock();
            skipNewlines();

            catchClauses.add(new CatchClauseNode(exceptionTypes, varName, catchBlock));
        }

        BlockNode finallyBlock = null;
        if (match(TokenType.FINALLY)) {
            consume();
            skipNewlines();
            finallyBlock = parseBlock();
        }

        return new TryCatchNode(tryToken.getLine(), tryToken.getColumn(), tryToken.getSource(), tryBlock, catchClauses,
            finallyBlock);
    }
    
    /**
     * Parses a return statement.
     * <p>
     * Return statements have the form: return [expression];
     *
     * @return the ReturnNode
     * @throws ParseException if parsing fails
     */
    private ReturnNode parseReturn()
        throws ParseException {
        Token returnToken = expect(TokenType.RETURN);
        skipNewlines();
        
        ExpressionNode value = null;
        
        // Check if there's a return value
        if (!match(TokenType.SEMI, TokenType.NEWLINE) && !isEOF()) {
            value = parseExpression();
        }
        
        skipNewlines();
        if (match(TokenType.SEMI)) {
            consume();
        }
        
        return new ReturnNode(returnToken.getLine(), returnToken.getColumn(), returnToken.getSource(), value);
    }
    
    /**
     * Parses a break statement.
     * <p>
     * Break statements have the form: break;
     *
     * @return the BreakNode
     * @throws ParseException if parsing fails
     */
    private BreakNode parseBreak()
        throws ParseException {
        Token breakToken = expect(TokenType.BREAK);
        skipNewlines();
        
        if (match(TokenType.SEMI)) {
            consume();
        }
        
        return new BreakNode(breakToken.getLine(), breakToken.getColumn(), breakToken.getSource());
    }
    
    /**
     * Parses a continue statement.
     * <p>
     * Continue statements have the form: continue;
     *
     * @return the ContinueNode
     * @throws ParseException if parsing fails
     */
    private ContinueNode parseContinue()
        throws ParseException {
        Token continueToken = expect(TokenType.CONTINUE);
        skipNewlines();
        
        if (match(TokenType.SEMI)) {
            consume();
        }
        
        return new ContinueNode(continueToken.getLine(), continueToken.getColumn(), continueToken.getSource());
    }
    
    /**
     * Parses a throw statement.
     * <p>
     * Throw statements have the form: throw expression;
     *
     * @return the ThrowNode
     * @throws ParseException if parsing fails
     */
    private ThrowNode parseThrow()
        throws ParseException {
        Token throwToken = expect(TokenType.THROW);
        skipNewlines();
        
        ExpressionNode exception = parseExpression();
        skipNewlines();
        
        if (match(TokenType.SEMI)) {
            consume();
        }
        
        return new ThrowNode(throwToken.getLine(), throwToken.getColumn(), throwToken.getSource(), exception);
    }
    
    /**
     * Parses a function definition statement.
     * <p>
     * Function definitions have the form: function functionName(params) { body }
     *
     * @return the FunctionDefinitionNode
     * @throws ParseException if parsing fails
     */
    private FunctionDefinitionNode parseFunctionDefinition()
        throws ParseException {
        Token functionToken = expect(TokenType.FUNCTION);
        skipNewlines();
        
        // Parse function name
        Token nameToken = expect(TokenType.ID);
        String functionName = nameToken.getValue();
        skipNewlines();
        
        // Parse parameters
        expect(TokenType.LPAREN);
        skipNewlines();
        
        List<ParameterNode> parameters = new ArrayList<>();
        if (!match(TokenType.RPAREN)) {
            parameters.add(parseLambdaParameter());
            skipNewlines();
            while (match(TokenType.COMMA)) {
                consume();
                skipNewlines();
                parameters.add(parseLambdaParameter());
                skipNewlines();
            }
        }
        
        expect(TokenType.RPAREN);
        skipNewlines();
        
        // Parse body
        BlockNode body = parseBlock();
        
        return new FunctionDefinitionNode(functionToken.getLine(), functionToken.getColumn(), functionToken.getSource(),
            functionName, parameters, body);
    }
    
    /**
     * Parses a macro definition statement.
     * <p>
     * Macro definitions have the form: macro macroName { body }
     *
     * @return the MacroDefinitionNode
     * @throws ParseException if parsing fails
     */
    private MacroDefinitionNode parseMacroDefinition()
        throws ParseException {
        Token macroToken = expect(TokenType.MACRO);
        skipNewlines();
        
        // Parse macro name
        Token nameToken = expect(TokenType.ID);
        String macroName = nameToken.getValue();
        skipNewlines();
        
        // Parse body
        BlockNode body = parseBlock();
        
        return new MacroDefinitionNode(macroToken.getLine(), macroToken.getColumn(), macroToken.getSource(), macroName,
            body);
    }

    /**
     * Parses an import statement.
     * <p>
     * Import statements have the form:
     * - import com.example.ClassName;
     * - import com.example.*;
     * - import com.example.ClassName.*; (equivalent to above)
     *
     * @return the ImportNode
     * @throws ParseException if parsing fails
     */
    private ImportNode parseImport()
        throws ParseException {
        Token importToken = expect(TokenType.IMPORT);
        skipNewlines();

        // Build the import path
        StringBuilder importPath = new StringBuilder();

        // Get first identifier (must be present)
        Token firstId = expect(TokenType.ID);
        importPath.append(firstId.getValue());
        skipNewlines();

        // Continue with .id or .*
        while (match(TokenType.DOT)) {
            consume(); // Consume DOT
            skipNewlines();

            if (match(TokenType.MUL)) {
                consume(); // Consume MUL (*)
                // Wildcard import
                expect(TokenType.SEMI);
                return new ImportNode(importToken.getLine(), importToken.getColumn(), importToken.getSource(),
                    importPath.toString(), true);
            }

            // Regular identifier
            Token id = expect(TokenType.ID);
            importPath.append('.').append(id.getValue());
            skipNewlines();
        }

        // Check for .* or .* at end (DOTMUL token handles this)
        if (match(TokenType.DOTMUL)) {
            consume();
            expect(TokenType.SEMI);
            return new ImportNode(importToken.getLine(), importToken.getColumn(), importToken.getSource(),
                importPath.toString(), true);
        }

        // Regular class import
        expect(TokenType.SEMI);
        return new ImportNode(importToken.getLine(), importToken.getColumn(), importToken.getSource(),
            importPath.toString(), false);
    }

    /**
     * Parses a variable declaration.
     * <p>
     * Variable declarations have the form: Type varName [= expression];
     *
     * @return the VariableDeclarationNode
     * @throws ParseException if parsing fails
     */
    private VariableDeclarationNode parseVariableDeclaration()
        throws ParseException {
        Token typeToken = peek();
        
        // Parse type name
        String typeName = parseQualifiedTypeName();
        skipNewlines();
        
        // Parse variable name
        String varName = expect(TokenType.ID).getValue();
        skipNewlines();
        
        // Parse optional initializer
        ExpressionNode initializer = null;
        if (match(TokenType.EQ)) {
            consume();
            skipNewlines();
            initializer = parseExpression();
        }
        
        skipNewlines();
        if (match(TokenType.SEMI)) {
            consume();
        }
        
        return new VariableDeclarationNode(typeToken.getLine(), typeToken.getColumn(), typeToken.getSource(), typeName,
            varName, initializer);
    }
    
    // ==================== Unary Operator Parsing ====================
    
    /**
     * Checks if the current token is a prefix unary operator.
     *
     * @return true if current token is a prefix unary operator
     */
    private boolean isPrefixUnaryOperator() {
        Token current = peek();
        if (current == null) {
            return false;
        }
        // Check for specific token types that are prefix operators
        if (match(TokenType.BANG, TokenType.TILDE, TokenType.INC, TokenType.DEC)) {
            return true;
        }
        // Check for + and - which need special handling
        if (current.getType() == TokenType.ADD || current.getType() == TokenType.SUB) {
            return true;
        }
        // Check with operator manager for custom operators
        String value = current.getValue();
        if (value != null) {
            return operatorManager.isOpType(value, ParserOperatorManager.OpType.PREFIX);
        }
        return false;
    }
    
    /**
     * Checks if the current token is a suffix unary operator.
     *
     * @return true if current token is a suffix unary operator
     */
    private boolean isSuffixUnaryOperator() {
        Token current = peek();
        if (current == null) {
            return false;
        }
        // Check for specific token types that are suffix operators
        if (match(TokenType.INC, TokenType.DEC)) {
            return true;
        }
        // Check with operator manager for custom operators
        String value = current.getValue();
        if (value != null) {
            return operatorManager.isOpType(value, ParserOperatorManager.OpType.SUFFIX);
        }
        return false;
    }
    
    /**
     * Parses prefix unary operators.
     * Handles: !, -, +, ~, ++, --
     *
     * @return the UnaryOpNode
     * @throws ParseException if parsing fails
     */
    private ExpressionNode parsePrefixUnary()
        throws ParseException {
        Token opToken = consume();
        String operator = opToken.getValue();
        ExpressionNode operand = parseUnary();
        
        return new UnaryOpNode(opToken.getLine(), opToken.getColumn(), opToken.getSource(), operator, operand, true);
    }
    
    /**
     * Parses suffix unary operators.
     * Handles: ++, --
     *
     * @param operand the operand expression
     * @return the UnaryOpNode
     * @throws ParseException if parsing fails
     */
    private ExpressionNode parseSuffixUnary(ExpressionNode operand)
        throws ParseException {
        Token opToken = consume();
        String operator = opToken.getValue();
        
        return new UnaryOpNode(opToken.getLine(), opToken.getColumn(), opToken.getSource(), operator, operand, false);
    }
    
    /**
     * Parses a unary expression (prefix or suffix).
     * Unary expressions include prefix operators (!, -, +, ~, ++, --)
     * and suffix operators (++, --).
     *
     * @return the expression node
     * @throws ParseException if parsing fails
     */
    public ExpressionNode parseUnary()
        throws ParseException {
        // Check for prefix unary operators
        if (isPrefixUnaryOperator()) {
            return parsePrefixUnary();
        }
        
        // Parse primary expression
        ExpressionNode expr = parsePrimary();
        
        // Check for suffix unary operators
        while (isSuffixUnaryOperator()) {
            expr = parseSuffixUnary(expr);
        }
        
        // Check for path operations (method calls, array access, field access)
        expr = parsePath(expr);
        
        return expr;
    }
    
    // ==================== Binary Operator Parsing ====================
    
    /**
     * Checks if the current token is a binary operator.
     *
     * @return true if current token is a binary operator
     */
    private boolean isBinaryOperator() {
        Token current = peek();
        if (current == null) {
            return false;
        }
        // Check with operator manager for binary operators
        String value = current.getValue();
        if (value != null) {
            return operatorManager.isOpType(value, ParserOperatorManager.OpType.MIDDLE);
        }
        return false;
    }
    
    /**
     * Gets the precedence of a binary operator.
     *
     * @param operator the operator lexeme
     * @return the precedence value, or -1 if not a binary operator
     */
    private int getBinaryPrecedence(String operator) {
        Integer precedence = operatorManager.precedence(operator);
        return precedence != null ? precedence : -1;
    }
    
    /**
     * Parses a binary expression with proper precedence.
     * Uses recursive descent with precedence climbing.
     *
     * @param minPrecedence the minimum precedence for this level
     * @return the expression node
     * @throws ParseException if parsing fails
     */
    private ExpressionNode parseBinary(int minPrecedence)
        throws ParseException {
        // Parse left operand (unary expression)
        ExpressionNode left = parseUnary();
        
        // While we have a binary operator with sufficient precedence
        while (true) {
            skipNewlines();
            Token current = peek();
            if (current == null) {
                break;
            }
            
            // Check if current token is a binary operator
            String op = current.getValue();
            if (!isBinaryOperator()) {
                break;
            }
            
            int precedence = getBinaryPrecedence(op);
            if (precedence < minPrecedence) {
                break;
            }
            
            // Consume the operator
            consume();
            skipNewlines();
            
            // Parse right operand with higher precedence
            ExpressionNode right = parseBinary(precedence + 1);
            
            // Create binary operation node
            left = new BinaryOpNode(current.getLine(), current.getColumn(), current.getSource(), left, op, right);
        }
        
        return left;
    }
    
    /**
     * Parses a ternary expression.
     * <p>
     * Ternary expressions have the form: condition ? thenExpr : elseExpr
     * The colon and elseExpr are required.
     *
     * @return the expression node
     * @throws ParseException if parsing fails
     */
    public ExpressionNode parseTernary()
        throws ParseException {
        ExpressionNode condition = parseBinary(0);
        
        // Check if we have a ternary operator
        skipNewlines();
        if (!match(TokenType.QUESTION)) {
            return condition;
        }
        
        // Consume the question mark
        Token questionToken = consume();
        skipNewlines();
        
        // Parse the then expression (ternaryExpr uses baseExpr[0], which is lowest precedence)
        ExpressionNode thenExpr = parseBinary(0);
        
        // Expect and consume colon
        skipNewlines();
        if (!match(TokenType.COLON)) {
            throw error("Expected ':' in ternary expression");
        }
        Token colonToken = consume();
        skipNewlines();
        
        // Parse the else expression (full expression to allow nested ternary)
        ExpressionNode elseExpr = parseExpression();
        
        return new TernaryNode(questionToken.getLine(), questionToken.getColumn(), questionToken.getSource(), condition,
            thenExpr, elseExpr);
    }
    
    /**
     * Parses a binary expression.
     * This is the main entry point for expression parsing.
     *
     * @return the expression node
     * @throws ParseException if parsing fails
     */
    public ExpressionNode parseExpression()
        throws ParseException {
        return parseTernary();
    }
    
    /**
     * Returns the current token without consuming it.
     *
     * @return the current token, or null if at end of stream
     */
    public Token peek() {
        return peek(0);
    }
    
    /**
     * Returns the token at offset from current position without consuming it.
     *
     * @param offset the offset from current position (0 = current, 1 = next, etc.)
     * @return the token at offset, or null if at end of stream
     */
    public Token peek(int offset) {
        int pos = position + offset;
        if (pos >= 0 && pos < tokens.size()) {
            return tokens.get(pos);
        }
        return null;
    }
    
    /**
     * Consumes and returns the current token.
     *
     * @return the consumed token
     * @throws ParseException if at end of stream
     */
    public Token consume()
        throws ParseException {
        if (position >= tokens.size()) {
            throw error("Unexpected end of input");
        }
        lastToken = tokens.get(position++);
        return lastToken;
    }
    
    /**
     * Checks if the current token is of the expected type and consumes it.
     *
     * @param expected the expected token type
     * @return the consumed token
     * @throws ParseException if the current token is not of the expected type
     */
    public Token expect(TokenType expected)
        throws ParseException {
        Token current = peek();
        if (current == null) {
            throw error("Unexpected end of input, expected " + expected);
        }
        if (current.getType() != expected) {
            throw error("Expected " + expected + " but found " + current.getType());
        }
        return consume();
    }
    
    /**
     * Checks if the current token matches any of the given types.
     *
     * @param types the token types to check
     * @return true if the current token matches any of the types
     */
    public boolean match(TokenType... types) {
        Token current = peek();
        if (current == null) {
            return false;
        }
        for (TokenType type : types) {
            if (current.getType() == type) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Checks if the current token is of the specified type.
     *
     * @param type the token type to check
     * @return true if the current token is of the specified type
     */
    public boolean is(TokenType type) {
        Token current = peek();
        return current != null && current.getType() == type;
    }

    /**
     * Checks if the current token can be used as an identifier (varId).
     * In QLExpress, certain keywords can be used as identifiers: FUNCTION, CASE, DEFAULT, SWITCH.
     *
     * @return true if the current token can be used as an identifier
     */
    private boolean isIdentifierToken() {
        Token current = peek();
        if (current == null) {
            return false;
        }
        TokenType type = current.getType();
        return type == TokenType.ID
            || type == TokenType.FUNCTION
            || type == TokenType.CASE
            || type == TokenType.DEFAULT
            || type == TokenType.SWITCH;
    }

    /**
     * Checks if the current token can be used as a field identifier (fieldId).
     * In QLExpress, field identifiers include: varId (ID, FUNCTION, CASE, DEFAULT, SWITCH),
     * CLASS, and QUOTE_STRING_LITERAL.
     *
     * @return true if the current token can be used as a field identifier
     */
    private boolean isFieldIdentifierToken() {
        Token current = peek();
        if (current == null) {
            return false;
        }
        TokenType type = current.getType();
        return type == TokenType.ID
            || type == TokenType.FUNCTION
            || type == TokenType.CASE
            || type == TokenType.DEFAULT
            || type == TokenType.SWITCH
            || type == TokenType.CLASS
            || type == TokenType.QUOTE_STRING_LITERAL;
    }

    /**
     * Consumes a field identifier token.
     * Field identifiers can be: ID, FUNCTION, CASE, DEFAULT, SWITCH, CLASS, QUOTE_STRING_LITERAL.
     *
     * @return the field identifier token
     * @throws ParseException if the current token is not a field identifier
     */
    private Token consumeFieldIdentifier() throws ParseException {
        Token current = peek();
        if (current == null || !isFieldIdentifierToken()) {
            throw error("Expected field identifier but found " + (current != null ? current.getType() : "EOF"));
        }
        return consume();
    }

    /**
     * Returns the last consumed token.
     *
     * @return the last consumed token, or null if no token has been consumed
     */
    public Token getPreviousToken() {
        return lastToken;
    }
    
    /**
     * Creates a parse exception at the current position.
     *
     * @param message the error message
     * @return the parse exception
     */
    public ParseException error(String message) {
        Token current = peek();
        if (current != null) {
            return new ParseException(message, current.getLine(), current.getColumn(), current.getSource());
        }
        return new ParseException(message, -1, -1, null);
    }
    
    /**
     * Returns the current position in the token stream.
     *
     * @return the current position
     */
    public int getPosition() {
        return position;
    }
    
    /**
     * Checks if we are at the end of the token stream.
     *
     * @return true if at end of stream
     */
    public boolean isEOF() {
        return position >= tokens.size() || (peek() != null && peek().getType() == TokenType.EOF);
    }
    
    /**
     * Parse exception with source location information.
     */
    public static class ParseException extends Exception {
        private final int line;
        
        private final int column;
        
        private final String source;
        
        public ParseException(String message, int line, int column, String source) {
            super(message);
            this.line = line;
            this.column = column;
            this.source = source;
        }
        
        public int getLine() {
            return line;
        }
        
        public int getColumn() {
            return column;
        }
        
        public String getSource() {
            return source;
        }
        
        public String getLocationString() {
            if (source != null && !source.isEmpty()) {
                return source + ":" + line + ":" + column;
            }
            return line + ":" + column;
        }
        
        @Override
        public String toString() {
            return getMessage() + " at " + getLocationString();
        }
    }
}
