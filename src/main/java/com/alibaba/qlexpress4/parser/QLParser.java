package com.alibaba.qlexpress4.parser;

import com.alibaba.qlexpress4.QLPrecedences;
import com.alibaba.qlexpress4.exception.QLSyntaxException;
import com.alibaba.qlexpress4.exception.ReportTemplate;
import com.alibaba.qlexpress4.parser.tree.AssignExpr;
import com.alibaba.qlexpress4.parser.tree.BinaryOpExpr;
import com.alibaba.qlexpress4.parser.tree.Block;
import com.alibaba.qlexpress4.parser.tree.Break;
import com.alibaba.qlexpress4.parser.tree.Continue;
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

    /*
     * left parent Token position -> token just after right parent token , Optional.empty() when without next token
     */
    private final Map<Integer, Optional<Token>> parenNextToken = new HashMap<>();

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
        while (!isEnd()) {
            stmtList.add(statement());
        }

        return new Program(stmtList);
    }

    public String getScript() {
        return scanner.getScript();
    }

    private Stmt statement() {
        if (matchTypeAndAdvance(TokenType.LBRACE)) {
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
            return importStmt();
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
        if (cur.getType() == TokenType.ID || cur.getType() == TokenType.TYPE) {
            Token ahead1 = scanner.lookAhead();
            if (ahead1 != null && ahead1.getType() == TokenType.ID) {
                Token ahead2 = scanner.lookAhead();
                if (ahead2.getType() == TokenType.SEMI || ahead2.getType() == TokenType.ASSIGN) {
                    scanner.back();
                    return true;
                }
            }
        }
        scanner.back();
        return false;
    }

    private LocalVarDeclareStmt localVarDeclareStmt() {
        // first three token has been determined
        Token keyToken = cur;
        Identifier type = new Identifier(cur);
        advance();
        Identifier varName = new Identifier(cur);
        advance();

        if (matchTypeAndAdvance(TokenType.SEMI)) {
            return new LocalVarDeclareStmt(keyToken, new VarDecl(type, varName), null);
        } else if (matchTypeAndAdvance(TokenType.ASSIGN)) {
            Expr expr = expr();
            advanceOrReportError(TokenType.SEMI, "expect ';' in the end of statement");
            return new LocalVarDeclareStmt(keyToken, new VarDecl(type, varName), expr);
        }
        throw new QLSyntaxException(ReportTemplate.report(scanner.getScript(), keyToken,
                "invalid variable declaration statement"));
    }

    private Stmt returnStmt() {
        Token keyToken = pre;
        if (matchTypeAndAdvance(TokenType.SEMI)) {
            return new Return(keyToken, null);
        }

        Expr expr = expr();
        advanceOrReportError(TokenType.SEMI, "return statement must end with ';'");
        return new Return(keyToken, expr);
    }

    private Stmt singleTokenStmt() {
        Token keyToken = pre;
        advanceOrReportError(TokenType.SEMI, "statement must end with ';'");
        switch (keyToken.getLexeme()) {
            case KeyWordsSet.BREAK:
                return new Break(keyToken);
            case KeyWordsSet.CONTINUE:
                return new Continue(keyToken);
        }
        // never should run there
        throw new QLSyntaxException(ReportTemplate.report(scanner.getScript(), keyToken,
                "unknown statement"));
    }

    private MacroStmt macroStmt() {
        Identifier macroName = idOrReportError("invalid macro name");
        advanceOrReportError(TokenType.LBRACE, "expect '{' in macro declaration");
        Block body = block();
        return new MacroStmt(macroName.getKeyToken(), macroName, body);
    }

    private ImportStmt importStmt() {
        Token importToken = pre;
        Token prePackToken = null;
        StringBuilder path = new StringBuilder();

        while (!isEnd()) {
            if (matchTypeAndAdvance(TokenType.MUL)) {
                advanceOrReportError(TokenType.SEMI, "import statement must end with ';'");
                if (prePackToken == null) {
                    throw new QLSyntaxException(ReportTemplate.report(
                            scanner.getScript(), importToken, "invalid import"
                    ));
                }
                return new ImportStmt(prePackToken, ImportStmt.ImportType.PREFIX, path.toString());
            } else if (matchTypeAndAdvance(TokenType.ID)) {
                if (prePackToken != null) {
                    path.append('.');
                }
                prePackToken = pre;
                path.append(prePackToken.getLexeme());
                if (matchTypeAndAdvance(TokenType.SEMI)) {
                    return new ImportStmt(prePackToken, ImportStmt.ImportType.FIXED, path.toString());
                } else if (!matchTypeAndAdvance(TokenType.DOT)) {
                    throw new QLSyntaxException(ReportTemplate.report(
                            scanner.getScript(), pre, "import must end with ';'"
                    ));
                }
            } else {
                throw new QLSyntaxException(ReportTemplate.report(
                        scanner.getScript(), cur, "import must end with ';'"
                ));
            }
        }

        throw new QLSyntaxException(ReportTemplate.report(
                scanner.getScript(), importToken, "invalid import"
        ));
    }

    private FunctionStmt functionStmt() {
        Identifier functionName = idOrReportError("invalid function name");

        advanceOrReportError(TokenType.LPAREN, "expect '(' in parameter list");
        List<VarDecl> paramList = parameterList();

        advanceOrReportError(TokenType.LBRACE, "expect '{' in function declaration");
        Block block = block();

        return new FunctionStmt(functionName.getKeyToken(), functionName, paramList, block);
    }

    protected List<VarDecl> parameterList() {
        Token lParen = pre;
        List<VarDecl> parameterList = new ArrayList<>();
        while (!isEnd()) {
            Identifier maybeType;
            if (matchTypeAndAdvance(TokenType.TYPE) || matchTypeAndAdvance(TokenType.ID)) {
                maybeType = new Identifier(pre);
            } else {
                throw new QLSyntaxException(ReportTemplate.report(scanner.getScript(),
                        cur, "invalid function param type declaration"));
            }

            if (matchTypeAndAdvance(TokenType.COMMA) || matchTypeAndAdvance(TokenType.RPAREN)) {
                // parameter without type
                if (maybeType.getKeyToken().getType() == TokenType.TYPE) {
                    throw new QLSyntaxException(ReportTemplate.report(scanner.getScript(),
                            maybeType.getKeyToken(), "invalid parameter name"));
                }
                parameterList.add(new VarDecl(null, maybeType));
                if (pre.getType() == TokenType.RPAREN) {
                    break;
                }
            }

            Identifier paramName;
            if (matchTypeAndAdvance(TokenType.ID)) {
                paramName = new Identifier(pre);
            } else if (isEnd()) {
                throw new QLSyntaxException(ReportTemplate.report(scanner.getScript(),
                        lParen, "incomplete parameter list, miss ')'"));
            } else {
                throw new QLSyntaxException(ReportTemplate.report(scanner.getScript(),
                        cur, "invalid parameter name"));
            }

            if (matchTypeAndAdvance(TokenType.COMMA) || matchTypeAndAdvance(TokenType.RPAREN)) {
                parameterList.add(new VarDecl(maybeType, paramName));
                if (pre.getType() == TokenType.RPAREN) {
                    break;
                }
            }
        }

        if (isEnd()) {
            throw new QLSyntaxException(ReportTemplate.report(scanner.getScript(),
                    lParen, "can not find ')' to match it"));
        }

        return parameterList;
    }

    protected Block block() {
        Token keyToken = pre;
        List<Stmt> stmtList = new ArrayList<>();
        while (!matchTypeAndAdvance(TokenType.RBRACE) && !isEnd()) {
            stmtList.add(statement());
        }
        return new Block(keyToken, stmtList);
    }

    private IfStmt ifStmt() {
        Token keyToken = pre;
        advanceOrReportError(TokenType.LPAREN, "expect '(' in if statement");
        Expr condition = expr();
        advanceOrReportError(TokenType.RPAREN, "expect ')' in if statement");

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

        advanceOrReportError(TokenType.LPAREN, "expect '(' in while statement");
        Expr condition = expr();
        advanceOrReportError(TokenType.RPAREN, "expect ')' in while statement");

        Stmt body = statement();
        return new WhileStmt(keyToken, condition, body);
    }

    private Stmt forStmt() {
        Token forToken = pre;
        advanceOrReportError(TokenType.LPAREN, "expect '(' in for statement");
        return isForEach()? forEachStmt(forToken): traditionalForStmt(forToken);
    }

    private ForEachStmt forEachStmt(Token forToken) {
        VarDecl itVar;
        if (matchTypeAndAdvance(TokenType.TYPE) || matchTypeAndAdvance(TokenType.ID)) {
            Token maybeType = pre;
            if (matchTypeAndAdvance(TokenType.COLON)) {
                itVar = new VarDecl(null, new Identifier(maybeType));
            } else if (matchTypeAndAdvance(TokenType.ID)) {
                itVar = new VarDecl(new Identifier(maybeType), new Identifier(pre));
                advanceOrReportError(TokenType.COLON, "expect ':' in for-each statement");
            } else {
                throw new QLSyntaxException(ReportTemplate.report(scanner.getScript(),
                        lastToken(), "invalid for each variable declare"));
            }
        } else {
            throw new QLSyntaxException(ReportTemplate.report(scanner.getScript(),
                    lastToken(), "invalid for each variable declare"));
        }

        Expr target = expr();

        advanceOrReportError(TokenType.RPAREN, "expect ')' in for-each statement");

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
                advanceOrReportError(TokenType.SEMI, "expect ';' in for statement");
            }
        }
        Expr condition = null;
        if (!matchTypeAndAdvance(TokenType.SEMI)) {
            condition = expr();
            advanceOrReportError(TokenType.SEMI, "expect ';' in for statement");
        }
        Expr forUpdate = null;
        if (!matchTypeAndAdvance(TokenType.SEMI)) {
            forUpdate = expr();
        }
        advanceOrReportError(TokenType.RPAREN, "expect ')' in for statement");

        Stmt body = statement();
        return new ForStmt(forToken, forInit, condition, forUpdate, body);
    }

    private boolean isForEach() {
        boolean result = false;
        for (int i = 0; i < 3; i++) {
            Token l1 = scanner.lookAhead();
            if (l1 == null) {
                break;
            }
            if (l1.getType() == TokenType.COLON) {
                result = true;
                break;
            }
        }
        scanner.back();
        return result;
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

    private Identifier idOrReportError(String reason) {
        if (cur.getType() != TokenType.ID) {
            throw new QLSyntaxException(ReportTemplate.report(scanner.getScript(), cur, reason));
        }
        Identifier id = new Identifier(cur);
        advance();
        return id;
    }

    protected void advanceOrReportError(TokenType expectType, String reason) {
        advanceOrReportErrorWithToken(expectType, reason, pre);
    }

    private void advanceOrReportErrorWithToken(TokenType expectType, String reason, Token reportToken) {
        if (isEnd() || !Objects.equals(cur.getType(), expectType)) {
            throw new QLSyntaxException(ReportTemplate.report(scanner.getScript(), reportToken, reason));
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
        return (leftExpr instanceof FieldCallExpr) || (leftExpr instanceof IdExpr);
    }

    enum GroupType {GROUP, LAMBDA, CAST}

    protected GroupType lookAheadGroupType() {
        Token rParenNextToken = lookAheadRParenNextToken(true);
        if (rParenNextToken == null || rParenNextToken.getType() == TokenType.ADD ||
                rParenNextToken.getType() == TokenType.SUB ||
                rParenNextToken.getType() == TokenType.INC ||
                rParenNextToken.getType() == TokenType.DEC) {
            // + - is prefix and middle operator in same time
            // so we need to handle expression like `(int) -1`
            if (cur != null && cur.getType() == TokenType.TYPE) {
                Token expectRParen = scanner.lookAhead();
                if (expectRParen != null && expectRParen.getType() == TokenType.RPAREN) {
                    scanner.back();
                    return GroupType.CAST;
                }
            }
            scanner.back();
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
                throw new QLSyntaxException(ReportTemplate.report(scanner.getScript(), lParenToken,
                        "can not find ')' to match"));
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
            } else if (!isEnd() && left instanceof GroupExpr && ParseRuleRegister.isPrefixToken(cur)) {
                // force cast
                Expr nextUnary = parsePrecedence(QLPrecedences.UNARY);
                left = new CallExpr(left.getKeyToken(), left, Collections.singletonList(nextUnary));
            } else {
                throw new QLSyntaxException(ReportTemplate.report(
                        scanner.getScript(), isEnd()? pre: cur, "invalid expression"
                ));
            }
        }
        return left;
    }

    private Expr parseMiddleAndAdvance(Expr left) {
        if (pre.getType() == TokenType.LPAREN) {
            Token lookAheadToken = lookAheadRParenNextToken(true);
            if (lookAheadToken != null && lookAheadToken.getType() == TokenType.ARROW) {
                if (!(left instanceof GroupExpr)) {
                    // only with a force cast, group expr allow in middle
                    throw new QLSyntaxException(ReportTemplate.report(scanner.getScript(), pre,
                            "invalid expression"));
                }
                // lambda expression
                LambdaExpr lambdaExpr = lambdaExpr();
                return new CallExpr(left.getKeyToken(), left, Collections.singletonList(lambdaExpr));
            }
            Token keyToken = pre;
            List<Expr> arguments = argumentList();
            return new CallExpr(keyToken, left, arguments);
        } else if (pre.getType() == TokenType.DOT) {
            // field call
            if (matchTypeAndAdvance(TokenType.ID)) {
                return new FieldCallExpr(pre, left, new Identifier(pre));
            }
            throw new QLSyntaxException(ReportTemplate.report(scanner.getScript(),
                    pre, "invalid field call"));
        } else if (pre.getType() == TokenType.INC || pre.getType() == TokenType.DEC) {
            // suffix operator
            return new SuffixUnaryOpExpr(pre, left);
        } else if (isAssignOperator(pre)) {
            // assign operator is right-associative
            Token keyToken = pre;
            if (!canAssign(left)) {
                throw new QLSyntaxException(ReportTemplate.report(scanner.getScript(),
                        keyToken, "invalid assign target"));
            }
            Expr rightExpr = parsePrecedence(QLPrecedences.ASSIGN);
            return new AssignExpr(keyToken, left, rightExpr);
        } else if (pre.getType() == TokenType.QUESTION) {
            // ?:
            Token keyToken = pre;
            Expr thenExpr = parsePrecedence(QLPrecedences.TERNARY);
            advanceOrReportErrorWithToken(TokenType.COLON, "can not find ':' to match '?'", keyToken);
            Expr elseExpr = parsePrecedence(QLPrecedences.TERNARY);
            return new TernaryExpr(keyToken, left, thenExpr, elseExpr);
        } else if (getMiddleOpPrecedence(pre) != null) {
            return new BinaryOpExpr(pre, left, parsePrecedence(getMiddleOpPrecedence(pre) + 1));
        } else {
            throw new QLSyntaxException(ReportTemplate.report(scanner.getScript(),
                    pre, "unknown middle operator"));
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
                throw new QLSyntaxException(ReportTemplate.report(scanner.getScript(), lParen,
                        "can not find ')' to match"));
            }
            if (!arguments.isEmpty()) {
                advanceOrReportError(TokenType.COMMA, "expect ',' between arguments");
            }
            arguments.add(expr());
        }
        if (pre.getType() != TokenType.RPAREN) {
            throw new QLSyntaxException(ReportTemplate.report(scanner.getScript(),
                    lParen, "can not find ')' to match"));
        }
        return arguments;
    }

    private Integer getCurOpPrecedence() {
        if (isEnd()) {
            return null;
        }
        return getMiddleOpPrecedence(cur);
    }

    private Token lastToken() {
        return isEnd()? pre: cur;
    }
}