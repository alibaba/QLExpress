package com.alibaba.qlexpress4.parser;

import com.alibaba.qlexpress4.QLPrecedences;
import com.alibaba.qlexpress4.exception.QLException;
import com.alibaba.qlexpress4.parser.tree.ArrayCallExpr;
import com.alibaba.qlexpress4.parser.tree.AssignExpr;
import com.alibaba.qlexpress4.parser.tree.BinaryOpExpr;
import com.alibaba.qlexpress4.parser.tree.Block;
import com.alibaba.qlexpress4.parser.tree.Break;
import com.alibaba.qlexpress4.parser.tree.Continue;
import com.alibaba.qlexpress4.parser.tree.DeclType;
import com.alibaba.qlexpress4.parser.tree.DeclTypeArgument;
import com.alibaba.qlexpress4.parser.tree.EmptyStmt;
import com.alibaba.qlexpress4.parser.tree.Expr;
import com.alibaba.qlexpress4.parser.tree.FieldCallExpr;
import com.alibaba.qlexpress4.parser.tree.ForEachStmt;
import com.alibaba.qlexpress4.parser.tree.ForStmt;
import com.alibaba.qlexpress4.parser.tree.CallExpr;
import com.alibaba.qlexpress4.parser.tree.FunctionStmt;
import com.alibaba.qlexpress4.parser.tree.GroupExpr;
import com.alibaba.qlexpress4.parser.tree.IdExpr;
import com.alibaba.qlexpress4.parser.tree.Identifier;
import com.alibaba.qlexpress4.parser.tree.IfStmt;
import com.alibaba.qlexpress4.parser.tree.ImportStmt;
import com.alibaba.qlexpress4.parser.tree.LambdaExpr;
import com.alibaba.qlexpress4.parser.tree.MacroStmt;
import com.alibaba.qlexpress4.parser.tree.Program;
import com.alibaba.qlexpress4.parser.tree.Return;
import com.alibaba.qlexpress4.parser.tree.Stmt;
import com.alibaba.qlexpress4.parser.tree.SuffixUnaryOpExpr;
import com.alibaba.qlexpress4.parser.tree.TernaryExpr;
import com.alibaba.qlexpress4.parser.tree.VarDecl;
import com.alibaba.qlexpress4.parser.tree.LocalVarDeclareStmt;
import com.alibaba.qlexpress4.parser.tree.WhileStmt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

public class QLParser {

    /**
     * left parent Token position -> token just after right parent token , Optional.empty() when without next token
     */
    private final Map<Integer, Optional<Token>> parenNextToken = new HashMap<>();

    /**
     * for generic type arguments
     * lt(`<`) Token position -> token just after `>` , Optional.empty() when without next token
     */
    private final Map<Integer, Optional<Token>> gtNextToken = new HashMap<>();

    private final Map<String, Integer> userDefineOperatorsPrecedence;

    private final Scanner scanner;

    protected Token pre;

    protected Token cur;

    public QLParser(Map<String, Integer> userDefineOperatorsPrecedence, Scanner scanner) {
        this.userDefineOperatorsPrecedence = userDefineOperatorsPrecedence;
        this.scanner = scanner;
        cur = scanner.next();
    }

    public Program parse() {
        List<Stmt> stmtList = new ArrayList<>();

        while (!isEnd() && matchKeyWordAndAdvance(KeyWordsSet.IMPORT)) {
            // import must at first
           stmtList.add(importStmt());
        }

        Stmt statement = null;
        while (!isEnd()) {
            if (statement instanceof Expr) {
                // expr statement must end with `;` if not last line
                advanceOrReportError(TokenType.SEMI, "STATEMENT_MUST_END_WITH_SEMI",
                        "statement must end with ';'");
                if (isEnd()) {
                    break;
                }
            }
            statement = statement();
            stmtList.add(statement);
        }

        return new Program(stmtList);
    }

    public String getScript() {
        return scanner.getScript();
    }

