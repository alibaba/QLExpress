package com.alibaba.qlexpress4.parser;

import com.alibaba.qlexpress4.exception.QLException;
import com.alibaba.qlexpress4.parser.tree.Expr;

import java.util.Arrays;
import java.util.List;

class ParseRuleRegister {

    private final static List<OperatorParseRule> rules = Arrays.asList(
            new IfExprRule(),
            new BlockExprRule(),
            new TryCatchFinalRule(),
            new LParenRule(),
            new NewRule(),
            new ConstRule(),
            new IdRule(),
            new IdOrLambdaOrQualifiedClsRule(),
            new TypeRule(),
            new LBrackRule(),
            new UnaryRule()
    );

    protected static Expr parsePrefixAndAdvance(QLParser parser, QLParser.ContextType contextType) {
        for (OperatorParseRule rule : rules) {
            if (rule.matchAndAdvance(parser)) {
                return rule.prefixParse(parser, contextType);
            }
        }
        throw QLException.reportParserErr(parser.getScript(), parser.lastToken(),
                "INVALID_EXPRESSION", "invalid expression");
    }

    protected static boolean isPrefixToken(Token token) {
        return rules.stream().anyMatch(rule -> rule.prefixCondition(token));
    }

}
