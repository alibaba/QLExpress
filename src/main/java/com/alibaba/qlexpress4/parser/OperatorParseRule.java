package com.alibaba.qlexpress4.parser;

import com.alibaba.qlexpress4.QLPrecedences;
import com.alibaba.qlexpress4.exception.QLException;
import com.alibaba.qlexpress4.parser.tree.*;

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
    abstract Expr prefixParse(QLParser parser, QLParser.ContextType contextType);

}

class IfExprRule extends OperatorParseRule {
    @Override
    boolean prefixCondition(Token cur) {
        return isKeyWord(cur, KeyWordsSet.IF);
    }

    @Override
    Expr prefixParse(QLParser parser, QLParser.ContextType contextType) {
        Token keyToken = parser.pre;
        parser.advanceOrReportError(TokenType.LPAREN, "EXPECT_LPAREN_BEFORE_IF_CONDITION",
                "expect '(' before if condition");
        Expr condition = parser.expr(contextType);
        parser.advanceOrReportError(TokenType.RPAREN, "EXPECT_RPAREN_AFTER_IF_CONDITION",
                "expect ')' after if condition");

        Stmt thenBranch = parser.statement(contextType);

        if (parser.matchKeyWordAndAdvance(KeyWordsSet.ELSE)) {
            Stmt elseBranch = parser.statement(contextType);
            return new IfExpr(keyToken, condition, thenBranch, elseBranch);
        } else {
            return new IfExpr(keyToken, condition, thenBranch, null);
        }
    }
}

class BlockExprRule extends OperatorParseRule {
    @Override
    boolean prefixCondition(Token cur) {
        return cur.getType() == TokenType.LBRACE;
    }

    @Override
    Expr prefixParse(QLParser parser, QLParser.ContextType contextType) {
        return parser.block(contextType);
    }
}

class TryCatchFinalRule extends OperatorParseRule {

    @Override
    boolean prefixCondition(Token cur) {
        return isKeyWord(cur, KeyWordsSet.TRY);
    }

    @Override
    Expr prefixParse(QLParser parser, QLParser.ContextType contextType) {
        return parser.tryCatchStmt(contextType);
    }
}

class LParenRule extends OperatorParseRule {

    @Override
    public boolean prefixCondition(Token cur) {
        return cur.getType() == TokenType.LPAREN;
    }

    @Override
    public Expr prefixParse(QLParser parser, QLParser.ContextType contextType) {
        // group / lambda / type cast
        QLParser.GroupType groupType = parser.lookAheadGroupType();
        switch (groupType) {
            case GROUP:
                return groupExpr(parser, contextType);
            case LAMBDA:
                return lambdaExpr(parser, contextType);
            case CAST:
                return castExpr(parser, contextType);
            default:
                throw QLException.reportParserErr(parser.getScript(), parser.pre,
                        "UNKNOWN_GROUP_TYPE", "unknown group type, maybe a bug");
        }
    }

    private Expr groupExpr(QLParser parser, QLParser.ContextType contextType) {
        Token keyToken = parser.pre;
        Expr groupExpr = parser.expr(contextType);
        parser.advanceOrReportError(TokenType.RPAREN, "EXPECT_RPAREN_AFTER_GROUP_EXPRESSION",
                "invalid expression, expect ')' after group expression");
        return new GroupExpr(keyToken, groupExpr);
    }

    private Expr lambdaExpr(QLParser parser, QLParser.ContextType contextType) {
        List<VarDecl> params = parser.parameterList();
        // skip ->
        parser.advance();
        Token keyToken = parser.pre;
        Block blockBody = null;
        Expr exprBody = null;
        if (parser.matchTypeAndAdvance(TokenType.LBRACE)) {
            blockBody = parser.block(QLParser.ContextType.BLOCK);
        } else {
            exprBody = parser.expr(contextType);
        }
        return new LambdaExpr(keyToken, params, blockBody, exprBody);
    }

    private Expr castExpr(QLParser parser, QLParser.ContextType contextType) {
        Token keyToken = parser.pre;
        Expr typeExpr = parser.expr(contextType);
        parser.advanceOrReportError(TokenType.RPAREN, "EXPECT_RPAREN_AFTER_TYPE_CAST",
                "expect ')' after type cast");

        Expr target = parser.parsePrecedence(QLPrecedences.UNARY, contextType);

        // cast precedence just below unary
        return new CastExpr(keyToken, typeExpr, target);
    }
}

class NewRule extends OperatorParseRule {

    @Override
    public boolean prefixCondition(Token cur) {
        return isKeyWord(cur, KeyWordsSet.NEW);
    }