    private Stmt statement() {
        if (matchTypeAndAdvance(TokenType.SEMI)) {
            return new EmptyStmt(pre);
        } else if (matchTypeAndAdvance(TokenType.LBRACE)) {
            // block
            return block();
        } else if (matchKeyWordAndAdvance(KeyWordsSet.IF)) {
            // if
            return ifStmt();
        } else if (matchKeyWordAndAdvance(KeyWordsSet.WHILE)) {
            // while
            return whileStmt();
        } else if (matchKeyWordAndAdvance(KeyWordsSet.FOR)) {
            // for
            return forStmt();
        } else if (matchKeyWordAndAdvance(KeyWordsSet.IMPORT)) {
            // import
            throw QLException.reportParserErr(scanner.getScript(), pre, "IMPORT_STATEMENT_MUST_AT_BEGINNING",
                    "import statement must at the beginning of script");
        } else if (matchKeyWordAndAdvance(KeyWordsSet.FUNCTION)) {
            // function
            return functionStmt();
        } else if (matchKeyWordAndAdvance(KeyWordsSet.MACRO)) {
            // macro
            return macroStmt();
        } else if (matchKeyWordAndAdvance(KeyWordsSet.BREAK) ||
                matchKeyWordAndAdvance(KeyWordsSet.CONTINUE)) {
            // single token statement
            return singleTokenStmt();
        } else if (matchKeyWordAndAdvance(KeyWordsSet.RETURN)) {
            return returnStmt();
        } else if (isLocalVarDeclStatement()) {
            // var declare statement
            return localVarDeclareStmt();
        } else {
            // expression(primary, assignment, ternary,....)
            return expr();
        }
    }

    private boolean isLocalVarDeclStatement() {
        Token typeDeclNextToken = lookAheadTypeDeclNextToken();
        if (typeDeclNextToken == null) {
            scanner.back();
            return false;
        }
        Token expectAssignOrSemi = scanner.lookAhead();
        boolean res = isTokenType(expectAssignOrSemi, TokenType.ASSIGN) ||
                isTokenType(expectAssignOrSemi, TokenType.SEMI);
        scanner.back();
        return res;
    }

    private Token lookAheadTypeDeclNextToken() {
        if (isTokenType(cur, TokenType.ID)) {
            Token maybeVarToken;
            while (isTokenType(maybeVarToken = scanner.lookAhead(), TokenType.DOT)) {
                if (!isTokenType(scanner.lookAhead(), TokenType.ID)) {
                    // invalid type declare
                    return null;
                }
            }

            if (isTokenType(maybeVarToken, TokenType.LT)) {
                return lookAheadGtNextToken(maybeVarToken);
            } else {
                return maybeVarToken;
            }
        } else if (isTokenType(cur, TokenType.TYPE)) {
            return scanner.lookAhead();
        } else {
            return null;
        }
    }

    public Token lookAheadGtNextTokenWithCache(Token ltToken) {
        if (gtNextToken.containsKey(ltToken.getPos())) {
            return gtNextToken.get(ltToken.getPos()).orElse(null);
        }

        Token res = lookAheadGtNextToken(ltToken);
        scanner.back();
        return res;
    }

    private Token lookAheadGtNextToken(Token ltToken) {
        Token ahead = scanner.lookAhead();
        while (true) {
            if (ahead == null || ahead.getType() == TokenType.SEMI) {
                gtNextToken.put(ltToken.getPos(), Optional.empty());
                return null;
            }
            if (ahead.getType() == TokenType.GT) {
                break;
            }
            ahead = ahead.getType() == TokenType.LT?
                    lookAheadGtNextToken(ahead):
                    scanner.lookAhead();
        }

        Token nextToken = scanner.lookAhead();
        gtNextToken.put(ltToken.getPos(), Optional.ofNullable(nextToken));
        return nextToken;
    }

    private LocalVarDeclareStmt localVarDeclareStmt() {
        // first three token has been determined
        Token keyToken = cur;
        DeclType type = declType();
        Identifier varName;
        if (matchTypeAndAdvance(TokenType.ID)) {
            varName = new Identifier(pre);
        } else {
            throw QLException.reportParserErr(scanner.getScript(), lastToken(), "INVALID_VARIABLE_NAME",
                    "invalid variable name");
        }

        if (matchTypeAndAdvance(TokenType.SEMI)) {
            return new LocalVarDeclareStmt(keyToken, new VarDecl(type, varName), null);
        } else if (matchTypeAndAdvance(TokenType.ASSIGN)) {
            Expr expr = expr();
            advanceOrReportError(TokenType.SEMI, "STATEMENT_MUST_END_WITH_SEMI",
                    "statement must end with ';'");
            return new LocalVarDeclareStmt(keyToken, new VarDecl(type, varName), expr);
        }
        throw QLException.reportParserErr(scanner.getScript(), keyToken, "INVALID_VARIABLE_DECLARE",
                "invalid variable declaration statement");
    }

