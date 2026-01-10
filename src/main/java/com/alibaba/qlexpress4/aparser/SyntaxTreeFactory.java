package com.alibaba.qlexpress4.aparser;

import com.alibaba.qlexpress4.runtime.operator.OperatorManager;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.WritableToken;
import org.antlr.v4.runtime.atn.DecisionState;
import org.antlr.v4.runtime.atn.PredictionMode;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Author: DQinYuan
 */
public class SyntaxTreeFactory {
    
    private static final AtomicBoolean IS_WARM_UP = new AtomicBoolean();
    
    private static void warmUpExpress(String script) {
        buildTree(script, new OperatorManager(), false, false, s -> {
        }, InterpolationMode.SCRIPT, "${", "}", true);
    }
    
    public static QLParser.ProgramContext buildTree(String script, ParserOperatorManager operatorManager,
        boolean printTree, boolean profile, Consumer<String> printer, InterpolationMode interpolationMode,
        String selectorStart, String selectorEnd, boolean strictNewLines) {
        QLexer lexer = new QLExtendLexer(CharStreams.fromString(script), script, interpolationMode, selectorStart,
            selectorEnd, strictNewLines);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        QLParser qlGrammarParser = new QLExtendParser(new AliasTokenStream(tokens, operatorManager), operatorManager,
            interpolationMode, strictNewLines);
        if (!printTree) {
            qlGrammarParser.removeErrorListeners();
        }
        qlGrammarParser.addErrorListener(new QLErrorListener(script));
        qlGrammarParser.setErrorHandler(new QLErrorStrategy());
        qlGrammarParser.getInterpreter().setPredictionMode(PredictionMode.SLL);
        if (profile) {
            qlGrammarParser.setProfile(true);
        }
        QLParser.ProgramContext programContext = qlGrammarParser.program();
        if (printTree) {
            printer.accept(tokens.getTokens().stream().map(Token::getText).collect(Collectors.joining(" | ")));
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
        
        // 按照 timeInPrediction 从大到小排序
        Arrays.stream(parser.getParseInfo().getDecisionInfo())
            .filter(decisionInfo -> decisionInfo.timeInPrediction > 0)
            .sorted((d1, d2) -> Long.compare(d2.timeInPrediction, d1.timeInPrediction))
            .forEach(decisionInfo -> {
                DecisionState ds = parser.getATN().getDecisionState(decisionInfo.decision);
                String rule = parser.getRuleNames()[ds.ruleIndex];
                System.out.printf("%-" + 35 + "s", rule);
                System.out.printf("%-" + 15 + "s", decisionInfo.timeInPrediction);
                System.out.printf("%-" + 15 + "s", decisionInfo.invocations);
                System.out.printf("%-" + 15 + "s", decisionInfo.SLL_TotalLook);
                System.out.printf("%-" + 15 + "s", decisionInfo.SLL_MaxLook);
                System.out.printf("%-" + 15 + "s", decisionInfo.ambiguities.size());
                System.out.printf("%-" + 15 + "s", decisionInfo.errors);
                System.out.printf("%-" + 15 + "s", decisionInfo.LL_Fallback);
                System.out.println();
            });
    }
    
    public static Token preHandleToken(Token originToken, ParserOperatorManager operatorManager) {
        if (originToken instanceof WritableToken && originToken.getType() == QLexer.ID) {
            Integer aliasId = operatorManager.getAlias(originToken.getText());
            if (aliasId != null && originToken.getType() != aliasId) {
                ((WritableToken)originToken).setType(aliasId);
            }
        }
        return originToken;
    }
}
