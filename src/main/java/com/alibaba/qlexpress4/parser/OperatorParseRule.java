package com.alibaba.qlexpress4.parser;

import com.alibaba.qlexpress4.QLPrecedences;
import com.alibaba.qlexpress4.exception.QLSyntaxException;
import com.alibaba.qlexpress4.exception.ReportTemplate;
import com.alibaba.qlexpress4.parser.tree.Block;
import com.alibaba.qlexpress4.parser.tree.CallExpr;
import com.alibaba.qlexpress4.parser.tree.ConstExpr;
import com.alibaba.qlexpress4.parser.tree.Expr;
import com.alibaba.qlexpress4.parser.tree.GroupExpr;
import com.alibaba.qlexpress4.parser.tree.IdExpr;
import com.alibaba.qlexpress4.parser.tree.Identifier;
import com.alibaba.qlexpress4.parser.tree.LambdaExpr;
import com.alibaba.qlexpress4.parser.tree.ListExpr;
import com.alibaba.qlexpress4.parser.tree.NewExpr;
import com.alibaba.qlexpress4.parser.tree.PrefixUnaryOpExpr;
import com.alibaba.qlexpress4.parser.tree.TypeExpr;
import com.alibaba.qlexpress4.parser.tree.VarDecl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

abstract class OperatorParseRule {

    protected boolean matchAndAdvance(QLParser parser) {
        return parser.matchAndAdvance(this::prefixCondition);
    }

    protected boolean isKeyWord(Token cur, String keyWord) {
        return Objects.equals(TokenType.KEY_WORD, cur.getType()) &&
                Objects.equals(keyWord, cur.getLexeme());
    }

    /**
     *
     * @param cur current token, non null
     * @return
     */
    abstract boolean prefixCondition(Token cur);

    /**
     * execute just after matchAndAdvance(QLParser) is true
     * @param parser
     * @return
     */
    abstract Expr prefixParse(QLParser parser);

}

class LParenRule extends OperatorParseRule {

    @Override
    public boolean prefixCondition(Token cur) {
        return cur.getType() == TokenType.LPAREN;
    }

    @Override
    public Expr prefixParse(QLParser parser) {
        // group / lambda / type cast
        QLParser.GroupType groupType = parser.lookAheadGroupType();
        switch (groupType) {
            case GROUP:
                return groupExpr(parser);
            case LAMBDA:
                return lambdaExpr(parser);
            case CAST:
                return castExpr(parser);
            default:
                throw new QLSyntaxException(ReportTemplate.report(parser.getScript(), parser.pre,
                        "unknown group type, maybe a bug"));
        }
    }

    private Expr groupExpr(QLParser parser) {
        Token keyToken = parser.pre;
        Expr groupExpr = parser.expr();
        parser.advanceOrReportError(TokenType.RPAREN, "invalid expression, expect ')'");
        return new GroupExpr(keyToken, groupExpr);
    }

    private Expr lambdaExpr(QLParser parser) {
        List<VarDecl> params = parser.parameterList();
        // skip ->
        parser.advance();
        Token keyToken = parser.pre;
        Block blockBody = null;
        Expr exprBody = null;
        if (parser.matchTypeAndAdvance(TokenType.LBRACE)) {
            blockBody = parser.block();
        } else {
            exprBody = parser.expr();
        }
        return new LambdaExpr(keyToken, params, blockBody, exprBody);
    }

    private Expr castExpr(QLParser parser) {
        Token castTypeToken;
        if (parser.matchTypeAndAdvance(TokenType.TYPE)) {
            castTypeToken = parser.pre;
        } else {
            throw new QLSyntaxException(ReportTemplate.report(parser.getScript(), parser.pre,
                    "invalid type cast"));
        }

        parser.advanceOrReportError(TokenType.RPAREN, "expect ')'");
        // cast precedence just below unary
        return new CallExpr(castTypeToken, new TypeExpr(castTypeToken),
                Collections.singletonList(parser.parsePrecedence(QLPrecedences.UNARY)));
    }
}

class NewRule extends OperatorParseRule {

    @Override
    public boolean prefixCondition(Token cur) {
        return isKeyWord(cur, KeyWordsSet.NEW);
    }

    @Override
    public Expr prefixParse(QLParser parser) {
        Token newToken = parser.pre;
        parser.advanceOrReportError(TokenType.ID, "invalid class name");
        Token clazzToken = parser.pre;
        parser.advanceOrReportError(TokenType.LPAREN, "expect '(' for arguments");

        return new NewExpr(newToken, new Identifier(clazzToken), parser.argumentList());
    }
}

class ConstRule extends OperatorParseRule {

    @Override
    public boolean prefixCondition(Token cur) {
        return cur.getType() == TokenType.NUMBER || cur.getType() == TokenType.STRING ||
                isKeyWord(cur, KeyWordsSet.NULL) || isKeyWord(cur, KeyWordsSet.TRUE) ||
                isKeyWord(cur, KeyWordsSet.FALSE);
    }

    @Override
    public Expr prefixParse(QLParser parser) {
        return new ConstExpr(parser.pre, parser.pre.getLiteral());
    }
}

class IdRule extends OperatorParseRule {

    @Override
    public boolean prefixCondition(Token cur) {
        return cur.getType() == TokenType.ID;
    }

    @Override
    public Expr prefixParse(QLParser parser) {
        Token idToken = parser.pre;
        if (parser.matchTypeAndAdvance(TokenType.ARROW)) {
            // single param lambda expression
            Token lambdaKeyToken = parser.pre;
            if (parser.matchTypeAndAdvance(TokenType.LBRACE)) {
                return new LambdaExpr(lambdaKeyToken,
                        Collections.singletonList(
                                new VarDecl(null, new Identifier(idToken))
                        ),
                        parser.block(), null);
            } else {
                return new LambdaExpr(lambdaKeyToken,
                        Collections.singletonList(
                                new VarDecl(null, new Identifier(idToken))
                        ),
                        null, parser.expr());
            }
        }
        return new IdExpr(idToken);
    }
}

class TypeRule extends OperatorParseRule {

    @Override
    public boolean prefixCondition(Token cur) {
        return cur.getType() == TokenType.TYPE;
    }

    @Override
    public Expr prefixParse(QLParser parser) {
        return new TypeExpr(parser.pre);
    }
}

/**
 * list literal
 */
class LBrackRule extends OperatorParseRule {

    @Override
    public boolean prefixCondition(Token cur) {
        return cur.getType() == TokenType.LBRACK;
    }

    @Override
    public Expr prefixParse(QLParser parser) {
        Token keyToken = parser.pre;
        List<Expr> elements = new ArrayList<>();
        while (!parser.matchTypeAndAdvance(TokenType.RBRACK)) {
            if (parser.isEnd()) {
                throw new QLSyntaxException(ReportTemplate.report(parser.getScript(),
                        keyToken, "can not find ']' to match"));
            }
            if (!elements.isEmpty()) {
                parser.advanceOrReportError(TokenType.COMMA, "expect ',' between elements");
            }
            elements.add(parser.expr());
        }
        return new ListExpr(keyToken, elements);
    }
}

class UnaryRule extends OperatorParseRule {

    @Override
    public boolean prefixCondition(Token cur) {
        return QLPrecedences.getPrefixPrecedence(cur) != null;
    }

    @Override
    public Expr prefixParse(QLParser parser) {
        int tokenPrecedence = QLPrecedences.getPrefixPrecedence(parser.pre);
        return new PrefixUnaryOpExpr(parser.pre, parser.parsePrecedence(tokenPrecedence + 1));
    }
}