    private VarDecl varDecl() {
        DeclType maybeType = declType();
        if (isEnd() || cur.getType() == TokenType.COMMA ||
                cur.getType() == TokenType.RPAREN ||
                cur.getType() == TokenType.COLON) {
            // var without type
            return new VarDecl(null, maybeType.getType().get(0));
        }

        if (matchTypeAndAdvance(TokenType.ID)) {
            return new VarDecl(maybeType, new Identifier(pre));
        } else {
            throw QLException.reportParserErr(scanner.getScript(), lastToken(),
                    "INVALID_VARIABLE_NAME", "invalid variable name");
        }
    }

    protected DeclType declType() {
        if (matchTypeAndAdvance(TokenType.TYPE)) {
            return new DeclType(Collections.singletonList(new Identifier(pre)), Collections.emptyList());
        } else if (matchTypeAndAdvance(TokenType.ID)) {
            List<Identifier> typeReference = new ArrayList<>();
            typeReference.add(new Identifier(pre));
            while (matchTypeAndAdvance(TokenType.DOT)) {
                if (matchTypeAndAdvance(TokenType.ID)) {
                    typeReference.add(new Identifier(pre));
                } else {
                    throw QLException.reportParserErr(scanner.getScript(), lastToken(),
                            "INVALID_TYPE_DECLARE", "invalid type declare");
                }
            }

            if (matchTypeAndAdvance(TokenType.LT)) {
                // generic type arguments
                return new DeclType(typeReference, typeArgumentList());
            } else {
                return new DeclType(typeReference, Collections.emptyList());
            }
        }
        throw QLException.reportParserErr(scanner.getScript(), lastToken(),
                "INVALID_TYPE_DECLARE", "invalid type declare");
    }

    protected List<DeclTypeArgument> typeArgumentList() {
        Token ltToken = pre;
        List<DeclTypeArgument> typeArguments = new ArrayList<>();
        while (!matchTypeAndAdvance(TokenType.GT)) {
            if (isEnd()) {
                throw QLException.reportParserErr(scanner.getScript(), ltToken,
                        "CAN_NOT_FIND_GT_TO_MATCH", "can not find '>' to match");
            }
            if (!typeArguments.isEmpty()) {
                advanceOrReportError(TokenType.COMMA, "EXPECT_COMMA_BETWEEN_TYPE_ARGUMENT",
                        "expect ',' between type argument");
            }
            typeArguments.add(typeArgument());
        }
        return typeArguments;
    }

    private DeclTypeArgument typeArgument() {
        if (matchTypeAndAdvance(TokenType.QUESTION)) {
            // wildcard
            if (matchKeyWordAndAdvance(KeyWordsSet.EXTENDS) || matchKeyWordAndAdvance(KeyWordsSet.SUPER)) {
                DeclTypeArgument.Bound bound = KeyWordsSet.EXTENDS.equals(pre.getLexeme())?
                        DeclTypeArgument.Bound.EXTENDS: DeclTypeArgument.Bound.SUPER;
                if (!isEnd() && cur.getType() == TokenType.ID) {
                    return new DeclTypeArgument(declType(), bound);
                } else {
                    throw QLException.reportParserErr(scanner.getScript(),
                            lastToken(), "INVALID_TYPE_BOUND",
                            "invalid type bound");
                }
            } else {
                return new DeclTypeArgument(new DeclType(Collections.singletonList(new Identifier(pre)),
                        Collections.emptyList()), DeclTypeArgument.Bound.NONE);
            }
        } else if (!isEnd() && (cur.getType() == TokenType.ID || cur.getType() == TokenType.TYPE)) {
            return new DeclTypeArgument(declType(), DeclTypeArgument.Bound.NONE);
        }
        throw QLException.reportParserErr(scanner.getScript(),
                lastToken(), "INVALID_TYPE_ARGUMENT", "invalid type argument");
    }

