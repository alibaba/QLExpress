package com.alibaba.qlexpress4.aparser;

import com.alibaba.qlexpress4.runtime.operator.OperatorManager;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenFactory;
import org.antlr.v4.runtime.TokenSource;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.WritableToken;
import org.antlr.v4.runtime.atn.DecisionInfo;
import org.antlr.v4.runtime.atn.DecisionState;
import org.antlr.v4.runtime.atn.PredictionMode;
import org.antlr.v4.runtime.misc.Interval;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Author: DQinYuan
 */
public class SyntaxTreeFactory {

    private static final AtomicBoolean IS_WARM_UP = new AtomicBoolean();

    public static void warmUp() {
        if (IS_WARM_UP.compareAndSet(false, true)) {
            // warm up
            warmUpExpress("1+1");
            warmUpExpress("a = b + c");
        }
    }

    private static void warmUpExpress(String script) {
        buildTree(script, new OperatorManager(), false, false, s -> {}, InterpolationMode.SCRIPT);
    }


    public static QLParser.ProgramContext buildTree(String script, ParserOperatorManager operatorManager,
                                                           boolean printTree, boolean profile, Consumer<String> printer,
                                                    InterpolationMode interpolationMode) {
        QLexer lexer = new QLexer(CharStreams.fromString(script), interpolationMode);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        QLParser qlGrammarParser = new QLParser(new AliasTokenStream(tokens, operatorManager),
                operatorManager, interpolationMode);
        qlGrammarParser.setErrorHandler(new QLErrorStrategy(script));
        qlGrammarParser.getInterpreter().setPredictionMode(PredictionMode.SLL);
        if (profile) {
            qlGrammarParser.setProfile(true);
        }
        QLParser.ProgramContext programContext = qlGrammarParser.program();
        if (printTree) {
            printer.accept(tokens.getTokens().stream()
                    .map(Token::getText).collect(Collectors.joining(" | ")));
            printer.accept(programContext.toStringTree(qlGrammarParser));
        }
        if (profile) {
            profileParser(qlGrammarParser);
        }
        return programContext;
    }

    private static void profileParser(Parser parser) {
        System.out.printf("%-" + 35 + "s", "rule");
        System.out.printf("%-" + 15 + "s", "time");
        System.out.printf("%-" + 15 + "s", "invocations");
        System.out.printf("%-" + 15 + "s", "lookahead");
        System.out.printf("%-" + 15 + "s", "lookahead(max)");
        System.out.printf("%-" + 15 + "s", "ambiguities");
        System.out.printf("%-" + 15 + "s", "errors");
        System.out.printf("%-" + 15 + "s", "fallBack");
        System.out.println();
        for (DecisionInfo decisionInfo : parser.getParseInfo().getDecisionInfo()) {
            DecisionState ds = parser.getATN().getDecisionState(decisionInfo.decision);
            String rule = parser.getRuleNames()[ds.ruleIndex];
            if (decisionInfo.timeInPrediction > 0) {
                System.out.printf("%-" + 35 + "s", rule);
                System.out.printf("%-" + 15 + "s", decisionInfo.timeInPrediction);
                System.out.printf("%-" + 15 + "s", decisionInfo.invocations);
                System.out.printf("%-" + 15 + "s", decisionInfo.SLL_TotalLook);
                System.out.printf("%-" + 15 + "s", decisionInfo.SLL_MaxLook);
                System.out.printf("%-" + 15 + "s", decisionInfo.ambiguities.size());
                System.out.printf("%-" + 15 + "s", decisionInfo.errors);
                System.out.printf("%-" + 15 + "s", decisionInfo.LL_Fallback);
                System.out.println();
            }
        }
    }

    private static Token preHandleToken(Token originToken, ParserOperatorManager operatorManager) {
        if (originToken instanceof WritableToken && originToken.getType() == QLexer.ID) {
            Integer aliasId = operatorManager.getAlias(originToken.getText());
            if (aliasId != null && originToken.getType() != aliasId) {
                ((WritableToken) originToken).setType(aliasId);
            }
        }
        return originToken;
    }

    private static class AliasTokenSource implements TokenSource {

        private final TokenSource tokenSource;

        private final ParserOperatorManager operatorManager;

        private AliasTokenSource(TokenSource tokenSource, ParserOperatorManager operatorManager) {
            this.tokenSource = tokenSource;
            this.operatorManager = operatorManager;
        }

        @Override
        public Token nextToken() {
            return preHandleToken(tokenSource.nextToken(), operatorManager);
        }

        @Override
        public int getLine() {
            return tokenSource.getLine();
        }

        @Override
        public int getCharPositionInLine() {
            return tokenSource.getCharPositionInLine();
        }

        @Override
        public CharStream getInputStream() {
            return tokenSource.getInputStream();
        }

        @Override
        public String getSourceName() {
            return tokenSource.getSourceName();
        }

        @Override
        public void setTokenFactory(TokenFactory<?> factory) {
            tokenSource.setTokenFactory(factory);
        }

        @Override
        public TokenFactory<?> getTokenFactory() {
            return tokenSource.getTokenFactory();
        }
    }

    private static class AliasTokenStream implements TokenStream {

        private final TokenStream stream;
        private final ParserOperatorManager operatorManager;

        private AliasTokenStream(TokenStream stream, ParserOperatorManager operatorManager) {
            this.stream = stream;
            this.operatorManager = operatorManager;
        }

        @Override
        public Token LT(int k) {
            return preHandleToken(stream.LT(k), operatorManager);
        }

        @Override
        public Token get(int index) {
            return preHandleToken(stream.get(index), operatorManager);
        }

        @Override
        public TokenSource getTokenSource() {
            return new AliasTokenSource(stream.getTokenSource(), operatorManager);
        }

        @Override
        public String getText(Interval interval) {
            return stream.getText(interval);
        }

        @Override
        public String getText() {
            return stream.getText();
        }

        @Override
        public String getText(RuleContext ctx) {
            return stream.getText(ctx);
        }

        @Override
        public String getText(Token start, Token stop) {
            return stream.getText(start, stop);
        }

        @Override
        public void consume() {
            stream.consume();
        }

        @Override
        public int LA(int i) {
            return preHandleToken(LT(i), operatorManager).getType();
        }

        @Override
        public int mark() {
            return stream.mark();
        }

        @Override
        public void release(int marker) {
            stream.release(marker);
        }

        @Override
        public int index() {
            return stream.index();
        }

        @Override
        public void seek(int index) {
            stream.seek(index);
        }

        @Override
        public int size() {
            return stream.size();
        }

        @Override
        public String getSourceName() {
            return stream.getSourceName();
        }
    }
}
