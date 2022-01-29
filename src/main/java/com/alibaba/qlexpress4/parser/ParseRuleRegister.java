package com.alibaba.qlexpress4.parser;

import com.alibaba.qlexpress4.exception.QLSyntaxException;
import com.alibaba.qlexpress4.exception.ReportTemplate;
import com.alibaba.qlexpress4.parser.tree.Expr;

import java.util.Arrays;
import java.util.List;

class ParseRuleRegister {

    private final static List<OperatorParseRule> rules = Arrays.asList(
            new LParenRule(),
            new NewRule(),
            new ConstRule(),
            new IdRule(),
            new TypeRule(),
            new LBrackRule(),
            new UnaryRule()
    );

    protected static Expr parsePrefixAndAdvance(QLParser parser) {
        for (OperatorParseRule rule : rules) {
            if (rule.matchAndAdvance(parser)) {
                return rule.prefixParse(parser);
            }
        }
        throw new QLSyntaxException(ReportTemplate.report(parser.getScript(),
                parser.isEnd()? parser.pre: parser.cur, "invalid expression"));
    }

    protected static boolean isPrefixToken(Token token) {
        return rules.stream().anyMatch(rule -> rule.prefixCondition(token));
    }

}