    private Stmt returnStmt() {
        Token keyToken = pre;
        if (matchTypeAndAdvance(TokenType.SEMI)) {
            return new Return(keyToken, null);
        }

        Expr expr = expr();
        advanceOrReportError(TokenType.SEMI, "STATEMENT_MUST_END_WITH_SEMI",
                "statement must end with ';'");
        return new Return(keyToken, expr);
    }

    private Stmt singleTokenStmt() {
        Token keyToken = pre;
        advanceOrReportError(TokenType.SEMI, "STATEMENT_MUST_END_WITH_SEMI",
                "statement must end with ';'");
        switch (keyToken.getLexeme()) {
            case KeyWordsSet.BREAK:
                return new Break(keyToken);
            case KeyWordsSet.CONTINUE:
                return new Continue(keyToken);
        }
        // never should run there
        throw QLException.reportParserErr(scanner.getScript(), keyToken, "UNKNOWN_STATEMENT",
                "unknown statement");
    }

    private MacroStmt macroStmt() {
        Identifier macroName = idOrReportError("INVALID_MACRO_NAME", "invalid macro name");
        advanceOrReportError(TokenType.LBRACE, "EXPECT_LBRACE_IN_MACRO_DECLARE",
                "expect '{' in macro declaration");
        Block body = block();
        return new MacroStmt(macroName.getKeyToken(), macroName, body);
    }

    private ImportStmt importStmt() {
        Token importToken = pre;
        boolean staticImport = matchKeyWordAndAdvance(KeyWordsSet.STATIC);
        Token prePackToken = null;
        StringBuilder path = new StringBuilder();

        while (!isEnd()) {
            if (matchTypeAndAdvance(TokenType.MUL)) {
                advanceOrReportError(TokenType.SEMI, "STATEMENT_MUST_END_WITH_SEMI",
                        "statement must end with ';'");
                if (prePackToken == null) {
                    throw QLException.reportParserErr(scanner.getScript(), importToken,
                            "INVALID_IMPORT_STATEMENT", "invalid import statement");
                }
                return new ImportStmt(prePackToken, ImportStmt.ImportType.PREFIX, path.toString(), staticImport);
            } else if (matchTypeAndAdvance(TokenType.ID)) {
                if (prePackToken != null) {
                    path.append('.');
                }
                prePackToken = pre;
                path.append(prePackToken.getLexeme());
                if (matchTypeAndAdvance(TokenType.SEMI)) {
                    return new ImportStmt(prePackToken, ImportStmt.ImportType.FIXED, path.toString(), staticImport);
                } else if (!matchTypeAndAdvance(TokenType.DOT)) {
                    throw QLException.reportParserErr(scanner.getScript(), lastToken(),
                            "STATEMENT_MUST_END_WITH_SEMI",
                            "statement must end with ';'");
                }
            } else {
                throw QLException.reportParserErr(scanner.getScript(), lastToken(), "INVALID_IMPORT_PACKAGE",
                        "invalid import package");
            }
        }

        throw QLException.reportParserErr(scanner.getScript(), importToken,
                "INVALID_IMPORT_STATEMENT", "invalid import statement");
    }

    private FunctionStmt functionStmt() {
        Identifier functionName = idOrReportError("INVALID_FUNCTION_NAME", "invalid function name");

        advanceOrReportError(TokenType.LPAREN, "EXPECT_LPAREN_BEFORE_PARAMETER_LIST",
                "expect '(' before parameter list");
        List<VarDecl> paramList = parameterList();

        advanceOrReportError(TokenType.LBRACE, "EXPECT_LBRACE_BEFORE_FUNCTION_BODY",
                "expect '{' before function body");
        Block block = block();

        return new FunctionStmt(functionName.getKeyToken(), functionName, paramList, block);
    }

    protected List<VarDecl> parameterList() {
        Token lParen = pre;
        List<VarDecl> parameterList = new ArrayList<>();
        while (!matchTypeAndAdvance(TokenType.RPAREN)) {
            if (isEnd()) {
                throw QLException.reportParserErr(scanner.getScript(),
                        lParen, "CAN_NOT_FIND_RPAREN_TO_MATCH", "can not find ')' to match it");
            }
            if (!parameterList.isEmpty()) {
                advanceOrReportError(TokenType.COMMA, "EXPRECT_COMMA_BETWEEN_PARAMETERS",
                        "expect ',' between parameters");
            }
            parameterList.add(varDecl());
        }

        return parameterList;
    }