    @Override
    public Expr prefixParse(QLParser parser, QLParser.ContextType contextType) {
        Token newToken = parser.pre;
        DeclType newType = parser.declType();

        parser.advanceOrReportError(TokenType.LPAREN, "EXPECT_LPAREN_BEFORE_ARGUMENTS",
                "expect '(' before arguments");

        return new NewExpr(newToken, newType, parser.argumentList());
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
    public Expr prefixParse(QLParser parser, QLParser.ContextType contextType) {
        return new ConstExpr(parser.pre, parser.pre.getLiteral());
    }
}

class IdRule extends OperatorParseRule {

    @Override
    protected boolean matchAndAdvance(QLParser parser) {
        if (!parser.isEnd() &&
                parser.cur.getType() == TokenType.ID
                && (parser.pre != null && parser.pre.getType() == TokenType.DOT)) {
            parser.advance();
            return true;
        }
        return false;
    }

    @Override
    public boolean prefixCondition(Token cur) {
        return cur.getType() == TokenType.ID;
    }

    @Override
    public Expr prefixParse(QLParser parser, QLParser.ContextType contextType) {
        return new IdExpr(parser.pre);
    }
}

class IdOrLambdaOrQualifiedClsRule extends OperatorParseRule {

    @Override
    boolean prefixCondition(Token cur) {
        return cur.getType() == TokenType.ID;
    }

    @Override
    public Expr prefixParse(QLParser parser, QLParser.ContextType contextType) {
        Token idToken = parser.pre;
        if (parser.matchTypeAndAdvance(TokenType.ARROW)) {
            // single param lambda expression
            Token lambdaKeyToken = parser.pre;
            if (parser.matchTypeAndAdvance(TokenType.LBRACE)) {
                return new LambdaExpr(lambdaKeyToken,
                        Collections.singletonList(
                                new VarDecl(null, new Identifier(idToken))
                        ),
                        parser.block(QLParser.ContextType.BLOCK), null);
            } else {
                return new LambdaExpr(lambdaKeyToken,
                        Collections.singletonList(
                                new VarDecl(null, new Identifier(idToken))
                        ),
                        null, parser.expr(contextType));
            }
        }

        Expr idExpr = parser.parseIdOrQualifiedCls();
        if (idExpr instanceof ConstExpr && !parser.isEnd() && parser.cur.getType() == TokenType.LT) {
            // generic type
            Token gtNextToken = parser.lookAheadGtNextTokenWithCache(parser.cur);
            if (gtNextToken != null && gtNextToken.getType() == TokenType.RPAREN) {
                // generic type argument
                parser.advance();
                List<DeclTypeArgument> typeArguments = parser.typeArgumentList();
                ConstExpr typeConstExpr = (ConstExpr) idExpr;
                return new TypeExpr(idToken, new DeclType(typeConstExpr.getKeyToken(),
                        (Class<?>) typeConstExpr.getConstValue(), typeArguments));
            }
        }
        return idExpr;
    }
}

class TypeRule extends OperatorParseRule {

    @Override
    public boolean prefixCondition(Token cur) {
        return cur.getType() == TokenType.TYPE;
    }

    @Override
    public Expr prefixParse(QLParser parser, QLParser.ContextType contextType) {
        return new TypeExpr(parser.pre, new DeclType(parser.pre,
                parser.mustLoadQualifiedCls((String) parser.pre.getLiteral(), parser.pre),
                Collections.emptyList()));
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
    public Expr prefixParse(QLParser parser, QLParser.ContextType contextType) {
        Token keyToken = parser.pre;
        List<Expr> elements = new ArrayList<>();
        while (!parser.matchTypeAndAdvance(TokenType.RBRACK)) {
            if (parser.isEnd()) {
                throw QLException.reportParserErr(parser.getScript(),
                        keyToken, "CAN_NOT_FIND_RBRACK_TO_MATCH", "can not find ']' to match");
            }
            if (!elements.isEmpty()) {
                parser.advanceOrReportError(TokenType.COMMA, "EXPECT_COMMA_BETWEEN_ELEMENTS",
                        "expect ',' between elements");
            }
            elements.add(parser.expr(contextType));
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
    public Expr prefixParse(QLParser parser, QLParser.ContextType contextType) {
        int tokenPrecedence = QLPrecedences.getPrefixPrecedence(parser.pre);
        return new PrefixUnaryOpExpr(parser.pre, parser
                .parsePrecedence(tokenPrecedence + 1, contextType));
    }
}