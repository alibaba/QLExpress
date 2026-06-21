package com.alibaba.qlexpress4.aparser;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class SyntaxTreeFactory {
    
    public static QLParser.ProgramContext buildTree(String script, ParserOperatorManager operatorManager,
        boolean printTree, Consumer<String> printer, InterpolationMode interpolationMode, String selectorStart,
        String selectorEnd, boolean strictNewLines) {
        List<Token> tokens =
            QLexer.tokenize(script, operatorManager, interpolationMode, selectorStart, selectorEnd, strictNewLines);
        QLParser parser = new QLParser(script, tokens, operatorManager, strictNewLines);
        QLParser.ProgramContext programContext = parser.program();
        if (printTree) {
            printer.accept(tokens.stream().map(Token::getText).collect(Collectors.joining(" | ")));
            printer.accept(programContext.toStringTree());
        }
        return programContext;
    }
    
}