    protected Block block() {
        Token keyToken = pre;
        List<Stmt> stmtList = new ArrayList<>();
        Stmt statement = null;
        // block end
        while (!matchTypeAndAdvance(TokenType.RBRACE)) {
            if (isEnd()) {
                throw QLException.reportParserErr(scanner.getScript(), keyToken,
                        "CAN_NOT_FIND_RBRACE_TO_MATCH", "can not find '}' to match");
            }
            if (statement instanceof Expr) {
                advanceOrReportError(TokenType.SEMI, "STATEMENT_MUST_END_WITH_SEMI",
                        "statement must end with ';'");
                statement = null;
                continue;
            }
            statement = statement();
            stmtList.add(statement);
        }

        return new Block(keyToken, stmtList);
    }

    private IfStmt ifStmt() {
        Token keyToken = pre;
        advanceOrReportError(TokenType.LPAREN, "EXPECT_LPAREN_BEFORE_IF_CONDITION",
                "expect '(' before if condition");
        Expr condition = expr();
        advanceOrReportError(TokenType.RPAREN, "EXPECT_RPAREN_AFTER_IF_CONDITION",
                "expect ')' after if condition");

        Stmt thenBranch = statement();

        if (matchKeyWordAndAdvance(KeyWordsSet.ELSE)) {
            Stmt elseBranch = statement();
            return new IfStmt(keyToken, condition, thenBranch, elseBranch);
        } else {
            return new IfStmt(keyToken, condition, thenBranch, null);
        }
    }

    private WhileStmt whileStmt() {
        Token keyToken = pre;

        advanceOrReportError(TokenType.LPAREN, "EXPECT_LPAREN_BEFORE_WHILE_CONDITION",
                "expect '(' before while condition");
        Expr condition = expr();
        advanceOrReportError(TokenType.RPAREN, "EXPECT_RPAREN_AFTER_WHILE_CONDITION",
                "expect ')' after while statement");

        Stmt body = statement();
        return new WhileStmt(keyToken, condition, body);
    }

    private Stmt forStmt() {
        Token forToken = pre;
        advanceOrReportError(TokenType.LPAREN, "EXPECT_LPAREN_AFTER_FOR", "expect '(' after 'for'");
        return isForEach()? forEachStmt(forToken): traditionalForStmt(forToken);
    }

    private ForEachStmt forEachStmt(Token forToken) {
        VarDecl itVar;
        itVar = varDecl();
        advanceOrReportError(TokenType.COLON, "EXPECT_COLON_AFTER_FOR_EACH_VARIABLE_DECLARE",
                "expect ':' after for-each variable declare");

        Expr target = expr();
        advanceOrReportError(TokenType.RPAREN, "EXPECT_RPAREN_AFTER_FOR_EACH_EXPRESSION",
                "expect ')' after for-each expression");

        Stmt body = statement();
        return new ForEachStmt(forToken, itVar, target, body);
    }

    private ForStmt traditionalForStmt(Token forToken) {
        Stmt forInit = null;
        if (!matchTypeAndAdvance(TokenType.SEMI)) {
            if (isLocalVarDeclStatement()) {
                forInit = localVarDeclareStmt();
            } else {
                forInit = expr();
                advanceOrReportError(TokenType.SEMI, "EXPECT_SEMI_AFTER_FOR_INIT",
                        "expect ';' after 'for' init expression");
            }
        }
        Expr condition = null;
        if (!matchTypeAndAdvance(TokenType.SEMI)) {
            condition = expr();
            advanceOrReportError(TokenType.SEMI, "EXPECT_SEMI_AFTER_FOR_CONDITION",
                    "expect ';' after 'for' condition expression");
        }
        Expr forUpdate = null;
        if (!matchTypeAndAdvance(TokenType.SEMI)) {
            forUpdate = expr();
        }
        advanceOrReportError(TokenType.RPAREN, "EXPECT_SEMI_AFTER_FOR_UPDATE",
                "expect ')' after 'for' update expression");

        Stmt body = statement();
        return new ForStmt(forToken, forInit, condition, forUpdate, body);
    }

    private boolean isForEach() {
        boolean result = isForEachInner();
        scanner.back();
        return result;
    }

