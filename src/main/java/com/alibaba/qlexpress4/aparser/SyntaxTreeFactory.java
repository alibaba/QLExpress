package com.alibaba.qlexpress4.aparser;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;

import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Author: DQinYuan
 */
public class SyntaxTreeFactory {

    public static QLGrammarParser.ProgramContext buildTree(String script, ParserOperatorManager operatorManager,
                                                           boolean printTree, Consumer<String> printer) {
        QLGrammarLexer lexer = new QLGrammarLexer(CharStreams.fromString(script));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        QLGrammarParser qlGrammarParser = new QLGrammarParser(tokens, operatorManager);
        qlGrammarParser.setErrorHandler(new QLErrorStrategy(script));
        QLGrammarParser.ProgramContext programContext = qlGrammarParser.program();
        if (printTree) {
            printer.accept(tokens.getTokens().stream()
                    .map(Token::getText).collect(Collectors.joining(" | ")));
            printer.accept(programContext.toStringTree(qlGrammarParser));
        }
        return programContext;
    }

}
