package com.alibaba.qlexpress4.parser;

import com.alibaba.qlexpress4.common.InterpolationMode;
import com.alibaba.qlexpress4.parser.lexer.QLexpressLexer;
import com.alibaba.qlexpress4.parser.parser.QLexpressParser;
import com.alibaba.qlexpress4.parser.ast.ProgramNode;
import com.alibaba.qlexpress4.runtime.operator.OperatorManager;

import java.util.List;
import java.util.function.Consumer;

import static com.alibaba.qlexpress4.parser.parser.QLexpressParser.ParseException;

/**
 * Factory for creating syntax trees (AST) from QLExpress script source code.
 * <p>
 * This factory replaces the ANTLR-based SyntaxTreeFactory with a hand-written
 * recursive descent parser implementation. It provides the same interface
 * for backward compatibility.
 * <p>
 * The parsing process consists of two stages:
 * <ol>
 *   <li>Lexical analysis: Source code → Token stream (via QLexpressLexer)</li>
 *   <li>Syntax analysis: Token stream → AST/ProgramNode (via QLexpressParser)</li>
 * </ol>
 *
 * @author QLExpress Team
 */
public class SyntaxTreeFactory {
    
    private SyntaxTreeFactory() {
        // Static factory class
    }
    
    /**
     * Builds an AST (ProgramNode) from the given script.
     * <p>
     * This method provides the same interface as the original ANTLR-based
     * SyntaxTreeFactory but returns a new ProgramNode instead of ANTLR's
     * ProgramContext.
     *
     * @param script            The QLExpress script to parse
     * @param operatorManager   The operator manager for resolving custom operators
     * @param printTree         If true, prints the token stream and AST to the printer
     * @param profile           If true, enables parser profiling (currently not supported)
     * @param printer           Consumer for receiving debug output
     * @param interpolationMode The interpolation mode for string interpolation
     * @param selectorStart     Custom selector start token (e.g., "${")
     * @param selectorEnd       Custom selector end token (e.g., "}")
     * @param strictNewlines    If true, newline tokens are significant; if false, they are skipped
     * @return The parsed AST as a ProgramNode
     * @throws com.alibaba.qlexpress4.parser.parser.QLexpressParser.ParseException if parsing fails
     */
    public static ProgramNode buildTree(String script, OperatorManager operatorManager, boolean printTree,
        boolean profile, Consumer<String> printer, InterpolationMode interpolationMode, String selectorStart,
        String selectorEnd, boolean strictNewlines)
        throws ParseException {
        
        // Create lexer with the specified configuration
        // Note: QLexpressLexer constructor signature is:
        // (input, source, interpolationMode, strictNewLines, selectorStart, selectorEnd)
        QLexpressLexer lexer = new QLexpressLexer(script, null, // source identifier (can be null)
            interpolationMode, strictNewlines, selectorStart, selectorEnd);
        
        // Tokenize the input
        List<com.alibaba.qlexpress4.parser.token.Token> tokens = lexer.tokenize();
        
        if (printTree) {
            // Print token stream for debugging
            printer.accept(tokens.stream()
                .map(t -> t.getType().name() + "(" + t.getValue() + ")")
                .reduce((a, b) -> a + " | " + b)
                .orElse(""));
        }
        
        // Create parser with the operator manager and interpolation mode
        QLexpressParser parser = new QLexpressParser(tokens, operatorManager, interpolationMode);

        // Parse the program
        ProgramNode programNode = parser.parseProgram();
        
        if (printTree) {
            // Print AST for debugging
            printer.accept(programNode.toString());
        }
        
        if (profile) {
            // Note: Profiling is not currently supported for the new parser
            // The original ANTLR-based implementation provided detailed profiling
            // information about parse decisions, lookahead, etc.
            printer.accept("Parser profiling is not yet supported for the new parser implementation.");
        }
        
        return programNode;
    }
    
    /**
     * Builds an AST (ProgramNode) from the given script with default settings.
     * <p>
     * Uses default values for interpolation mode, selector tokens, and newline handling.
     *
     * @param script          The QLExpress script to parse
     * @param operatorManager The operator manager for resolving custom operators
     * @return The parsed AST as a ProgramNode
     * @throws com.alibaba.qlexpress4.parser.parser.QLexpressParser.ParseException if parsing fails
     */
    public static ProgramNode buildTree(String script, OperatorManager operatorManager)
        throws ParseException {
        return buildTree(script, operatorManager, false, false, s -> {
        }, InterpolationMode.SCRIPT, "${", "}", false);
    }
    
    /**
     * Builds an AST (ProgramNode) from the given script with debug output enabled.
     *
     * @param script          The QLExpress script to parse
     * @param operatorManager The operator manager for resolving custom operators
     * @param printer         Consumer for receiving debug output
     * @return The parsed AST as a ProgramNode
     * @throws com.alibaba.qlexpress4.parser.parser.QLexpressParser.ParseException if parsing fails
     */
    public static ProgramNode buildTreeWithDebug(String script, OperatorManager operatorManager,
        Consumer<String> printer)
        throws ParseException {
        return buildTree(script, operatorManager, true, false, printer, InterpolationMode.SCRIPT, "${", "}", false);
    }
}