    private boolean isForEachInner() {
        Token mayBeVarNameToken = lookAheadTypeDeclNextToken();
        if (mayBeVarNameToken == null) {
            return false;
        }
        if (mayBeVarNameToken.getType() == TokenType.COLON) {
            return true;
        }
        Token expectColon = scanner.lookAhead();
        return isTokenType(expectColon, TokenType.COLON);
    }

    protected boolean isEnd() {
        return cur == null;
    }

    protected boolean matchTypeAndAdvance(TokenType expectType) {
        return matchAndAdvance(cur -> Objects.equals(expectType, cur.getType()));
    }

    protected boolean matchAndAdvance(Predicate<Token> predicate) {
        if (isEnd()) {
            return false;
        }
        if (predicate.test(cur)) {
            advance();
            return true;
        }
        return false;
    }

    private Identifier idOrReportError(String errorCode, String reason) {
        if (cur.getType() != TokenType.ID) {
            throw QLException.reportParserErr(scanner.getScript(), cur, errorCode, reason);
        }
        Identifier id = new Identifier(cur);
        advance();
        return id;
    }

    protected void advanceOrReportError(TokenType expectType, String errorCode, String reason) {
        advanceOrReportErrorWithToken(expectType, errorCode, reason, lastToken());
    }

    private void advanceOrReportErrorWithToken(TokenType expectType, String errorCode, String reason, Token reportToken) {
        if (isEnd() || !Objects.equals(cur.getType(), expectType)) {
            throw  QLException.reportParserErr(scanner.getScript(), reportToken, errorCode, reason);
        }
        advance();
    }

    protected boolean matchKeyWordAndAdvance(String keyword) {
        return matchAndAdvance(cur ->
                Objects.equals(TokenType.KEY_WORD, cur.getType()) && Objects.equals(keyword, cur.getLexeme()));
    }

    protected void advance() {
        pre = cur;
        cur = scanner.next();
    }

    protected Expr expr() {
        return parsePrecedence(QLPrecedences.ASSIGN);
    }

    private boolean canAssign(Expr leftExpr) {
        return (leftExpr instanceof FieldCallExpr) || (leftExpr instanceof IdExpr) ||
                (leftExpr instanceof ArrayCallExpr);
    }

    enum GroupType {GROUP, LAMBDA, CAST}

    protected GroupType lookAheadGroupType() {
        Token rParenNextToken = lookAheadRParenNextToken(true);
        if (rParenNextToken == null || rParenNextToken.getType() == TokenType.ADD ||
                rParenNextToken.getType() == TokenType.SUB ||
                rParenNextToken.getType() == TokenType.INC ||
                rParenNextToken.getType() == TokenType.DEC) {
            // + - ++ -- ( is prefix and middle operator in same time
            // so we need to handle expression like `(int) -1`
            if (cur != null && cur.getType() == TokenType.TYPE) {
                Token expectRParen = scanner.lookAhead();
                if (expectRParen != null && expectRParen.getType() == TokenType.RPAREN) {
                    scanner.back();
                    return GroupType.CAST;
                }
            }
            scanner.back();
        } else if (ParseRuleRegister.isPrefixToken(rParenNextToken)) {
            return GroupType.CAST;
        }

        return rParenNextToken != null && rParenNextToken.getType() == TokenType.ARROW?
                GroupType.LAMBDA: GroupType.GROUP;
    }

    private Token lookAheadRParenNextToken(boolean preLParen) {
        Token lParen = preLParen? pre: cur;
        if (parenNextToken.containsKey(lParen.getPos())) {
            // get next token from cache
            return parenNextToken.get(lParen.getPos()).orElse(null);
        }

        Token token = lookAheadRParenNextTokenInner(preLParen, lParen);
        scanner.back();
        return token;
    }

    private Token lookAheadRParenNextTokenInner(boolean fromCur, Token lParenToken) {
        Token ahead = fromCur? cur: scanner.lookAhead();
        while (true) {
            if (ahead == null) {
                throw QLException.reportParserErr(scanner.getScript(), lParenToken,
                        "CAN_NOT_FIND_RPAREN_TO_MATCH",
                        "can not find ')' to match it");
            }
            if (ahead.getType() == TokenType.RPAREN) {
                break;
            }
            ahead = ahead.getType() == TokenType.LPAREN?
                    lookAheadRParenNextTokenInner(false, ahead):
                    scanner.lookAhead();
        }

        Token nextToken = scanner.lookAhead();
        parenNextToken.put(lParenToken.getPos(), Optional.ofNullable(nextToken));
        return nextToken;
    }

    private LambdaExpr lambdaExpr() {
        List<VarDecl> params = parameterList();
        // skip ->
        advance();
        Token keyToken = pre;
        Block blockBody = null;
        Expr exprBody = null;
        if (matchTypeAndAdvance(TokenType.LBRACE)) {
            blockBody = block();
        } else {
            exprBody = expr();
        }
        return new LambdaExpr(keyToken, params, blockBody, exprBody);
    }

    protected Expr parsePrecedence(int precedence) {
        Expr left = ParseRuleRegister.parsePrefixAndAdvance(this);

        while (true) {
            // union bit move op
            unionOp();
            Integer curOpPrecedence = getCurOpPrecedence();
            if (curOpPrecedence != null && curOpPrecedence >= precedence) {
                advance();
                left = parseMiddleAndAdvance(left);
            } else if (curOpPrecedence != null || isEnd() || cur.getType() == TokenType.SEMI ||
                    cur.getType() == TokenType.RPAREN || cur.getType() == TokenType.RBRACE ||
                    // list literal
                    cur.getType() == TokenType.RBRACK ||
                    // expression in argument list, list literal etc.
                    cur.getType() == TokenType.COMMA ||
                    // ?:
                    cur.getType() == TokenType.COLON) {
                break;
            } else {
                throw QLException.reportParserErr(scanner.getScript(), lastToken(), "INVALID_EXPRESSION",
                        "invalid expression");
            }
        }
        return left;
    }

    private void unionOp() {
        if (cur == null) {
            return;
        }
        if (cur.getType() == TokenType.LT) {
            Token ltLt = scanner.lookAhead();
            scanner.back();
            if (isTokenType(ltLt, TokenType.LT) && ltLt.getPos() == cur.getPos() + 1) {
                scanner.next();
                cur = new Token(TokenType.LSHIFT, "<<", ltLt.getPos(), ltLt.getLine(), ltLt.getCol());
            }
        } else if (cur.getType() == TokenType.GT) {
            Token gtGt = scanner.lookAhead();
            if (isTokenType(gtGt, TokenType.GT) && gtGt.getPos() == cur.getPos() + 1) {
                Token gtGtGt = scanner.lookAhead();
                scanner.back();
                if (isTokenType(gtGtGt, TokenType.GT) && gtGtGt.getPos() == gtGt.getPos() + 1) {
                    scanner.next();
                    scanner.next();
                    cur = new Token(TokenType.URSHIFT, ">>>", gtGtGt.getPos(), gtGtGt.getLine(),
                            gtGtGt.getCol());
                } else {
                    scanner.next();
                    cur = new Token(TokenType.RSHIFT, ">>", gtGt.getPos(), gtGt.getLine(), gtGt.getCol());
                }
            } else {
                scanner.back();
            }
        } else if (cur.getType() == TokenType.COLON) {
            Token colonColon = scanner.lookAhead();
            scanner.back();
            if (isTokenType(colonColon, TokenType.COLON) && colonColon.getPos() == cur.getPos() + 1) {
                // method reference
                scanner.next();
                cur = new Token(TokenType.METHOD_REF, "::", colonColon.getPos(),
                        colonColon.getLine(), colonColon.getCol());
            }
        }
    }

    private Expr parseMiddleAndAdvance(Expr left) {
        if (pre.getType() == TokenType.LPAREN) {
            Token lookAheadToken = lookAheadRParenNextToken(true);
            if (lookAheadToken != null && lookAheadToken.getType() == TokenType.ARROW) {
                if (!(left instanceof GroupExpr)) {
                    // only with a force cast, group expr allow in middle
                    throw QLException.reportParserErr(scanner.getScript(), pre,
                            "INVALID_EXPRESSION", "invalid expression");
                }
                // lambda expression
                LambdaExpr lambdaExpr = lambdaExpr();
                return new CallExpr(left.getKeyToken(), left, Collections.singletonList(lambdaExpr));
            }
            Token keyToken = pre;
            List<Expr> arguments = argumentList();
            return new CallExpr(keyToken, left, arguments);
        } else if (pre.getType() == TokenType.LBRACK) {
            Token keyToken = pre;
            Expr indexExpr = expr();
            advanceOrReportErrorWithToken(TokenType.RBRACK, "CAN_NOT_FIND_RBRACK_TO_MATCH",
                    "can not find ']' to match", keyToken);
            return new ArrayCallExpr(keyToken, left, indexExpr);
        } else if (pre.getType() == TokenType.DOT || pre.getType() == TokenType.METHOD_REF) {
            // field call
            if (matchTypeAndAdvance(TokenType.ID)) {
                return new FieldCallExpr(pre, left, new Identifier(pre));
            }
            throw QLException.reportParserErr(scanner.getScript(),
                    lastToken(), "INVALID_FIELD", "invalid field");
        } else if (pre.getType() == TokenType.INC || pre.getType() == TokenType.DEC) {
            // suffix operator
            return new SuffixUnaryOpExpr(pre, left);
        } else if (isAssignOperator(pre)) {
            // assign operator is right-associative
            Token keyToken = pre;
            if (!canAssign(left)) {
                throw QLException.reportParserErr(scanner.getScript(),
                        keyToken, "INVALID_ASSIGN_TARGET", "invalid assign target");
            }
            Expr rightExpr = parsePrecedence(QLPrecedences.ASSIGN);
            return new AssignExpr(keyToken, left, rightExpr);
        } else if (pre.getType() == TokenType.QUESTION) {
            // ?:
            Token keyToken = pre;
            Expr thenExpr = parsePrecedence(QLPrecedences.TERNARY);
            advanceOrReportErrorWithToken(TokenType.COLON, "CAN_NOT_FIND_COLON_TO_MATCH_QUESTION",
                    "can not find ':' to match '?'", keyToken);
            Expr elseExpr = parsePrecedence(QLPrecedences.TERNARY);
            return new TernaryExpr(keyToken, left, thenExpr, elseExpr);
        } else if (getMiddleOpPrecedence(pre) != null) {
            return new BinaryOpExpr(pre, left, parsePrecedence(getMiddleOpPrecedence(pre) + 1));
        } else {
            throw QLException.reportParserErr(scanner.getScript(),
                    pre, "UNKNOWN_MIDDLE_OPERATOR", "unknown middle operator");
        }
    }

    private boolean isAssignOperator(Token opToken) {
        switch (opToken.getType()) {
            case ASSIGN:
            case ADD_ASSIGN:
            case SUB_ASSIGN:
            case AND_ASSIGN:
            case OR_ASSIGN:
            case MUL_ASSIGN:
            case MOD_ASSIGN:
            case LSHIFT_ASSIGN:
            case RSHIFT_ASSIGN:
            case URSHIFT_ASSIGN:
            case DIV_ASSIGN:
                return true;
            default:
                return false;
        }
    }

    private Integer getMiddleOpPrecedence(Token opToken) {
        Integer opPrecedence = QLPrecedences.getMiddlePrecedence(opToken);
        if (opPrecedence == null) {
            opPrecedence = userDefineOperatorsPrecedence.get(opToken.getLexeme());
        }
        return opPrecedence;
    }

    protected List<Expr> argumentList() {
        Token lParen = pre;
        List<Expr> arguments = new ArrayList<>();
        while (!matchTypeAndAdvance(TokenType.RPAREN)) {
            if (isEnd()) {
                throw QLException.reportParserErr(scanner.getScript(), lParen,
                        "CAN_NOT_FIND_RPAREN_TO_MATCH", "can not find ')' to match it");
            }
            if (!arguments.isEmpty()) {
                advanceOrReportError(TokenType.COMMA, "EXPECT_COMMA_BETWEEN_ARGUMENTS",
                        "expect ',' between arguments");
            }
            arguments.add(expr());
        }
        return arguments;
    }

    private Integer getCurOpPrecedence() {
        if (isEnd()) {
            return null;
        }
        return getMiddleOpPrecedence(cur);
    }

    protected Token lastToken() {
        return isEnd()? pre: cur;
    }

    private boolean isTokenType(Token token, TokenType type) {
        return token != null && token.getType() == type;
    }
}