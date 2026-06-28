package com.alibaba.qlexpress4.aparser;

import com.alibaba.qlexpress4.exception.QLErrorCodes;
import com.alibaba.qlexpress4.exception.QLException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.alibaba.qlexpress4.aparser.ParserOperatorManager.OpType.MIDDLE;
import static com.alibaba.qlexpress4.aparser.ParserOperatorManager.OpType.PREFIX;
import static com.alibaba.qlexpress4.aparser.ParserOperatorManager.OpType.SUFFIX;

public class QLParser {
    public static final int FOR = QLexer.FOR;
    
    public static final int IF = QLexer.IF;
    
    public static final int ELSE = QLexer.ELSE;
    
    public static final int WHILE = QLexer.WHILE;
    
    public static final int BREAK = QLexer.BREAK;
    
    public static final int CONTINUE = QLexer.CONTINUE;
    
    public static final int RETURN = QLexer.RETURN;
    
    public static final int FUNCTION = QLexer.FUNCTION;
    
    public static final int MACRO = QLexer.MACRO;
    
    public static final int IMPORT = QLexer.IMPORT;
    
    public static final int STATIC = QLexer.STATIC;
    
    public static final int NEW = QLexer.NEW;
    
    public static final int SWITCH = QLexer.SWITCH;
    
    public static final int CASE = QLexer.CASE;
    
    public static final int DEFAULT = QLexer.DEFAULT;
    
    public static final int BYTE = QLexer.BYTE;
    
    public static final int SHORT = QLexer.SHORT;
    
    public static final int INT = QLexer.INT;
    
    public static final int LONG = QLexer.LONG;
    
    public static final int FLOAT = QLexer.FLOAT;
    
    public static final int DOUBLE = QLexer.DOUBLE;
    
    public static final int CHAR = QLexer.CHAR;
    
    public static final int BOOL = QLexer.BOOL;
    
    public static final int NULL = QLexer.NULL;
    
    public static final int TRUE = QLexer.TRUE;
    
    public static final int FALSE = QLexer.FALSE;
    
    public static final int EXTENDS = QLexer.EXTENDS;
    
    public static final int SUPER = QLexer.SUPER;
    
    public static final int TRY = QLexer.TRY;
    
    public static final int CATCH = QLexer.CATCH;
    
    public static final int FINALLY = QLexer.FINALLY;
    
    public static final int THROW = QLexer.THROW;
    
    public static final int THEN = QLexer.THEN;
    
    public static final int CLASS = QLexer.CLASS;
    
    public static final int THIS = QLexer.THIS;
    
    public static final int QuoteStringLiteral = QLexer.QuoteStringLiteral;
    
    public static final int IntegerLiteral = QLexer.IntegerLiteral;
    
    public static final int FloatingPointLiteral = QLexer.FloatingPointLiteral;
    
    public static final int IntegerOrFloatingLiteral = QLexer.IntegerOrFloatingLiteral;
    
    public static final int LPAREN = QLexer.LPAREN;
    
    public static final int RPAREN = QLexer.RPAREN;
    
    public static final int LBRACE = QLexer.LBRACE;
    
    public static final int RBRACE = QLexer.RBRACE;
    
    public static final int LBRACK = QLexer.LBRACK;
    
    public static final int RBRACK = QLexer.RBRACK;
    
    public static final int DOT = QLexer.DOT;
    
    public static final int ARROW = QLexer.ARROW;
    
    public static final int SEMI = QLexer.SEMI;
    
    public static final int COMMA = QLexer.COMMA;
    
    public static final int QUESTION = QLexer.QUESTION;
    
    public static final int COLON = QLexer.COLON;
    
    public static final int DCOLON = QLexer.DCOLON;
    
    public static final int GT = QLexer.GT;
    
    public static final int LT = QLexer.LT;
    
    public static final int EQ = QLexer.EQ;
    
    public static final int NOEQ = QLexer.NOEQ;
    
    public static final int RIGHSHIFT_ASSGIN = QLexer.RIGHSHIFT_ASSGIN;
    
    public static final int RIGHSHIFT = QLexer.RIGHSHIFT;
    
    public static final int OPTIONAL_CHAINING = QLexer.OPTIONAL_CHAINING;
    
    public static final int SPREAD_CHAINING = QLexer.SPREAD_CHAINING;
    
    public static final int URSHIFT_ASSGIN = QLexer.URSHIFT_ASSGIN;
    
    public static final int URSHIFT = QLexer.URSHIFT;
    
    public static final int LSHIFT_ASSGIN = QLexer.LSHIFT_ASSGIN;
    
    public static final int LEFTSHIFT = QLexer.LEFTSHIFT;
    
    public static final int GE = QLexer.GE;
    
    public static final int LE = QLexer.LE;
    
    public static final int DOTMUL = QLexer.DOTMUL;
    
    public static final int CARET = QLexer.CARET;
    
    public static final int ADD_ASSIGN = QLexer.ADD_ASSIGN;
    
    public static final int SUB_ASSIGN = QLexer.SUB_ASSIGN;
    
    public static final int AND_ASSIGN = QLexer.AND_ASSIGN;
    
    public static final int OR_ASSIGN = QLexer.OR_ASSIGN;
    
    public static final int MUL_ASSIGN = QLexer.MUL_ASSIGN;
    
    public static final int MOD_ASSIGN = QLexer.MOD_ASSIGN;
    
    public static final int DIV_ASSIGN = QLexer.DIV_ASSIGN;
    
    public static final int XOR_ASSIGN = QLexer.XOR_ASSIGN;
    
    public static final int BANG = QLexer.BANG;
    
    public static final int TILDE = QLexer.TILDE;
    
    public static final int ADD = QLexer.ADD;
    
    public static final int SUB = QLexer.SUB;
    
    public static final int MUL = QLexer.MUL;
    
    public static final int DIV = QLexer.DIV;
    
    public static final int BIT_AND = QLexer.BIT_AND;
    
    public static final int BIT_OR = QLexer.BIT_OR;
    
    public static final int MOD = QLexer.MOD;
    
    public static final int INC = QLexer.INC;
    
    public static final int DEC = QLexer.DEC;
    
    public static final int NEWLINE = QLexer.NEWLINE;
    
    public static final int OPID = QLexer.OPID;
    
    public static final int SELECTOR_START = QLexer.SELECTOR_START;
    
    public static final int SELECTOR_END = QLexer.SELECTOR_END;
    
    public static final int ID = QLexer.ID;
    
    public static final int DOUBLE_QUOTE = QLexer.DOUBLE_QUOTE;
    
    public static final int StaticStringCharacters = QLexer.StaticStringCharacters;
    
    public static final int DyStrExprStart = QLexer.DyStrExprStart;
    
    public static final int DyStrText = QLexer.DyStrText;
    
    public static final int SelectorVariable_VANME = QLexer.SelectorVariable_VANME;
    
    private final String script;
    
    private final List<Token> tokens;
    
    private final ParserOperatorManager operatorManager;
    
    private final boolean strictNewLines;
    
    private int p;
    
    public QLParser(String script, List<Token> tokens, ParserOperatorManager operatorManager, boolean strictNewLines) {
        this.script = script == null ? "" : script;
        this.tokens = tokens;
        this.operatorManager = operatorManager;
        this.strictNewLines = strictNewLines;
    }
    
    public ProgramContext program() {
        ProgramContext ctx = new ProgramContext();
        skipNewlines();
        while (la(IMPORT)) {
            ctx.imports.add(parseImportDeclaration());
            ctx.addChild(ctx.imports.get(ctx.imports.size() - 1));
            skipNewlines();
        }
        if (!la(Token.EOF)) {
            ctx.blockStatements = parseBlockStatementsUntil(Token.EOF);
            ctx.addChild(ctx.blockStatements);
        }
        expect(Token.EOF, "<EOF>");
        return ctx;
    }
    
    private BlockStatementsContext parseBlockStatementsUntil(int endType) {
        BlockStatementsContext ctx = new BlockStatementsContext();
        while (!la(endType) && !la(Token.EOF)) {
            if (endType == RBRACE && la(RBRACE)) {
                break;
            }
            BlockStatementContext statement = parseBlockStatement();
            ctx.statements.add(statement);
            ctx.addChild(statement);
        }
        return ctx;
    }
    
    private BlockStatementContext parseBlockStatement() {
        if (la(NEWLINE) || la(SEMI)) {
            EmptyStatementContext ctx = new EmptyStatementContext();
            ctx.addToken(consume());
            return ctx;
        }
        if (la(IMPORT)) {
            throw syntax(lt(), "Import statement is not at the beginning of the file.");
        }
        if (la(THROW)) {
            ThrowStatementContext ctx = new ThrowStatementContext();
            ctx.throwToken = consumeNode(ctx);
            ctx.expression = parseExpression();
            ctx.addChild(ctx.expression);
            consumeNextStatement();
            return ctx;
        }
        if (la(WHILE)) {
            return parseWhileStatement();
        }
        if (la(FOR)) {
            return parseForStatement();
        }
        if (la(FUNCTION)) {
            return parseFunctionStatement();
        }
        if (la(MACRO)) {
            return parseMacroStatement();
        }
        LocalVariableDeclarationContext local = tryParseLocalVariableDeclaration();
        if (local != null) {
            LocalVariableDeclarationStatementContext ctx = new LocalVariableDeclarationStatementContext();
            ctx.localVariableDeclaration = local;
            ctx.addChild(local);
            ctx.addToken(expect(SEMI, "';'"));
            return ctx;
        }
        if (la(BREAK) || la(CONTINUE)) {
            BreakContinueStatementContext ctx = new BreakContinueStatementContext();
            Token token = consume();
            ctx.addToken(token);
            if (token.getType() == BREAK) {
                ctx.breakToken = new TerminalNode(token);
            }
            else {
                ctx.continueToken = new TerminalNode(token);
            }
            consumeNextStatement();
            return ctx;
        }
        if (la(RETURN)) {
            ReturnStatementContext ctx = new ReturnStatementContext();
            ctx.returnToken = consumeNode(ctx);
            if (!isNextStatementStart()) {
                ctx.expression = parseExpression();
                ctx.addChild(ctx.expression);
            }
            consumeNextStatement();
            return ctx;
        }
        ExpressionStatementContext ctx = new ExpressionStatementContext();
        ctx.expression = parseExpression();
        ctx.addChild(ctx.expression);
        consumeNextStatement();
        return ctx;
    }
    
    private WhileStatementContext parseWhileStatement() {
        WhileStatementContext ctx = new WhileStatementContext();
        ctx.whileToken = consumeNode(ctx);
        expectInto(ctx, LPAREN, "'('");
        skipNewlines();
        ctx.expression = parseExpression();
        ctx.addChild(ctx.expression);
        skipNewlines();
        expectInto(ctx, RPAREN, "')'");
        ctx.blockStatements = parseBracedBlock(ctx);
        return ctx;
    }
    
    private BlockStatementsContext parseBracedBlock(RuleContext owner) {
        expectInto(owner, LBRACE, "'{'");
        skipNewlines();
        BlockStatementsContext block = null;
        if (!la(RBRACE)) {
            block = parseBlockStatementsUntil(RBRACE);
            owner.addChild(block);
        }
        skipNewlines();
        expectInto(owner, RBRACE, "'}'");
        return block;
    }
    
    private BlockStatementsContext parseRequiredBracedBlock(RuleContext owner) {
        BlockStatementsContext block = parseBracedBlock(owner);
        return block;
    }
    
    private BlockStatementContext parseForStatement() {
        int save = p;
        consume();
        expect(LPAREN, "'('");
        boolean forEach = scanForEachHeader();
        p = save;
        if (forEach) {
            return parseForEachStatement();
        }
        return parseTraditionalForStatement();
    }
    
    private boolean scanForEachHeader() {
        int depth = 0;
        for (int i = p; i < tokens.size(); i++) {
            int type = tokens.get(i).getType();
            if (type == LPAREN || type == LBRACK || type == LBRACE) {
                depth++;
            }
            else if (type == RPAREN || type == RBRACK || type == RBRACE) {
                depth--;
                if (depth <= 0 && type == RPAREN) {
                    return false;
                }
            }
            else if (depth == 0 && type == SEMI) {
                return false;
            }
            else if (depth == 0 && type == COLON) {
                return true;
            }
        }
        return false;
    }
    
    private TraditionalForStatementContext parseTraditionalForStatement() {
        TraditionalForStatementContext ctx = new TraditionalForStatementContext();
        ctx.forToken = consumeNode(ctx);
        expectInto(ctx, LPAREN, "'('");
        skipNewlines();
        ctx.forInit = parseForInit();
        ctx.addChild(ctx.forInit);
        skipNewlines();
        if (!la(SEMI)) {
            ctx.forCondition = parseExpression();
            ctx.addChild(ctx.forCondition);
        }
        expectInto(ctx, SEMI, "';'");
        skipNewlines();
        if (!la(RPAREN)) {
            ctx.forUpdate = parseExpression();
            ctx.addChild(ctx.forUpdate);
        }
        skipNewlines();
        expectInto(ctx, RPAREN, "')'");
        ctx.blockStatements = parseRequiredBracedBlock(ctx);
        return ctx;
    }
    
    private ForInitContext parseForInit() {
        ForInitContext ctx = new ForInitContext();
        if (la(SEMI)) {
            consumeNode(ctx);
            return ctx;
        }
        LocalVariableDeclarationContext local = tryParseLocalVariableDeclaration();
        if (local != null) {
            ctx.localVariableDeclaration = local;
            ctx.addChild(local);
            expectInto(ctx, SEMI, "';'");
            return ctx;
        }
        ctx.expression = parseExpression();
        ctx.addChild(ctx.expression);
        expectInto(ctx, SEMI, "';'");
        return ctx;
    }
    
    private ForEachStatementContext parseForEachStatement() {
        ForEachStatementContext ctx = new ForEachStatementContext();
        ctx.forToken = consumeNode(ctx);
        expectInto(ctx, LPAREN, "'('");
        skipNewlines();
        if (!singleForEachVarBeforeColon()) {
            ctx.declType = parseDeclType();
            ctx.addChild(ctx.declType);
        }
        ctx.varId = parseVarId();
        ctx.addChild(ctx.varId);
        expectInto(ctx, COLON, "':'");
        ctx.expression = parseExpression();
        ctx.addChild(ctx.expression);
        skipNewlines();
        expectInto(ctx, RPAREN, "')'");
        ctx.blockStatements = parseRequiredBracedBlock(ctx);
        return ctx;
    }
    
    private FunctionStatementContext parseFunctionStatement() {
        FunctionStatementContext ctx = new FunctionStatementContext();
        ctx.functionToken = consumeNode(ctx);
        ctx.varId = parseVarId();
        ctx.addChild(ctx.varId);
        expectInto(ctx, LPAREN, "'('");
        skipNewlines();
        if (!la(RPAREN)) {
            ctx.params = parseFormalOrInferredParameterList();
            ctx.addChild(ctx.params);
        }
        skipNewlines();
        expectInto(ctx, RPAREN, "')'");
        ctx.blockStatements = parseRequiredBracedBlock(ctx);
        return ctx;
    }
    
    private MacroStatementContext parseMacroStatement() {
        MacroStatementContext ctx = new MacroStatementContext();
        ctx.macroToken = consumeNode(ctx);
        ctx.varId = parseVarId();
        ctx.addChild(ctx.varId);
        ctx.blockStatements = parseRequiredBracedBlock(ctx);
        return ctx;
    }
    
    private NonExpressionStatementContext parseNonExpressionStatement() {
        NonExpressionStatementContext ctx = new NonExpressionStatementContext();
        BlockStatementContext statement;
        if (la(NEWLINE) || la(SEMI) || la(THROW) || la(WHILE) || la(FOR) || la(FUNCTION) || la(MACRO) || la(BREAK)
            || la(CONTINUE) || la(RETURN)) {
            statement = parseBlockStatement();
        }
        else {
            LocalVariableDeclarationContext local = tryParseLocalVariableDeclaration();
            if (local == null) {
                throw syntax(lt(), "mismatched input '" + tokenText(lt()) + "' expecting statement");
            }
            LocalVariableDeclarationStatementContext localStmt = new LocalVariableDeclarationStatementContext();
            localStmt.localVariableDeclaration = local;
            localStmt.addChild(local);
            localStmt.addToken(expect(SEMI, "';'"));
            statement = localStmt;
        }
        ctx.statement = statement;
        ctx.addChild(statement);
        return ctx;
    }
    
    private LocalVariableDeclarationContext tryParseLocalVariableDeclaration() {
        int save = p;
        try {
            DeclTypeContext declType = parseDeclType();
            if (!isVarIdToken(lt().getType())) {
                p = save;
                return null;
            }
            if (isMiddleOperator(lt())) {
                p = save;
                return null;
            }
            LocalVariableDeclarationContext ctx = new LocalVariableDeclarationContext();
            ctx.declType = declType;
            ctx.addChild(declType);
            ctx.variableDeclaratorList = parseVariableDeclaratorList();
            ctx.addChild(ctx.variableDeclaratorList);
            return ctx;
        }
        catch (QLParseBacktrack e) {
            p = save;
            return null;
        }
    }
    
    private VariableDeclaratorListContext parseVariableDeclaratorList() {
        VariableDeclaratorListContext ctx = new VariableDeclaratorListContext();
        ctx.variables.add(parseVariableDeclarator());
        ctx.addChild(ctx.variables.get(0));
        skipNewlines();
        while (la(COMMA)) {
            consumeNode(ctx);
            skipNewlines();
            VariableDeclaratorContext variable = parseVariableDeclarator();
            ctx.variables.add(variable);
            ctx.addChild(variable);
            skipNewlines();
        }
        return ctx;
    }
    
    private VariableDeclaratorContext parseVariableDeclarator() {
        VariableDeclaratorContext ctx = new VariableDeclaratorContext();
        ctx.id = parseVariableDeclaratorId();
        ctx.addChild(ctx.id);
        if (la(EQ)) {
            consumeNode(ctx);
            skipNewlines();
            ctx.initializer = parseVariableInitializer();
            ctx.addChild(ctx.initializer);
        }
        return ctx;
    }
    
    private VariableDeclaratorIdContext parseVariableDeclaratorId() {
        VariableDeclaratorIdContext ctx = new VariableDeclaratorIdContext();
        ctx.varId = parseVarId();
        ctx.addChild(ctx.varId);
        if (la(LBRACK) && la(1, RBRACK)) {
            ctx.dims = parseDims();
            ctx.addChild(ctx.dims);
        }
        return ctx;
    }
    
    private VariableInitializerContext parseVariableInitializer() {
        VariableInitializerContext ctx = new VariableInitializerContext();
        if (la(LBRACE) && isArrayInitializerAhead()) {
            ctx.arrayInitializer = parseArrayInitializer();
            ctx.addChild(ctx.arrayInitializer);
        }
        else {
            ctx.expression = parseExpression();
            ctx.addChild(ctx.expression);
        }
        return ctx;
    }
    
    private boolean isArrayInitializerAhead() {
        return !isMapExprAhead();
    }
    
    private ArrayInitializerContext parseArrayInitializer() {
        ArrayInitializerContext ctx = new ArrayInitializerContext();
        expectInto(ctx, LBRACE, "'{'");
        skipNewlines();
        if (!la(RBRACE)) {
            ctx.initializers = parseVariableInitializerList();
            ctx.addChild(ctx.initializers);
        }
        skipNewlines();
        expectInto(ctx, RBRACE, "'}'");
        return ctx;
    }
    
    private VariableInitializerListContext parseVariableInitializerList() {
        VariableInitializerListContext ctx = new VariableInitializerListContext();
        ctx.initializers.add(parseVariableInitializer());
        ctx.addChild(ctx.initializers.get(0));
        skipNewlines();
        while (la(COMMA)) {
            consumeNode(ctx);
            skipNewlines();
            if (la(RBRACE)) {
                break;
            }
            VariableInitializerContext item = parseVariableInitializer();
            ctx.initializers.add(item);
            ctx.addChild(item);
            skipNewlines();
        }
        return ctx;
    }
    
    private ExpressionContext parseExpression() {
        int save = p;
        if (hasTopLevelAssignOperatorAhead()) {
            LeftHandSideContext left = tryParseLeftHandSide();
            if (left != null && isAssignOperator(lt().getType())) {
                ExpressionContext ctx = new ExpressionContext();
                ctx.left = left;
                ctx.addChild(left);
                ctx.assignOperator = parseAssignOperator();
                ctx.addChild(ctx.assignOperator);
                skipNewlines();
                ctx.expression = parseExpression();
                ctx.addChild(ctx.expression);
                return ctx;
            }
            p = save;
        }
        ExpressionContext ctx = new ExpressionContext();
        ctx.ternary = parseTernaryExpr();
        ctx.addChild(ctx.ternary);
        return ctx;
    }
    
    private LeftHandSideContext tryParseLeftHandSide() {
        if (!isVarIdToken(lt().getType())) {
            return null;
        }
        LeftHandSideContext ctx = new LeftHandSideContext();
        ctx.varId = parseVarId();
        ctx.addChild(ctx.varId);
        if (la(LPAREN)) {
            ctx.lparen = consumeNode(ctx);
            skipNewlines();
            if (!la(RPAREN)) {
                ctx.argumentList = parseArgumentList();
                ctx.addChild(ctx.argumentList);
            }
            skipNewlines();
            expectInto(ctx, RPAREN, "')'");
        }
        while (true) {
            int beforeNewlines = p;
            skipNewlines();
            PathPartContext part = tryParsePathPart();
            if (part == null) {
                p = beforeNewlines;
                break;
            }
            ctx.pathParts.add(part);
            ctx.addChild(part);
        }
        return ctx;
    }
    
    private AssignOperatorContext parseAssignOperator() {
        AssignOperatorContext ctx = new AssignOperatorContext();
        if (!isAssignOperator(lt().getType())) {
            throw syntax(lt(), "mismatched input '" + tokenText(lt()) + "' expecting assignment operator");
        }
        ctx.addToken(consume());
        return ctx;
    }
    
    private TernaryExprContext parseTernaryExpr() {
        TernaryExprContext ctx = new TernaryExprContext();
        ctx.condition = parseBaseExpr(1);
        ctx.addChild(ctx.condition);
        if (la(QUESTION)) {
            ctx.question = consumeNode(ctx);
            skipNewlines();
            ctx.thenExpr = parseBaseExpr(0);
            ctx.addChild(ctx.thenExpr);
            expectInto(ctx, COLON, "':'");
            skipNewlines();
            ctx.elseExpr = parseExpression();
            ctx.addChild(ctx.elseExpr);
        }
        return ctx;
    }
    
    private BaseExprContext parseBaseExpr(int minPrecedence) {
        BaseExprContext ctx = new BaseExprContext();
        ctx.primary = parsePrimary();
        ctx.addChild(ctx.primary);
        while (!la(Token.EOF) && (!strictNewLines || !la(NEWLINE)) && isMiddleOperator(lt())
            && precedence(lt()) >= minPrecedence) {
            LeftAssoContext leftAsso = new LeftAssoContext();
            leftAsso.binaryop = parseBinaryop();
            leftAsso.addChild(leftAsso.binaryop);
            skipNewlines();
            leftAsso.right = parseBaseExpr(precedence(leftAsso.binaryop.getStart()) + 1);
            leftAsso.addChild(leftAsso.right);
            ctx.leftAssos.add(leftAsso);
            ctx.addChild(leftAsso);
        }
        return ctx;
    }
    
    private BinaryopContext parseBinaryop() {
        if (!isMiddleOperator(lt())) {
            throw syntax(lt(), "mismatched input '" + tokenText(lt()) + "' expecting operator");
        }
        BinaryopContext ctx = new BinaryopContext();
        ctx.addToken(consume());
        return ctx;
    }
    
    private PrimaryContext parsePrimary() {
        PrimaryContext ctx = new PrimaryContext();
        if (isLambdaStart()) {
            ctx.nonPathable = parseLambdaExpr();
            ctx.addChild(ctx.nonPathable);
            return ctx;
        }
        if (la(IF)) {
            ctx.nonPathable = parseQlIf();
            ctx.addChild(ctx.nonPathable);
            return ctx;
        }
        if (la(SWITCH) && la(1, LPAREN)) {
            ctx.nonPathable = parseSwitchExpr();
            ctx.addChild(ctx.nonPathable);
            return ctx;
        }
        if (la(TRY)) {
            ctx.nonPathable = parseTryCatchExpr();
            ctx.addChild(ctx.nonPathable);
            return ctx;
        }
        if (isPrefixOperator(lt())) {
            ctx.prefix = new PrefixExpressContext();
            ctx.prefix.opId = parseOpId();
            ctx.prefix.addChild(ctx.prefix.opId);
            ctx.addChild(ctx.prefix);
        }
        ctx.pathable = parsePrimaryNoFixPathable();
        ctx.addChild(ctx.pathable);
        while (true) {
            int beforeNewlines = p;
            skipNewlines();
            PathPartContext part = tryParsePathPart();
            if (part == null) {
                p = beforeNewlines;
                break;
            }
            ctx.pathParts.add(part);
            ctx.addChild(part);
        }
        if (isSuffixOperator(lt())) {
            ctx.suffix = new SuffixExpressContext();
            ctx.suffix.opId = parseOpId();
            ctx.suffix.addChild(ctx.suffix.opId);
            ctx.addChild(ctx.suffix);
        }
        return ctx;
    }
    
    private PrimaryNoFixPathableContext parsePrimaryNoFixPathable() {
        if (isLiteralStart(lt().getType())) {
            ConstExprContext ctx = new ConstExprContext();
            ctx.literal = parseLiteral();
            ctx.addChild(ctx.literal);
            return ctx;
        }
        if (la(LPAREN)) {
            if (isCastStart()) {
                CastExprContext ctx = new CastExprContext();
                expectInto(ctx, LPAREN, "'('");
                skipNewlines();
                ctx.declType = parseDeclType();
                ctx.addChild(ctx.declType);
                skipNewlines();
                expectInto(ctx, RPAREN, "')'");
                ctx.primary = parsePrimary();
                ctx.addChild(ctx.primary);
                return ctx;
            }
            GroupExprContext ctx = new GroupExprContext();
            expectInto(ctx, LPAREN, "'('");
            skipNewlines();
            ctx.expression = parseExpression();
            ctx.addChild(ctx.expression);
            skipNewlines();
            expectInto(ctx, RPAREN, "')'");
            return ctx;
        }
        if (la(NEW)) {
            return parseNewExpr();
        }
        if (isPrimitiveType(lt().getType())) {
            TypeExprContext ctx = new TypeExprContext();
            ctx.primitiveType = parsePrimitiveType();
            ctx.addChild(ctx.primitiveType);
            return ctx;
        }
        if (la(LBRACK)) {
            return parseListExpr();
        }
        if (la(LBRACE)) {
            return isMapExprAhead() ? parseMapExpr() : parseBlockExpr();
        }
        if (la(SELECTOR_START)) {
            ContextSelectExprContext ctx = new ContextSelectExprContext();
            ctx.selectorStart = consumeNode(ctx);
            ctx.selectorVariable = new TerminalNode(expect(SelectorVariable_VANME, "selector variable"));
            ctx.addChild(ctx.selectorVariable);
            ctx.selectorEnd = new TerminalNode(expect(SELECTOR_END, "selector end"));
            ctx.addChild(ctx.selectorEnd);
            return ctx;
        }
        if (isVarIdToken(lt().getType())) {
            VarIdExprContext ctx = new VarIdExprContext();
            ctx.varId = parseVarId();
            ctx.addChild(ctx.varId);
            if (la(LPAREN)) {
                ctx.lparen = consumeNode(ctx);
                skipNewlines();
                if (!la(RPAREN)) {
                    ctx.argumentList = parseArgumentList();
                    ctx.addChild(ctx.argumentList);
                }
                skipNewlines();
                expectInto(ctx, RPAREN, "')'");
            }
            return ctx;
        }
        throw syntax(lt(), "mismatched input '" + tokenText(lt()) + "' expecting expression");
    }
    
    private boolean isCastStart() {
        int save = p;
        try {
            expect(LPAREN, "'('");
            skipNewlines();
            parseDeclType();
            skipNewlines();
            if (!la(RPAREN)) {
                p = save;
                return false;
            }
            consume();
            boolean result = isPrimaryStart(lt());
            p = save;
            return result;
        }
        catch (RuntimeException e) {
            p = save;
            return false;
        }
    }
    
    private PrimaryNoFixPathableContext parseNewExpr() {
        Token newToken = lt();
        consume();
        DeclTypeNoArrContext type = parseDeclTypeNoArr();
        if (la(LPAREN)) {
            NewObjExprContext ctx = new NewObjExprContext();
            ctx.newToken = new TerminalNode(newToken);
            ctx.addChild(ctx.newToken);
            if (type.clsType == null) {
                throw syntax(newToken, "primitive type can not be constructed");
            }
            ctx.varIds.addAll(type.clsType.varIds);
            for (VarIdContext varId : ctx.varIds) {
                ctx.addChild(varId);
            }
            expectInto(ctx, LPAREN, "'('");
            skipNewlines();
            if (!la(RPAREN)) {
                ctx.argumentList = parseArgumentList();
                ctx.addChild(ctx.argumentList);
            }
            skipNewlines();
            expectInto(ctx, RPAREN, "')'");
            return ctx;
        }
        if (la(LBRACK) && !la(1, RBRACK)) {
            NewEmptyArrExprContext ctx = new NewEmptyArrExprContext();
            ctx.newToken = new TerminalNode(newToken);
            ctx.addChild(ctx.newToken);
            ctx.declTypeNoArr = type;
            ctx.addChild(type);
            ctx.dimExprs = parseDimExprs();
            ctx.addChild(ctx.dimExprs);
            return ctx;
        }
        NewInitArrExprContext ctx = new NewInitArrExprContext();
        ctx.newToken = new TerminalNode(newToken);
        ctx.addChild(ctx.newToken);
        ctx.declTypeNoArr = type;
        ctx.addChild(type);
        ctx.dims = parseDims();
        ctx.addChild(ctx.dims);
        ctx.arrayInitializer = parseArrayInitializer();
        ctx.addChild(ctx.arrayInitializer);
        return ctx;
    }
    
    private ListExprContext parseListExpr() {
        ListExprContext ctx = new ListExprContext();
        expectInto(ctx, LBRACK, "'['");
        skipNewlines();
        if (!la(RBRACK)) {
            ctx.listItems = parseListItems();
            ctx.addChild(ctx.listItems);
        }
        skipNewlines();
        expectInto(ctx, RBRACK, "']'");
        return ctx;
    }
    
    private ListItemsContext parseListItems() {
        ListItemsContext ctx = new ListItemsContext();
        ctx.expressions.add(parseExpression());
        ctx.addChild(ctx.expressions.get(0));
        skipNewlines();
        while (la(COMMA)) {
            consumeNode(ctx);
            skipNewlines();
            if (la(RBRACK)) {
                break;
            }
            ExpressionContext item = parseExpression();
            ctx.expressions.add(item);
            ctx.addChild(item);
            skipNewlines();
        }
        return ctx;
    }
    
    private MapExprContext parseMapExpr() {
        MapExprContext ctx = new MapExprContext();
        expectInto(ctx, LBRACE, "'{'");
        skipNewlines();
        ctx.mapEntries = new MapEntriesContext();
        if (la(COLON)) {
            ctx.mapEntries.addToken(consume());
        }
        else {
            MapEntryContext entry = parseMapEntry();
            ctx.mapEntries.entries.add(entry);
            ctx.mapEntries.addChild(entry);
            skipNewlines();
            while (la(COMMA)) {
                consumeNode(ctx.mapEntries);
                skipNewlines();
                if (la(RBRACE)) {
                    break;
                }
                entry = parseMapEntry();
                ctx.mapEntries.entries.add(entry);
                ctx.mapEntries.addChild(entry);
                skipNewlines();
            }
        }
        ctx.addChild(ctx.mapEntries);
        skipNewlines();
        expectInto(ctx, RBRACE, "'}'");
        return ctx;
    }
    
    private MapEntryContext parseMapEntry() {
        MapEntryContext ctx = new MapEntryContext();
        ctx.mapKey = parseMapKey();
        ctx.addChild(ctx.mapKey);
        skipNewlines();
        expectInto(ctx, COLON, "':'");
        skipNewlines();
        if ("'@class'".equals(ctx.mapKey.getText()) && la(QuoteStringLiteral)) {
            ClsValueContext clsValue = new ClsValueContext();
            clsValue.quote = consumeNode(clsValue);
            ctx.mapValue = clsValue;
        }
        else {
            EValueContext eValue = new EValueContext();
            eValue.expression = parseExpression();
            eValue.addChild(eValue.expression);
            ctx.mapValue = eValue;
        }
        ctx.addChild(ctx.mapValue);
        return ctx;
    }
    
    private MapKeyContext parseMapKey() {
        if (la(QuoteStringLiteral)) {
            QuoteStringKeyContext ctx = new QuoteStringKeyContext();
            ctx.addToken(consume());
            return ctx;
        }
        if (la(DOUBLE_QUOTE)) {
            StringKeyContext ctx = new StringKeyContext();
            ctx.doubleQuoteString = parseDoubleQuoteStringLiteral();
            ctx.addChild(ctx.doubleQuoteString);
            return ctx;
        }
        if (isIdMapKey(lt().getType())) {
            IdKeyContext ctx = new IdKeyContext();
            ctx.addToken(consume());
            return ctx;
        }
        throw syntax(lt(), "mismatched input '" + tokenText(lt()) + "' expecting map key");
    }
    
    private BlockExprContext parseBlockExpr() {
        BlockExprContext ctx = new BlockExprContext();
        expectInto(ctx, LBRACE, "'{'");
        skipNewlines();
        if (!la(RBRACE)) {
            ctx.blockStatements = parseBlockStatementsUntil(RBRACE);
            ctx.addChild(ctx.blockStatements);
        }
        skipNewlines();
        expectInto(ctx, RBRACE, "'}'");
        return ctx;
    }
    
    private QlIfContext parseQlIf() {
        QlIfContext ctx = new QlIfContext();
        ctx.ifToken = consumeNode(ctx);
        expectInto(ctx, LPAREN, "'('");
        skipNewlines();
        ctx.condition = parseExpression();
        ctx.addChild(ctx.condition);
        skipNewlines();
        expectInto(ctx, RPAREN, "')'");
        skipNewlines();
        if (la(THEN)) {
            consumeNode(ctx);
            skipNewlines();
        }
        ctx.thenBody = parseThenBody();
        ctx.addChild(ctx.thenBody);
        int save = p;
        skipNewlines();
        if (la(ELSE)) {
            consumeNode(ctx);
            skipNewlines();
            ctx.elseBody = parseElseBody();
            ctx.addChild(ctx.elseBody);
        }
        else {
            p = save;
        }
        return ctx;
    }
    
    private ThenBodyContext parseThenBody() {
        ThenBodyContext ctx = new ThenBodyContext();
        if (la(LBRACE)) {
            ctx.lbrace = new TerminalNode(expect(LBRACE, "'{'"));
            ctx.addChild(ctx.lbrace);
            skipNewlines();
            if (!la(RBRACE)) {
                ctx.blockStatements = parseBlockStatementsUntil(RBRACE);
                ctx.addChild(ctx.blockStatements);
            }
            skipNewlines();
            expectInto(ctx, RBRACE, "'}'");
        }
        else if (isNonExpressionStatementStart()) {
            ctx.nonExpressionStatement = parseNonExpressionStatement();
            ctx.addChild(ctx.nonExpressionStatement);
        }
        else {
            ctx.expression = parseExpression();
            ctx.addChild(ctx.expression);
        }
        return ctx;
    }
    
    private ElseBodyContext parseElseBody() {
        ElseBodyContext ctx = new ElseBodyContext();
        if (la(LBRACE)) {
            ctx.lbrace = new TerminalNode(expect(LBRACE, "'{'"));
            ctx.addChild(ctx.lbrace);
            skipNewlines();
            if (!la(RBRACE)) {
                ctx.blockStatements = parseBlockStatementsUntil(RBRACE);
                ctx.addChild(ctx.blockStatements);
            }
            skipNewlines();
            expectInto(ctx, RBRACE, "'}'");
        }
        else if (la(IF)) {
            ctx.qlIf = parseQlIf();
            ctx.addChild(ctx.qlIf);
        }
        else if (isNonExpressionStatementStart()) {
            ctx.nonExpressionStatement = parseNonExpressionStatement();
            ctx.addChild(ctx.nonExpressionStatement);
        }
        else {
            ctx.expression = parseExpression();
            ctx.addChild(ctx.expression);
        }
        return ctx;
    }
    
    private SwitchExprContext parseSwitchExpr() {
        SwitchExprContext ctx = new SwitchExprContext();
        ctx.switchToken = consumeNode(ctx);
        expectInto(ctx, LPAREN, "'('");
        skipNewlines();
        ctx.expression = parseExpression();
        ctx.addChild(ctx.expression);
        skipNewlines();
        expectInto(ctx, RPAREN, "')'");
        expectInto(ctx, LBRACE, "'{'");
        skipNewlines();
        if (!la(RBRACE)) {
            ctx.groups = parseSwitchCaseGroups();
            ctx.addChild(ctx.groups);
        }
        skipNewlines();
        expectInto(ctx, RBRACE, "'}'");
        return ctx;
    }
    
    private SwitchCaseGroupsContext parseSwitchCaseGroups() {
        SwitchCaseGroupsContext ctx = new SwitchCaseGroupsContext();
        while (la(CASE) || la(DEFAULT)) {
            SwitchCaseGroupContext group = parseSwitchCaseGroup();
            ctx.groups.add(group);
            ctx.addChild(group);
            skipNewlines();
        }
        return ctx;
    }
    
    private SwitchCaseGroupContext parseSwitchCaseGroup() {
        int save = p;
        SwitchExpressionLabelContext exprLabel = tryParseSwitchExpressionLabel();
        if (exprLabel != null) {
            SwitchExprGroupContext ctx = new SwitchExprGroupContext();
            ctx.label = exprLabel;
            ctx.addChild(exprLabel);
            skipNewlines();
            ctx.expression = parseExpression();
            ctx.addChild(ctx.expression);
            skipNewlines();
            return ctx;
        }
        p = save;
        SwitchStatementGroupContext ctx = new SwitchStatementGroupContext();
        ctx.labels = parseSwitchLabels();
        ctx.addChild(ctx.labels);
        skipNewlines();
        if (!la(CASE) && !la(DEFAULT) && !la(RBRACE)) {
            ctx.blockStatements = parseBlockStatementsUntilSwitchGroupEnd();
            ctx.addChild(ctx.blockStatements);
        }
        skipNewlines();
        return ctx;
    }
    
    private BlockStatementsContext parseBlockStatementsUntilSwitchGroupEnd() {
        BlockStatementsContext ctx = new BlockStatementsContext();
        while (!la(CASE) && !la(DEFAULT) && !la(RBRACE) && !la(Token.EOF)) {
            BlockStatementContext statement = parseBlockStatement();
            ctx.statements.add(statement);
            ctx.addChild(statement);
        }
        return ctx;
    }
    
    private SwitchExpressionLabelContext tryParseSwitchExpressionLabel() {
        int save = p;
        try {
            SwitchExpressionLabelContext ctx = new SwitchExpressionLabelContext();
            if (la(CASE)) {
                ctx.caseToken = consumeNode(ctx);
                ctx.expressionList = parseExpressionListUntilArrow();
                ctx.addChild(ctx.expressionList);
                skipNewlines();
                expectInto(ctx, ARROW, "'->'");
            }
            else if (la(DEFAULT)) {
                ctx.defaultToken = consumeNode(ctx);
                skipNewlines();
                expectInto(ctx, ARROW, "'->'");
            }
            else {
                p = save;
                return null;
            }
            skipNewlines();
            return ctx;
        }
        catch (RuntimeException e) {
            p = save;
            return null;
        }
    }
    
    private SwitchLabelsContext parseSwitchLabels() {
        SwitchLabelsContext ctx = new SwitchLabelsContext();
        while (la(CASE) || la(DEFAULT)) {
            SwitchLabelContext label = new SwitchLabelContext();
            if (la(CASE)) {
                label.caseToken = consumeNode(label);
                label.expression = parseExpression();
                label.addChild(label.expression);
            }
            else {
                label.defaultToken = consumeNode(label);
            }
            expectInto(label, COLON, "':'");
            ctx.labels.add(label);
            ctx.addChild(label);
            skipNewlines();
        }
        return ctx;
    }
    
    private ExpressionListContext parseExpressionListUntilArrow() {
        ExpressionListContext ctx = new ExpressionListContext();
        ctx.expressions.add(parseExpression());
        ctx.addChild(ctx.expressions.get(0));
        skipNewlines();
        while (la(COMMA)) {
            consumeNode(ctx);
            skipNewlines();
            ExpressionContext expr = parseExpression();
            ctx.expressions.add(expr);
            ctx.addChild(expr);
            skipNewlines();
        }
        return ctx;
    }
    
    private TryCatchExprContext parseTryCatchExpr() {
        TryCatchExprContext ctx = new TryCatchExprContext();
        ctx.tryToken = consumeNode(ctx);
        ctx.blockStatements = parseRequiredBracedBlock(ctx);
        int save = p;
        skipNewlines();
        if (la(CATCH)) {
            ctx.tryCatches = new TryCatchesContext();
            while (la(CATCH)) {
                TryCatchContext catchCtx = parseTryCatch();
                ctx.tryCatches.catches.add(catchCtx);
                ctx.tryCatches.addChild(catchCtx);
                save = p;
                skipNewlines();
                if (!la(CATCH)) {
                    p = save;
                    break;
                }
            }
            ctx.addChild(ctx.tryCatches);
        }
        else {
            p = save;
        }
        save = p;
        skipNewlines();
        if (la(FINALLY)) {
            ctx.tryFinally = parseTryFinally();
            ctx.addChild(ctx.tryFinally);
        }
        else {
            p = save;
        }
        return ctx;
    }
    
    private TryCatchContext parseTryCatch() {
        TryCatchContext ctx = new TryCatchContext();
        ctx.catchToken = consumeNode(ctx);
        expectInto(ctx, LPAREN, "'('");
        ctx.catchParams = parseCatchParams();
        ctx.addChild(ctx.catchParams);
        expectInto(ctx, RPAREN, "')'");
        ctx.blockStatements = parseRequiredBracedBlock(ctx);
        return ctx;
    }
    
    private CatchParamsContext parseCatchParams() {
        CatchParamsContext ctx = new CatchParamsContext();
        if (singleVarBefore(RPAREN)) {
            ctx.varId = parseVarId();
            ctx.addChild(ctx.varId);
            return ctx;
        }
        ctx.declTypes.add(parseDeclType());
        ctx.addChild(ctx.declTypes.get(0));
        while (la(BIT_OR)) {
            consumeNode(ctx);
            DeclTypeContext declType = parseDeclType();
            ctx.declTypes.add(declType);
            ctx.addChild(declType);
        }
        ctx.varId = parseVarId();
        ctx.addChild(ctx.varId);
        return ctx;
    }
    
    private TryFinallyContext parseTryFinally() {
        TryFinallyContext ctx = new TryFinallyContext();
        ctx.finallyToken = consumeNode(ctx);
        ctx.blockStatements = parseRequiredBracedBlock(ctx);
        return ctx;
    }
    
    private LambdaExprContext parseLambdaExpr() {
        LambdaExprContext ctx = new LambdaExprContext();
        ctx.lambdaParameters = parseLambdaParameters();
        ctx.addChild(ctx.lambdaParameters);
        ctx.arrow = new TerminalNode(expect(ARROW, "'->'"));
        ctx.addChild(ctx.arrow);
        skipNewlines();
        if (la(LBRACE) && !isMapExprAhead()) {
            expectInto(ctx, LBRACE, "'{'");
            skipNewlines();
            if (!la(RBRACE)) {
                ctx.blockStatements = parseBlockStatementsUntil(RBRACE);
                ctx.addChild(ctx.blockStatements);
            }
            skipNewlines();
            expectInto(ctx, RBRACE, "'}'");
        }
        else {
            ctx.expression = parseExpression();
            ctx.addChild(ctx.expression);
        }
        return ctx;
    }
    
    private LambdaParametersContext parseLambdaParameters() {
        LambdaParametersContext ctx = new LambdaParametersContext();
        if (isVarIdToken(lt().getType()) && la(1, ARROW)) {
            ctx.varId = parseVarId();
            ctx.addChild(ctx.varId);
            return ctx;
        }
        expectInto(ctx, LPAREN, "'('");
        if (!la(RPAREN)) {
            ctx.params = parseFormalOrInferredParameterList();
            ctx.addChild(ctx.params);
        }
        expectInto(ctx, RPAREN, "')'");
        return ctx;
    }
    
    private FormalOrInferredParameterListContext parseFormalOrInferredParameterList() {
        FormalOrInferredParameterListContext ctx = new FormalOrInferredParameterListContext();
        FormalOrInferredParameterContext param = parseFormalOrInferredParameter();
        ctx.params.add(param);
        ctx.addChild(param);
        skipNewlines();
        while (la(COMMA)) {
            consumeNode(ctx);
            skipNewlines();
            param = parseFormalOrInferredParameter();
            ctx.params.add(param);
            ctx.addChild(param);
            skipNewlines();
        }
        return ctx;
    }
    
    private FormalOrInferredParameterContext parseFormalOrInferredParameter() {
        FormalOrInferredParameterContext ctx = new FormalOrInferredParameterContext();
        if (!singleVarBefore(COMMA, RPAREN)) {
            int save = p;
            try {
                DeclTypeContext declType = parseDeclType();
                if (isVarIdToken(lt().getType())) {
                    ctx.declType = declType;
                    ctx.addChild(declType);
                }
                else {
                    p = save;
                }
            }
            catch (RuntimeException e) {
                p = save;
            }
        }
        ctx.varId = parseVarId();
        ctx.addChild(ctx.varId);
        return ctx;
    }
    
    private PathPartContext tryParsePathPart() {
        if (la(DOT)) {
            Token dot = consume();
            if (isVarIdToken(lt().getType()) && la(1, LPAREN)) {
                MethodInvokeContext ctx = new MethodInvokeContext();
                ctx.addToken(dot);
                ctx.varId = parseVarId();
                ctx.addChild(ctx.varId);
                parseMethodArguments(ctx);
                return ctx;
            }
            FieldAccessContext ctx = new FieldAccessContext();
            ctx.addToken(dot);
            ctx.fieldId = parseFieldId();
            ctx.addChild(ctx.fieldId);
            return ctx;
        }
        if (la(OPTIONAL_CHAINING) || la(SPREAD_CHAINING)) {
            boolean optional = la(OPTIONAL_CHAINING);
            Token token = consume();
            if (isVarIdToken(lt().getType()) && la(1, LPAREN)) {
                MethodInvokeContext base =
                    optional ? new OptionalMethodInvokeContext() : new SpreadMethodInvokeContext();
                base.addToken(token);
                base.varId = parseVarId();
                base.addChild(base.varId);
                parseMethodArguments(base);
                return base;
            }
            FieldAccessContext base = optional ? new OptionalFieldAccessContext() : new SpreadFieldAccessContext();
            base.addToken(token);
            base.fieldId = parseFieldId();
            base.addChild(base.fieldId);
            return base;
        }
        if (la(DCOLON)) {
            MethodAccessContext ctx = new MethodAccessContext();
            ctx.dcolon = consumeNode(ctx);
            ctx.varId = parseVarId();
            ctx.addChild(ctx.varId);
            return ctx;
        }
        if (la(LBRACK)) {
            IndexExprContext ctx = new IndexExprContext();
            expectInto(ctx, LBRACK, "'['");
            skipNewlines();
            if (!la(RBRACK)) {
                ctx.indexValueExpr = parseIndexValueExpr();
                ctx.addChild(ctx.indexValueExpr);
            }
            skipNewlines();
            expectInto(ctx, RBRACK, "']'");
            return ctx;
        }
        if (isGroupOperator(lt())) {
            CustomPathContext ctx = new CustomPathContext();
            ctx.opId = parseOpId();
            ctx.addChild(ctx.opId);
            skipNewlines();
            if (isVarIdToken(lt().getType())) {
                ctx.varId = parseVarId();
                ctx.pathText = ctx.varId.getText();
                ctx.addChild(ctx.varId);
            }
            else if (la(QuoteStringLiteral)) {
                TerminalNode quote = new TerminalNode(consume());
                ctx.pathText = quote.getText().substring(1, quote.getText().length() - 1);
                ctx.addChild(quote);
            }
            else {
                throw syntax(lt(), "mismatched input '" + tokenText(lt()) + "' expecting custom path");
            }
            return ctx;
        }
        return null;
    }
    
    private void parseMethodArguments(MethodInvokeContext ctx) {
        expectInto(ctx, LPAREN, "'('");
        skipNewlines();
        if (!la(RPAREN)) {
            ctx.argumentList = parseArgumentList();
            ctx.addChild(ctx.argumentList);
        }
        skipNewlines();
        expectInto(ctx, RPAREN, "')'");
    }
    
    private FieldIdContext parseFieldId() {
        FieldIdContext ctx = new FieldIdContext();
        if (isVarIdToken(lt().getType()) || la(CLASS)) {
            ctx.addToken(consume());
            return ctx;
        }
        if (la(QuoteStringLiteral)) {
            ctx.quote = consumeNode(ctx);
            return ctx;
        }
        throw syntax(lt(), "mismatched input '" + tokenText(lt()) + "' expecting field");
    }
    
    private IndexValueExprContext parseIndexValueExpr() {
        if (la(COLON)) {
            SliceIndexContext ctx = new SliceIndexContext();
            consumeNode(ctx);
            skipNewlines();
            if (!la(RBRACK)) {
                ctx.end = parseExpression();
                ctx.addChild(ctx.end);
            }
            return ctx;
        }
        ExpressionContext first = parseExpression();
        skipNewlines();
        if (la(COLON)) {
            SliceIndexContext ctx = new SliceIndexContext();
            ctx.start = first;
            ctx.addChild(first);
            consumeNode(ctx);
            skipNewlines();
            if (!la(RBRACK)) {
                ctx.end = parseExpression();
                ctx.addChild(ctx.end);
            }
            return ctx;
        }
        SingleIndexContext ctx = new SingleIndexContext();
        ctx.expression = first;
        ctx.addChild(first);
        return ctx;
    }
    
    private ArgumentListContext parseArgumentList() {
        ArgumentListContext ctx = new ArgumentListContext();
        ctx.expressions.add(parseExpression());
        ctx.addChild(ctx.expressions.get(0));
        skipNewlines();
        while (la(COMMA)) {
            consumeNode(ctx);
            skipNewlines();
            ExpressionContext expression = parseExpression();
            ctx.expressions.add(expression);
            ctx.addChild(expression);
            skipNewlines();
        }
        return ctx;
    }
    
    private LiteralContext parseLiteral() {
        LiteralContext ctx = new LiteralContext();
        if (la(IntegerLiteral) || la(FloatingPointLiteral) || la(IntegerOrFloatingLiteral) || la(QuoteStringLiteral)
            || la(NULL)) {
            ctx.addToken(consume());
            return ctx;
        }
        if (la(TRUE) || la(FALSE)) {
            ctx.boolen = new BoolenLiteralContext();
            ctx.boolen.addToken(consume());
            ctx.addChild(ctx.boolen);
            return ctx;
        }
        if (la(DOUBLE_QUOTE)) {
            ctx.doubleQuoteString = parseDoubleQuoteStringLiteral();
            ctx.addChild(ctx.doubleQuoteString);
            return ctx;
        }
        throw syntax(lt(), "mismatched input '" + tokenText(lt()) + "' expecting literal");
    }
    
    private DoubleQuoteStringLiteralContext parseDoubleQuoteStringLiteral() {
        DoubleQuoteStringLiteralContext ctx = new DoubleQuoteStringLiteralContext();
        expectInto(ctx, DOUBLE_QUOTE, "'\"'");
        if (la(StaticStringCharacters)) {
            ctx.staticCharacters = consumeNode(ctx);
        }
        while (!la(DOUBLE_QUOTE) && !la(Token.EOF)) {
            if (la(DyStrText)) {
                ctx.addToken(consume());
            }
            else if (la(DyStrExprStart)) {
                StringExpressionContext expr = new StringExpressionContext();
                expr.start = consumeNode(expr);
                if (la(SelectorVariable_VANME)) {
                    expr.selectorVariable = consumeNode(expr);
                }
                else {
                    skipNewlines();
                    expr.expression = parseExpression();
                    expr.addChild(expr.expression);
                    skipNewlines();
                    expectInto(expr, RBRACE, "'}'");
                }
                ctx.addChild(expr);
            }
            else {
                throw syntax(lt(), "mismatched input '" + tokenText(lt()) + "' expecting string content");
            }
        }
        expectInto(ctx, DOUBLE_QUOTE, "'\"'");
        return ctx;
    }
    
    private ImportDeclarationContext parseImportDeclaration() {
        Token importToken = expect(IMPORT, "'import'");
        List<VarIdContext> ids = new ArrayList<>();
        VarIdContext first = parseVarId();
        ids.add(first);
        while (la(DOT) && !la(1, MUL) && !la(1, Token.EOF)) {
            consume();
            ids.add(parseVarId());
        }
        ImportDeclarationContext ctx;
        if (la(DOTMUL) || (la(DOT) && la(1, MUL))) {
            ImportPackContext pack = new ImportPackContext();
            ctx = pack;
            if (la(DOTMUL)) {
                consume();
            }
            else {
                consume();
                consume();
            }
        }
        else {
            ctx = new ImportClsContext();
        }
        ctx.addToken(importToken);
        ctx.varIds.addAll(ids);
        for (VarIdContext id : ids) {
            ctx.addChild(id);
        }
        expectInto(ctx, SEMI, "';'");
        return ctx;
    }
    
    private DeclTypeContext parseDeclType() {
        DeclTypeContext ctx = new DeclTypeContext();
        if (isPrimitiveType(lt().getType())) {
            ctx.primitiveType = parsePrimitiveType();
            ctx.addChild(ctx.primitiveType);
        }
        else if (isVarIdToken(lt().getType())) {
            ctx.clsType = parseClsType();
            ctx.addChild(ctx.clsType);
        }
        else {
            throw new QLParseBacktrack();
        }
        if (la(LBRACK) && la(1, RBRACK)) {
            ctx.dims = parseDims();
            ctx.addChild(ctx.dims);
        }
        return ctx;
    }
    
    private DeclTypeNoArrContext parseDeclTypeNoArr() {
        DeclTypeNoArrContext ctx = new DeclTypeNoArrContext();
        if (isPrimitiveType(lt().getType())) {
            ctx.primitiveType = parsePrimitiveType();
            ctx.addChild(ctx.primitiveType);
        }
        else if (isVarIdToken(lt().getType())) {
            ctx.clsType = parseClsType();
            ctx.addChild(ctx.clsType);
        }
        else {
            throw syntax(lt(), "mismatched input '" + tokenText(lt()) + "' expecting type");
        }
        return ctx;
    }
    
    private PrimitiveTypeContext parsePrimitiveType() {
        if (!isPrimitiveType(lt().getType())) {
            throw new QLParseBacktrack();
        }
        PrimitiveTypeContext ctx = new PrimitiveTypeContext();
        ctx.addToken(consume());
        return ctx;
    }
    
    private ClsTypeContext parseClsType() {
        ClsTypeContext ctx = new ClsTypeContext();
        ctx.varIds.add(parseVarId());
        ctx.addChild(ctx.varIds.get(0));
        while (la(DOT) && isVarIdToken(token(p + 1).getType())) {
            consume();
            VarIdContext id = parseVarId();
            ctx.varIds.add(id);
            ctx.addChild(id);
        }
        if (la(LT) || la(NOEQ)) {
            parseTypeArguments();
        }
        return ctx;
    }
    
    private void parseTypeArguments() {
        if (la(NOEQ)) {
            consume();
            return;
        }
        expect(LT, "'<'");
        skipNewlines();
        if (!isTypeArgumentEnd() && !la(Token.EOF)) {
            parseTypeArgumentList();
            skipNewlines();
        }
        if (isTypeArgumentEnd()) {
            consume();
        }
    }
    
    private void parseTypeArgumentList() {
        parseTypeArgument();
        skipNewlines();
        while (la(COMMA)) {
            consume();
            skipNewlines();
            parseTypeArgument();
            skipNewlines();
        }
    }
    
    private void parseTypeArgument() {
        if (la(QUESTION)) {
            consume();
            skipNewlines();
            if (la(EXTENDS) || la(SUPER)) {
                consume();
                skipNewlines();
                parseReferenceType();
            }
            return;
        }
        parseReferenceType();
    }
    
    private void parseReferenceType() {
        if (isVarIdToken(lt().getType())) {
            parseClsType();
            if (la(LBRACK) && la(1, RBRACK)) {
                parseDims();
            }
            return;
        }
        if (isPrimitiveType(lt().getType())) {
            parsePrimitiveType();
            if (la(LBRACK) && la(1, RBRACK)) {
                parseDims();
                return;
            }
        }
        throw syntax(lt(), "mismatched input '" + tokenText(lt()) + "' expecting reference type");
    }
    
    private boolean isTypeArgumentEnd() {
        return la(GT) || la(RIGHSHIFT) || la(URSHIFT);
    }
    
    private DimsContext parseDims() {
        DimsContext ctx = new DimsContext();
        do {
            expectInto(ctx, LBRACK, "'['");
            expectInto(ctx, RBRACK, "']'");
        } while (la(LBRACK) && la(1, RBRACK));
        return ctx;
    }
    
    private DimExprsContext parseDimExprs() {
        DimExprsContext ctx = new DimExprsContext();
        do {
            expectInto(ctx, LBRACK, "'['");
            skipNewlines();
            ExpressionContext expression = parseExpression();
            ctx.expressions.add(expression);
            ctx.addChild(expression);
            skipNewlines();
            expectInto(ctx, RBRACK, "']'");
        } while (la(LBRACK) && !la(1, RBRACK));
        return ctx;
    }
    
    private OpIdContext parseOpId() {
        if (!isOpIdToken(lt().getType())) {
            throw syntax(lt(), "mismatched input '" + tokenText(lt()) + "' expecting operator");
        }
        OpIdContext ctx = new OpIdContext();
        ctx.addToken(consume());
        return ctx;
    }
    
    private VarIdContext parseVarId() {
        if (!isVarIdToken(lt().getType())) {
            throw syntax(lt(), "mismatched input '" + tokenText(lt()) + "' expecting identifier");
        }
        VarIdContext ctx = new VarIdContext();
        ctx.addToken(consume());
        return ctx;
    }
    
    private void consumeNextStatement() {
        if (la(Token.EOF) || la(RBRACE)) {
            return;
        }
        if (la(SEMI)) {
            consume();
            return;
        }
        if (strictNewLines) {
            expect(NEWLINE, "NEWLINE");
            while (la(NEWLINE)) {
                consume();
            }
        }
        else if (la(NEWLINE)) {
            while (la(NEWLINE)) {
                consume();
            }
        }
    }
    
    private boolean isNextStatementStart() {
        return la(Token.EOF) || la(RBRACE) || la(SEMI) || (strictNewLines && la(NEWLINE));
    }
    
    private void skipNewlines() {
        while (la(NEWLINE)) {
            consume();
        }
    }
    
    private boolean isLambdaStart() {
        if (isVarIdToken(lt().getType()) && la(1, ARROW)) {
            return true;
        }
        if (!la(LPAREN)) {
            return false;
        }
        int close = findMatchingParen(p);
        return close >= 0 && token(close + 1).getType() == ARROW;
    }
    
    private int findMatchingParen(int start) {
        int depth = 0;
        for (int i = start; i < tokens.size(); i++) {
            int type = token(i).getType();
            if (type == LPAREN) {
                depth++;
            }
            else if (type == RPAREN) {
                depth--;
                if (depth == 0) {
                    return i;
                }
            }
        }
        return -1;
    }
    
    private boolean isPrimaryStart(Token token) {
        int type = token.getType();
        return isLiteralStart(type) || type == LPAREN || type == NEW || isVarIdToken(type) || isPrimitiveType(type)
            || type == LBRACK || type == LBRACE || type == SELECTOR_START || type == IF || type == SWITCH || type == TRY
            || isPrefixOperator(token);
    }
    
    private boolean isNonExpressionStatementStart() {
        if (la(THROW) || la(WHILE) || la(FOR) || la(FUNCTION) || la(MACRO) || la(BREAK) || la(CONTINUE) || la(RETURN)
            || la(SEMI) || la(NEWLINE)) {
            return true;
        }
        return tryParseLocalVariableDeclaration() != null;
    }
    
    private boolean singleVarBefore(int... endTypes) {
        if (!isVarIdToken(lt().getType())) {
            return false;
        }
        for (int endType : endTypes) {
            if (la(1, endType)) {
                return true;
            }
        }
        return false;
    }
    
    private boolean singleForEachVarBeforeColon() {
        if (!isVarIdToken(lt().getType())) {
            return false;
        }
        int i = p + 1;
        while (token(i).getType() == NEWLINE) {
            i++;
        }
        return token(i).getType() == COLON;
    }
    
    private boolean isMapExprAhead() {
        int save = p;
        try {
            expect(LBRACE, "'{'");
            skipNewlines();
            if (la(COLON)) {
                p = save;
                return true;
            }
            if (!isIdMapKey(lt().getType()) && !la(QuoteStringLiteral) && !la(DOUBLE_QUOTE)) {
                p = save;
                return false;
            }
            if (la(DOUBLE_QUOTE)) {
                parseDoubleQuoteStringLiteral();
            }
            else {
                consume();
            }
            skipNewlines();
            boolean result = la(COLON);
            p = save;
            return result;
        }
        catch (RuntimeException e) {
            p = save;
            return false;
        }
    }
    
    private boolean hasTopLevelAssignOperatorAhead() {
        int depth = 0;
        for (int i = p; i < tokens.size(); i++) {
            int type = token(i).getType();
            if (depth == 0) {
                if (isAssignOperator(type)) {
                    return true;
                }
                if (type == Token.EOF || type == SEMI || type == COMMA || type == RPAREN || type == RBRACK
                    || type == RBRACE || (strictNewLines && type == NEWLINE)) {
                    return false;
                }
            }
            if (type == LPAREN || type == LBRACK || type == LBRACE) {
                depth++;
            }
            else if (type == RPAREN || type == RBRACK || type == RBRACE) {
                if (depth == 0) {
                    return false;
                }
                depth--;
            }
        }
        return false;
    }
    
    private boolean isLiteralStart(int type) {
        return type == IntegerLiteral || type == FloatingPointLiteral || type == IntegerOrFloatingLiteral
            || type == TRUE || type == FALSE || type == QuoteStringLiteral || type == DOUBLE_QUOTE || type == NULL;
    }
    
    private boolean isAssignOperator(int type) {
        return type == EQ || type == RIGHSHIFT_ASSGIN || type == URSHIFT_ASSGIN || type == LSHIFT_ASSGIN
            || type == ADD_ASSIGN || type == SUB_ASSIGN || type == AND_ASSIGN || type == OR_ASSIGN || type == MUL_ASSIGN
            || type == MOD_ASSIGN || type == DIV_ASSIGN || type == XOR_ASSIGN;
    }
    
    private boolean isPrimitiveType(int type) {
        return type == BYTE || type == SHORT || type == INT || type == LONG || type == FLOAT || type == DOUBLE
            || type == BOOL || type == CHAR;
    }
    
    private boolean isVarIdToken(int type) {
        return type == ID || type == FUNCTION || type == CASE || type == DEFAULT || type == SWITCH;
    }
    
    private boolean isIdMapKey(int type) {
        return isVarIdToken(type) || type == FOR || type == IF || type == ELSE || type == WHILE || type == BREAK
            || type == CONTINUE || type == RETURN || type == MACRO || type == IMPORT || type == STATIC || type == NEW
            || type == BYTE || type == SHORT || type == INT || type == LONG || type == FLOAT || type == DOUBLE
            || type == CHAR || type == BOOL || type == NULL || type == TRUE || type == FALSE || type == EXTENDS
            || type == SUPER || type == TRY || type == CATCH || type == FINALLY || type == THROW || type == CLASS
            || type == THIS;
    }
    
    private boolean isOpIdToken(int type) {
        return type == BANG || type == TILDE || type == ADD || type == SUB || type == INC || type == DEC
            || type == DOTMUL || isAssignOperator(type) || type == OPID;
    }
    
    private boolean isMiddleOperator(Token token) {
        return operatorManager != null && operatorManager.isOpType(token.getText(), MIDDLE);
    }
    
    private boolean isPrefixOperator(Token token) {
        return operatorManager != null && operatorManager.isOpType(token.getText(), PREFIX);
    }
    
    private boolean isSuffixOperator(Token token) {
        return operatorManager != null && operatorManager.isOpType(token.getText(), SUFFIX);
    }
    
    private boolean isGroupOperator(Token token) {
        return isOpIdToken(token.getType()) && isMiddleOperator(token)
            && precedence(token) == com.alibaba.qlexpress4.QLPrecedences.GROUP;
    }
    
    private int precedence(Token token) {
        Integer precedence = operatorManager.precedence(token.getText());
        return precedence == null ? -1 : precedence;
    }
    
    private Token lt() {
        return token(p);
    }
    
    private Token token(int index) {
        if (index >= tokens.size()) {
            return tokens.get(tokens.size() - 1);
        }
        return tokens.get(index);
    }
    
    private boolean la(int type) {
        return lt().getType() == type;
    }
    
    private boolean la(int offset, int type) {
        return token(p + offset).getType() == type;
    }
    
    private Token consume() {
        return tokens.get(p++);
    }
    
    private TerminalNode consumeNode(RuleContext ctx) {
        Token token = consume();
        TerminalNode node = new TerminalNode(token);
        ctx.addChild(node);
        return node;
    }
    
    private Token expect(int type, String display) {
        if (!la(type)) {
            throw syntax(lt(), "mismatched input '" + tokenText(lt()) + "' expecting " + display);
        }
        return consume();
    }
    
    private void expectInto(RuleContext ctx, int type, String display) {
        ctx.addToken(expect(type, display));
    }
    
    private String tokenText(Token token) {
        return token.getType() == Token.EOF ? "<EOF>" : token.getText();
    }
    
    private RuntimeException syntax(Token token, String reason) {
        String reportScript = token.getType() == Token.EOF ? script + "<EOF>" : script;
        throw QLException.reportScannerErr(reportScript,
            token.getStartIndex(),
            token.getLine(),
            token.getCharPositionInLine() + 1,
            tokenText(token),
            QLErrorCodes.SYNTAX_ERROR.name(),
            reason);
    }
    
    private static class QLParseBacktrack extends RuntimeException {
    }
    
    public static class ProgramContext extends RuleContext {
        private final List<ImportDeclarationContext> imports = new ArrayList<>();
        
        private BlockStatementsContext blockStatements;
        
        public List<ImportDeclarationContext> importDeclaration() {
            return imports;
        }
        
        public BlockStatementsContext blockStatements() {
            return blockStatements;
        }
        
        @Override
        public <T> T accept(QLParserBaseVisitor<T> visitor) {
            return visitor.visitProgram(this);
        }
    }
    
    public static class BlockStatementsContext extends RuleContext {
        private final List<BlockStatementContext> statements = new ArrayList<>();
        
        public List<BlockStatementContext> blockStatement() {
            return statements;
        }
        
        @Override
        public <T> T accept(QLParserBaseVisitor<T> visitor) {
            return visitor.visitBlockStatements(this);
        }
    }
    
    public abstract static class BlockStatementContext extends RuleContext {
    }
    
    public static class LocalVariableDeclarationStatementContext extends BlockStatementContext {
        private LocalVariableDeclarationContext localVariableDeclaration;
        
        public LocalVariableDeclarationContext localVariableDeclaration() {
            return localVariableDeclaration;
        }
        
        @Override
        public <T> T accept(QLParserBaseVisitor<T> visitor) {
            return visitor.visitLocalVariableDeclarationStatement(this);
        }
    }
    
    public static class ThrowStatementContext extends BlockStatementContext {
        private TerminalNode throwToken;
        
        private ExpressionContext expression;
        
        public TerminalNode THROW() {
            return throwToken;
        }
        
        public ExpressionContext expression() {
            return expression;
        }
        
        @Override
        public <T> T accept(QLParserBaseVisitor<T> visitor) {
            return visitor.visitThrowStatement(this);
        }
    }
    
    public static class WhileStatementContext extends BlockStatementContext {
        private TerminalNode whileToken;
        
        private ExpressionContext expression;
        
        private BlockStatementsContext blockStatements;
        
        public TerminalNode WHILE() {
            return whileToken;
        }
        
        public ExpressionContext expression() {
            return expression;
        }
        
        public BlockStatementsContext blockStatements() {
            return blockStatements;
        }
        
        @Override
        public <T> T accept(QLParserBaseVisitor<T> visitor) {
            return visitor.visitWhileStatement(this);
        }
    }
    
    public static class TraditionalForStatementContext extends BlockStatementContext {
        private TerminalNode forToken;
        
        private ForInitContext forInit;
        
        public ExpressionContext forCondition;
        
        public ExpressionContext forUpdate;
        
        private BlockStatementsContext blockStatements;
        
        public TerminalNode FOR() {
            return forToken;
        }
        
        public ForInitContext forInit() {
            return forInit;
        }
        
        public BlockStatementsContext blockStatements() {
            return blockStatements;
        }
        
        @Override
        public <T> T accept(QLParserBaseVisitor<T> visitor) {
            return visitor.visitTraditionalForStatement(this);
        }
    }
    
    public static class ForEachStatementContext extends BlockStatementContext {
        private TerminalNode forToken;
        
        private DeclTypeContext declType;
        
        private VarIdContext varId;
        
        private ExpressionContext expression;
        
        private BlockStatementsContext blockStatements;
        
        public TerminalNode FOR() {
            return forToken;
        }
        
        public DeclTypeContext declType() {
            return declType;
        }
        
        public VarIdContext varId() {
            return varId;
        }
        
        public ExpressionContext expression() {
            return expression;
        }
        
        public BlockStatementsContext blockStatements() {
            return blockStatements;
        }
        
        @Override
        public <T> T accept(QLParserBaseVisitor<T> visitor) {
            return visitor.visitForEachStatement(this);
        }
    }
    
    public static class FunctionStatementContext extends BlockStatementContext {
        private TerminalNode functionToken;
        
        private VarIdContext varId;
        
        private FormalOrInferredParameterListContext params;
        
        private BlockStatementsContext blockStatements;
        
        public TerminalNode FUNCTION() {
            return functionToken;
        }
        
        public VarIdContext varId() {
            return varId;
        }
        
        public FormalOrInferredParameterListContext formalOrInferredParameterList() {
            return params;
        }
        
        public BlockStatementsContext blockStatements() {
            return blockStatements;
        }
        
        @Override
        public <T> T accept(QLParserBaseVisitor<T> visitor) {
            return visitor.visitFunctionStatement(this);
        }
    }
    
    public static class MacroStatementContext extends BlockStatementContext {
        private TerminalNode macroToken;
        
        private VarIdContext varId;
        
        private BlockStatementsContext blockStatements;
        
        public TerminalNode MACRO() {
            return macroToken;
        }
        
        public VarIdContext varId() {
            return varId;
        }
        
        public BlockStatementsContext blockStatements() {
            return blockStatements;
        }
        
        @Override
        public <T> T accept(QLParserBaseVisitor<T> visitor) {
            return visitor.visitMacroStatement(this);
        }
    }
    
    public static class BreakContinueStatementContext extends BlockStatementContext {
        private TerminalNode breakToken;
        
        private TerminalNode continueToken;
        
        public TerminalNode BREAK() {
            return breakToken;
        }
        
        public TerminalNode CONTINUE() {
            return continueToken;
        }
        
        @Override
        public <T> T accept(QLParserBaseVisitor<T> visitor) {
            return visitor.visitBreakContinueStatement(this);
        }
    }
    
    public static class ReturnStatementContext extends BlockStatementContext {
        private TerminalNode returnToken;
        
        private ExpressionContext expression;
        
        public TerminalNode RETURN() {
            return returnToken;
        }
        
        public ExpressionContext expression() {
            return expression;
        }
        
        @Override
        public <T> T accept(QLParserBaseVisitor<T> visitor) {
            return visitor.visitReturnStatement(this);
        }
    }
    
    public static class EmptyStatementContext extends BlockStatementContext {
        @Override
        public <T> T accept(QLParserBaseVisitor<T> visitor) {
            return visitor.visitEmptyStatement(this);
        }
    }
    
    public static class ExpressionStatementContext extends BlockStatementContext {
        private ExpressionContext expression;
        
        public ExpressionContext expression() {
            return expression;
        }
        
        @Override
        public <T> T accept(QLParserBaseVisitor<T> visitor) {
            return visitor.visitExpressionStatement(this);
        }
    }
    
    public static class LocalVariableDeclarationContext extends RuleContext {
        private DeclTypeContext declType;
        
        private VariableDeclaratorListContext variableDeclaratorList;
        
        public DeclTypeContext declType() {
            return declType;
        }
        
        public VariableDeclaratorListContext variableDeclaratorList() {
            return variableDeclaratorList;
        }
        
        @Override
        public <T> T accept(QLParserBaseVisitor<T> visitor) {
            return visitor.visitLocalVariableDeclaration(this);
        }
    }
    
    public static class ForInitContext extends RuleContext {
        private LocalVariableDeclarationContext localVariableDeclaration;
        
        private ExpressionContext expression;
        
        public LocalVariableDeclarationContext localVariableDeclaration() {
            return localVariableDeclaration;
        }
        
        public ExpressionContext expression() {
            return expression;
        }
        
        @Override
        public <T> T accept(QLParserBaseVisitor<T> visitor) {
            return visitor.visitForInit(this);
        }
    }
    
    public static class VariableDeclaratorListContext extends RuleContext {
        private final List<VariableDeclaratorContext> variables = new ArrayList<>();
        
        public List<VariableDeclaratorContext> variableDeclarator() {
            return variables;
        }
        
        @Override
        public <T> T accept(QLParserBaseVisitor<T> visitor) {
            return visitor.visitVariableDeclaratorList(this);
        }
    }
    
    public static class VariableDeclaratorContext extends RuleContext {
        private VariableDeclaratorIdContext id;
        
        private VariableInitializerContext initializer;
        
        public VariableDeclaratorIdContext variableDeclaratorId() {
            return id;
        }
        
        public VariableInitializerContext variableInitializer() {
            return initializer;
        }
        
        @Override
        public <T> T accept(QLParserBaseVisitor<T> visitor) {
            return visitor.visitVariableDeclarator(this);
        }
    }
    
    public static class VariableDeclaratorIdContext extends RuleContext {
        private VarIdContext varId;
        
        private DimsContext dims;
        
        public VarIdContext varId() {
            return varId;
        }
        
        public DimsContext dims() {
            return dims;
        }
        
        @Override
        public <T> T accept(QLParserBaseVisitor<T> visitor) {
            return visitor.visitVariableDeclaratorId(this);
        }
    }
    
    public static class VariableInitializerContext extends RuleContext {
        private ExpressionContext expression;
        
        private ArrayInitializerContext arrayInitializer;
        
        public ExpressionContext expression() {
            return expression;
        }
        
        public ArrayInitializerContext arrayInitializer() {
            return arrayInitializer;
        }
        
        @Override
        public <T> T accept(QLParserBaseVisitor<T> visitor) {
            return visitor.visitVariableInitializer(this);
        }
    }
    
    public static class ArrayInitializerContext extends RuleContext {
        private VariableInitializerListContext initializers;
        
        public VariableInitializerListContext variableInitializerList() {
            return initializers;
        }
        
        @Override
        public <T> T accept(QLParserBaseVisitor<T> visitor) {
            return visitor.visitArrayInitializer(this);
        }
    }
    
    public static class VariableInitializerListContext extends RuleContext {
        private final List<VariableInitializerContext> initializers = new ArrayList<>();
        
        public List<VariableInitializerContext> variableInitializer() {
            return initializers;
        }
        
        @Override
        public <T> T accept(QLParserBaseVisitor<T> visitor) {
            return visitor.visitVariableInitializerList(this);
        }
    }
    
    public static class DeclTypeContext extends RuleContext {
        private PrimitiveTypeContext primitiveType;
        
        private ClsTypeContext clsType;
        
        private DimsContext dims;
        
        public PrimitiveTypeContext primitiveType() {
            return primitiveType;
        }
        
        public ClsTypeContext clsType() {
            return clsType;
        }
        
        public DimsContext dims() {
            return dims;
        }
        
        @Override
        public <T> T accept(QLParserBaseVisitor<T> visitor) {
            return visitor.visitDeclType(this);
        }
    }
    
    public static class DeclTypeNoArrContext extends RuleContext {
        private PrimitiveTypeContext primitiveType;
        
        private ClsTypeContext clsType;
        
        public PrimitiveTypeContext primitiveType() {
            return primitiveType;
        }
        
        public ClsTypeContext clsType() {
            return clsType;
        }
        
        @Override
        public <T> T accept(QLParserBaseVisitor<T> visitor) {
            return visitor.visitDeclTypeNoArr(this);
        }
    }
    
    public static class PrimitiveTypeContext extends RuleContext {
        @Override
        public <T> T accept(QLParserBaseVisitor<T> visitor) {
            return visitor.visitPrimitiveType(this);
        }
    }
    
    public static class ClsTypeContext extends RuleContext {
        private final List<VarIdContext> varIds = new ArrayList<>();
        
        public List<VarIdContext> varId() {
            return varIds;
        }
        
        @Override
        public <T> T accept(QLParserBaseVisitor<T> visitor) {
            return visitor.visitClsType(this);
        }
    }
    
    public static class DimsContext extends RuleContext {
        public List<TerminalNode> LBRACK() {
            return tokenNodes(QLParser.LBRACK);
        }
        
        @Override
        public <T> T accept(QLParserBaseVisitor<T> visitor) {
            return visitor.visitDims(this);
        }
    }
    
    public static class DimExprsContext extends RuleContext {
        private final List<ExpressionContext> expressions = new ArrayList<>();
        
        public List<ExpressionContext> expression() {
            return expressions;
        }
        
        @Override
        public <T> T accept(QLParserBaseVisitor<T> visitor) {
            return visitor.visitDimExprs(this);
        }
    }
    
    public static class ExpressionContext extends RuleContext {
        private LeftHandSideContext left;
        
        private AssignOperatorContext assignOperator;
        
        private ExpressionContext expression;
        
        private TernaryExprContext ternary;
        
        public LeftHandSideContext leftHandSide() {
            return left;
        }
        
        public AssignOperatorContext assignOperator() {
            return assignOperator;
        }
        
        public ExpressionContext expression() {
            return expression;
        }
        
        public TernaryExprContext ternaryExpr() {
            return ternary;
        }
        
        @Override
        public <T> T accept(QLParserBaseVisitor<T> visitor) {
            return visitor.visitExpression(this);
        }
    }
    
    public static class LeftHandSideContext extends RuleContext {
        private VarIdContext varId;
        
        private TerminalNode lparen;
        
        private ArgumentListContext argumentList;
        
        private final List<PathPartContext> pathParts = new ArrayList<>();
        
        public VarIdContext varId() {
            return varId;
        }
        
        public TerminalNode LPAREN() {
            return lparen;
        }
        
        public ArgumentListContext argumentList() {
            return argumentList;
        }
        
        public List<PathPartContext> pathPart() {
            return pathParts;
        }
        
        @Override
        public <T> T accept(QLParserBaseVisitor<T> visitor) {
            return visitor.visitLeftHandSide(this);
        }
    }
    
    public static class AssignOperatorContext extends RuleContext {
        @Override
        public <T> T accept(QLParserBaseVisitor<T> visitor) {
            return visitor.visitAssignOperator(this);
        }
    }
    
    public static class TernaryExprContext extends RuleContext {
        public BaseExprContext condition;
        
        public BaseExprContext thenExpr;
        
        public ExpressionContext elseExpr;
        
        private TerminalNode question;
        
        public TerminalNode QUESTION() {
            return question;
        }
        
        public BaseExprContext baseExpr(int i) {
            return i == 0 ? condition : thenExpr;
        }
        
        @Override
        public <T> T accept(QLParserBaseVisitor<T> visitor) {
            return visitor.visitTernaryExpr(this);
        }
    }
    
    public static class BaseExprContext extends RuleContext {
        private PrimaryContext primary;
        
        private final List<LeftAssoContext> leftAssos = new ArrayList<>();
        
        public PrimaryContext primary() {
            return primary;
        }
        
        public List<LeftAssoContext> leftAsso() {
            return leftAssos;
        }
        
        @Override
        public <T> T accept(QLParserBaseVisitor<T> visitor) {
            return visitor.visitBaseExpr(this);
        }
    }
    
    public static class LeftAssoContext extends RuleContext {
        private BinaryopContext binaryop;
        
        private BaseExprContext right;
        
        public BinaryopContext binaryop() {
            return binaryop;
        }
        
        public BaseExprContext baseExpr() {
            return right;
        }
        
        @Override
        public <T> T accept(QLParserBaseVisitor<T> visitor) {
            return visitor.visitLeftAsso(this);
        }
    }
    
    public static class BinaryopContext extends RuleContext {
        @Override
        public <T> T accept(QLParserBaseVisitor<T> visitor) {
            return visitor.visitBinaryop(this);
        }
    }
    
    public static class PrimaryContext extends RuleContext {
        private PrefixExpressContext prefix;
        
        private PrimaryNoFixPathableContext pathable;
        
        private final List<PathPartContext> pathParts = new ArrayList<>();
        
        private SuffixExpressContext suffix;
        
        private PrimaryNoFixNonPathableContext nonPathable;
        
        public PrefixExpressContext prefixExpress() {
            return prefix;
        }
        
        public PrimaryNoFixPathableContext primaryNoFixPathable() {
            return pathable;
        }
        
        public List<PathPartContext> pathPart() {
            return pathParts;
        }
        
        public SuffixExpressContext suffixExpress() {
            return suffix;
        }
        
        public PrimaryNoFixNonPathableContext primaryNoFixNonPathable() {
            return nonPathable;
        }
        
        @Override
        public <T> T accept(QLParserBaseVisitor<T> visitor) {
            return visitor.visitPrimary(this);
        }
    }
    
    public static class PrefixExpressContext extends RuleContext {
        private OpIdContext opId;
        
        public OpIdContext opId() {
            return opId;
        }
        
        @Override
        public <T> T accept(QLParserBaseVisitor<T> visitor) {
            return visitor.visitPrefixExpress(this);
        }
    }
    
    public static class SuffixExpressContext extends RuleContext {
        private OpIdContext opId;
        
        public OpIdContext opId() {
            return opId;
        }
        
        @Override
        public <T> T accept(QLParserBaseVisitor<T> visitor) {
            return visitor.visitSuffixExpress(this);
        }
    }
    
    public abstract static class PrimaryNoFixPathableContext extends RuleContext {
    }
    
    public abstract static class PrimaryNoFixNonPathableContext extends RuleContext {
    }
    
    public static class ConstExprContext extends PrimaryNoFixPathableContext {
        private LiteralContext literal;
        
        public LiteralContext literal() {
            return literal;
        }
        
        @Override
        public <T> T accept(QLParserBaseVisitor<T> visitor) {
            return visitor.visitConstExpr(this);
        }
    }
    
    public static class CastExprContext extends PrimaryNoFixPathableContext {
        private DeclTypeContext declType;
        
        private PrimaryContext primary;
        
        public DeclTypeContext declType() {
            return declType;
        }
        
        public PrimaryContext primary() {
            return primary;
        }
        
        @Override
        public <T> T accept(QLParserBaseVisitor<T> visitor) {
            return visitor.visitCastExpr(this);
        }
    }
    
    public static class GroupExprContext extends PrimaryNoFixPathableContext {
        private ExpressionContext expression;
        
        public ExpressionContext expression() {
            return expression;
        }
        
        @Override
        public <T> T accept(QLParserBaseVisitor<T> visitor) {
            return visitor.visitGroupExpr(this);
        }
    }
    
    public static class NewObjExprContext extends PrimaryNoFixPathableContext {
        private TerminalNode newToken;
        
        private final List<VarIdContext> varIds = new ArrayList<>();
        
        private ArgumentListContext argumentList;
        
        public TerminalNode NEW() {
            return newToken;
        }
        
        public List<VarIdContext> varId() {
            return varIds;
        }
        
        public ArgumentListContext argumentList() {
            return argumentList;
        }
        
        @Override
        public <T> T accept(QLParserBaseVisitor<T> visitor) {
            return visitor.visitNewObjExpr(this);
        }
    }
    
    public static class NewEmptyArrExprContext extends PrimaryNoFixPathableContext {
        private TerminalNode newToken;
        
        private DeclTypeNoArrContext declTypeNoArr;
        
        private DimExprsContext dimExprs;
        
        public TerminalNode NEW() {
            return newToken;
        }
        
        public DeclTypeNoArrContext declTypeNoArr() {
            return declTypeNoArr;
        }
        
        public DimExprsContext dimExprs() {
            return dimExprs;
        }
        
        @Override
        public <T> T accept(QLParserBaseVisitor<T> visitor) {
            return visitor.visitNewEmptyArrExpr(this);
        }
    }
    
    public static class NewInitArrExprContext extends PrimaryNoFixPathableContext {
        private TerminalNode newToken;
        
        private DeclTypeNoArrContext declTypeNoArr;
        
        private DimsContext dims;
        
        private ArrayInitializerContext arrayInitializer;
        
        public TerminalNode NEW() {
            return newToken;
        }
        
        public DeclTypeNoArrContext declTypeNoArr() {
            return declTypeNoArr;
        }
        
        public DimsContext dims() {
            return dims;
        }
        
        public ArrayInitializerContext arrayInitializer() {
            return arrayInitializer;
        }
        
        @Override
        public <T> T accept(QLParserBaseVisitor<T> visitor) {
            return visitor.visitNewInitArrExpr(this);
        }
    }
    
    public static class VarIdExprContext extends PrimaryNoFixPathableContext {
        private VarIdContext varId;
        
        private TerminalNode lparen;
        
        private ArgumentListContext argumentList;
        
        public VarIdContext varId() {
            return varId;
        }
        
        public TerminalNode LPAREN() {
            return lparen;
        }
        
        public ArgumentListContext argumentList() {
            return argumentList;
        }
        
        @Override
        public <T> T accept(QLParserBaseVisitor<T> visitor) {
            return visitor.visitVarIdExpr(this);
        }
    }
    
    public static class TypeExprContext extends PrimaryNoFixPathableContext {
        private PrimitiveTypeContext primitiveType;
        
        public PrimitiveTypeContext primitiveType() {
            return primitiveType;
        }
        
        @Override
        public <T> T accept(QLParserBaseVisitor<T> visitor) {
            return visitor.visitTypeExpr(this);
        }
    }
    
    public static class ListExprContext extends PrimaryNoFixPathableContext {
        private ListItemsContext listItems;
        
        public ListItemsContext listItems() {
            return listItems;
        }
        
        @Override
        public <T> T accept(QLParserBaseVisitor<T> visitor) {
            return visitor.visitListExpr(this);
        }
    }
    
    public static class MapExprContext extends PrimaryNoFixPathableContext {
        private MapEntriesContext mapEntries;
        
        public MapEntriesContext mapEntries() {
            return mapEntries;
        }
        
        @Override
        public <T> T accept(QLParserBaseVisitor<T> visitor) {
            return visitor.visitMapExpr(this);
        }
    }
    
    public static class BlockExprContext extends PrimaryNoFixPathableContext {
        private BlockStatementsContext blockStatements;
        
        public BlockStatementsContext blockStatements() {
            return blockStatements;
        }
        
        @Override
        public <T> T accept(QLParserBaseVisitor<T> visitor) {
            return visitor.visitBlockExpr(this);
        }
    }
    
    public static class ContextSelectExprContext extends PrimaryNoFixPathableContext {
        private TerminalNode selectorStart;
        
        private TerminalNode selectorVariable;
        
        private TerminalNode selectorEnd;
        
        public TerminalNode SELECTOR_START() {
            return selectorStart;
        }
        
        public TerminalNode SelectorVariable_VANME() {
            return selectorVariable;
        }
        
        @Override
        public <T> T accept(QLParserBaseVisitor<T> visitor) {
            return visitor.visitContextSelectExpr(this);
        }
    }
    
    public static class QlIfContext extends PrimaryNoFixNonPathableContext {
        private TerminalNode ifToken;
        
        public ExpressionContext condition;
        
        private ThenBodyContext thenBody;
        
        private ElseBodyContext elseBody;
        
        public TerminalNode IF() {
            return ifToken;
        }
        
        public ThenBodyContext thenBody() {
            return thenBody;
        }
        
        public ElseBodyContext elseBody() {
            return elseBody;
        }
        
        @Override
        public <T> T accept(QLParserBaseVisitor<T> visitor) {
            return visitor.visitQlIf(this);
        }
    }
    
    public static class ThenBodyContext extends RuleContext {
        private TerminalNode lbrace;
        
        private BlockStatementsContext blockStatements;
        
        private NonExpressionStatementContext nonExpressionStatement;
        
        private ExpressionContext expression;
        
        public TerminalNode LBRACE() {
            return lbrace;
        }
        
        public BlockStatementsContext blockStatements() {
            return blockStatements;
        }
        
        public NonExpressionStatementContext nonExpressionStatement() {
            return nonExpressionStatement;
        }
        
        public ExpressionContext expression() {
            return expression;
        }
        
        @Override
        public <T> T accept(QLParserBaseVisitor<T> visitor) {
            return visitor.visitThenBody(this);
        }
    }
    
    public static class ElseBodyContext extends RuleContext {
        private TerminalNode lbrace;
        
        private BlockStatementsContext blockStatements;
        
        private QlIfContext qlIf;
        
        private NonExpressionStatementContext nonExpressionStatement;
        
        private ExpressionContext expression;
        
        public TerminalNode LBRACE() {
            return lbrace;
        }
        
        public BlockStatementsContext blockStatements() {
            return blockStatements;
        }
        
        public QlIfContext qlIf() {
            return qlIf;
        }
        
        public NonExpressionStatementContext nonExpressionStatement() {
            return nonExpressionStatement;
        }
        
        public ExpressionContext expression() {
            return expression;
        }
        
        @Override
        public <T> T accept(QLParserBaseVisitor<T> visitor) {
            return visitor.visitElseBody(this);
        }
    }
    
    public static class NonExpressionStatementContext extends RuleContext {
        private BlockStatementContext statement;
        
        public LocalVariableDeclarationContext localVariableDeclaration() {
            return statement instanceof LocalVariableDeclarationStatementContext
                ? ((LocalVariableDeclarationStatementContext)statement).localVariableDeclaration()
                : null;
        }
        
        public TerminalNode THROW() {
            return statement instanceof ThrowStatementContext ? ((ThrowStatementContext)statement).THROW() : null;
        }
        
        public TerminalNode RETURN() {
            return statement instanceof ReturnStatementContext ? ((ReturnStatementContext)statement).RETURN() : null;
        }
        
        public TerminalNode WHILE() {
            return statement instanceof WhileStatementContext ? ((WhileStatementContext)statement).WHILE() : null;
        }
        
        public TerminalNode FOR() {
            if (statement instanceof TraditionalForStatementContext) {
                return ((TraditionalForStatementContext)statement).FOR();
            }
            if (statement instanceof ForEachStatementContext) {
                return ((ForEachStatementContext)statement).FOR();
            }
            return null;
        }
        
        public TerminalNode FUNCTION() {
            return statement instanceof FunctionStatementContext ? ((FunctionStatementContext)statement).FUNCTION()
                : null;
        }
        
        public TerminalNode MACRO() {
            return statement instanceof MacroStatementContext ? ((MacroStatementContext)statement).MACRO() : null;
        }
        
        public TerminalNode BREAK() {
            return statement instanceof BreakContinueStatementContext
                ? ((BreakContinueStatementContext)statement).BREAK()
                : null;
        }
        
        public TerminalNode CONTINUE() {
            return statement instanceof BreakContinueStatementContext
                ? ((BreakContinueStatementContext)statement).CONTINUE()
                : null;
        }
        
        public TerminalNode NEWLINE() {
            return statement instanceof EmptyStatementContext ? statement.tokenNode(QLParser.NEWLINE) : null;
        }
        
        public List<ExpressionContext> expression() {
            if (statement instanceof ThrowStatementContext) {
                return Collections.singletonList(((ThrowStatementContext)statement).expression());
            }
            if (statement instanceof ReturnStatementContext
                && ((ReturnStatementContext)statement).expression() != null) {
                return Collections.singletonList(((ReturnStatementContext)statement).expression());
            }
            return Collections.emptyList();
        }
        
        public VarIdContext varId() {
            if (statement instanceof FunctionStatementContext) {
                return ((FunctionStatementContext)statement).varId();
            }
            if (statement instanceof MacroStatementContext) {
                return ((MacroStatementContext)statement).varId();
            }
            return null;
        }
        
        @Override
        public <T> T accept(QLParserBaseVisitor<T> visitor) {
            return visitor.visitNonExpressionStatement(this);
        }
    }
    
    public static class ListItemsContext extends RuleContext {
        private final List<ExpressionContext> expressions = new ArrayList<>();
        
        public List<ExpressionContext> expression() {
            return expressions;
        }
        
        @Override
        public <T> T accept(QLParserBaseVisitor<T> visitor) {
            return visitor.visitListItems(this);
        }
    }
    
    public static class TryCatchesContext extends RuleContext {
        private final List<TryCatchContext> catches = new ArrayList<>();
        
        public List<TryCatchContext> tryCatch() {
            return catches;
        }
        
        @Override
        public <T> T accept(QLParserBaseVisitor<T> visitor) {
            return visitor.visitTryCatches(this);
        }
    }
    
    public static class TryCatchContext extends RuleContext {
        private TerminalNode catchToken;
        
        private CatchParamsContext catchParams;
        
        private BlockStatementsContext blockStatements;
        
        public TerminalNode CATCH() {
            return catchToken;
        }
        
        public CatchParamsContext catchParams() {
            return catchParams;
        }
        
        public BlockStatementsContext blockStatements() {
            return blockStatements;
        }
        
        @Override
        public <T> T accept(QLParserBaseVisitor<T> visitor) {
            return visitor.visitTryCatch(this);
        }
    }
    
    public static class CatchParamsContext extends RuleContext {
        private final List<DeclTypeContext> declTypes = new ArrayList<>();
        
        private VarIdContext varId;
        
        public List<DeclTypeContext> declType() {
            return declTypes;
        }
        
        public VarIdContext varId() {
            return varId;
        }
        
        @Override
        public <T> T accept(QLParserBaseVisitor<T> visitor) {
            return visitor.visitCatchParams(this);
        }
    }
    
    public static class TryFinallyContext extends RuleContext {
        private TerminalNode finallyToken;
        
        private BlockStatementsContext blockStatements;
        
        public TerminalNode FINALLY() {
            return finallyToken;
        }
        
        public BlockStatementsContext blockStatements() {
            return blockStatements;
        }
        
        @Override
        public <T> T accept(QLParserBaseVisitor<T> visitor) {
            return visitor.visitTryFinally(this);
        }
    }
    
    public static class MapEntriesContext extends RuleContext {
        private final List<MapEntryContext> entries = new ArrayList<>();
        
        public List<MapEntryContext> mapEntry() {
            return entries;
        }
        
        @Override
        public <T> T accept(QLParserBaseVisitor<T> visitor) {
            return visitor.visitMapEntries(this);
        }
    }
    
    public static class MapEntryContext extends RuleContext {
        private MapKeyContext mapKey;
        
        private MapValueContext mapValue;
        
        public MapKeyContext mapKey() {
            return mapKey;
        }
        
        public MapValueContext mapValue() {
            return mapValue;
        }
        
        @Override
        public <T> T accept(QLParserBaseVisitor<T> visitor) {
            return visitor.visitMapEntry(this);
        }
    }
    
    public abstract static class MapValueContext extends RuleContext {
    }
    
    public static class ClsValueContext extends MapValueContext {
        private TerminalNode quote;
        
        public TerminalNode QuoteStringLiteral() {
            return quote;
        }
        
        @Override
        public <T> T accept(QLParserBaseVisitor<T> visitor) {
            return visitor.visitClsValue(this);
        }
    }
    
    public static class EValueContext extends MapValueContext {
        private ExpressionContext expression;
        
        public ExpressionContext expression() {
            return expression;
        }
        
        @Override
        public <T> T accept(QLParserBaseVisitor<T> visitor) {
            return visitor.visitEValue(this);
        }
    }
    
    public abstract static class MapKeyContext extends RuleContext {
    }
    
    public static class IdKeyContext extends MapKeyContext {
        @Override
        public <T> T accept(QLParserBaseVisitor<T> visitor) {
            return visitor.visitIdKey(this);
        }
    }
    
    public static class StringKeyContext extends MapKeyContext {
        private DoubleQuoteStringLiteralContext doubleQuoteString;
        
        public DoubleQuoteStringLiteralContext doubleQuoteStringLiteral() {
            return doubleQuoteString;
        }
        
        @Override
        public <T> T accept(QLParserBaseVisitor<T> visitor) {
            return visitor.visitStringKey(this);
        }
    }
    
    public static class QuoteStringKeyContext extends MapKeyContext {
        @Override
        public <T> T accept(QLParserBaseVisitor<T> visitor) {
            return visitor.visitQuoteStringKey(this);
        }
    }
    
    public abstract static class PathPartContext extends RuleContext {
    }
    
    public static class MethodInvokeContext extends PathPartContext {
        protected VarIdContext varId;
        
        protected ArgumentListContext argumentList;
        
        public VarIdContext varId() {
            return varId;
        }
        
        public ArgumentListContext argumentList() {
            return argumentList;
        }
        
        @Override
        public <T> T accept(QLParserBaseVisitor<T> visitor) {
            return visitor.visitMethodInvoke(this);
        }
    }
    
    public static class OptionalMethodInvokeContext extends MethodInvokeContext {
        @Override
        public <T> T accept(QLParserBaseVisitor<T> visitor) {
            return visitor.visitOptionalMethodInvoke(this);
        }
    }
    
    public static class SpreadMethodInvokeContext extends MethodInvokeContext {
        @Override
        public <T> T accept(QLParserBaseVisitor<T> visitor) {
            return visitor.visitSpreadMethodInvoke(this);
        }
    }
    
    public static class FieldAccessContext extends PathPartContext {
        protected FieldIdContext fieldId;
        
        public FieldIdContext fieldId() {
            return fieldId;
        }
        
        @Override
        public <T> T accept(QLParserBaseVisitor<T> visitor) {
            return visitor.visitFieldAccess(this);
        }
    }
    
    public static class OptionalFieldAccessContext extends FieldAccessContext {
        @Override
        public <T> T accept(QLParserBaseVisitor<T> visitor) {
            return visitor.visitOptionalFieldAccess(this);
        }
    }
    
    public static class SpreadFieldAccessContext extends FieldAccessContext {
        @Override
        public <T> T accept(QLParserBaseVisitor<T> visitor) {
            return visitor.visitSpreadFieldAccess(this);
        }
    }
    
    public static class MethodAccessContext extends PathPartContext {
        private TerminalNode dcolon;
        
        private VarIdContext varId;
        
        public TerminalNode DCOLON() {
            return dcolon;
        }
        
        public VarIdContext varId() {
            return varId;
        }
        
        @Override
        public <T> T accept(QLParserBaseVisitor<T> visitor) {
            return visitor.visitMethodAccess(this);
        }
    }
    
    public static class IndexExprContext extends PathPartContext {
        private IndexValueExprContext indexValueExpr;
        
        public IndexValueExprContext indexValueExpr() {
            return indexValueExpr;
        }
        
        @Override
        public <T> T accept(QLParserBaseVisitor<T> visitor) {
            return visitor.visitIndexExpr(this);
        }
    }
    
    public static class CustomPathContext extends PathPartContext {
        private OpIdContext opId;
        
        private VarIdContext varId;
        
        private String pathText;
        
        public OpIdContext opId() {
            return opId;
        }
        
        public VarIdContext varId() {
            return varId;
        }
        
        public String pathText() {
            return pathText;
        }
        
        @Override
        public <T> T accept(QLParserBaseVisitor<T> visitor) {
            return visitor.visitCustomPath(this);
        }
    }
    
    public static class FieldIdContext extends RuleContext {
        private TerminalNode quote;
        
        public TerminalNode QuoteStringLiteral() {
            return quote;
        }
        
        @Override
        public <T> T accept(QLParserBaseVisitor<T> visitor) {
            return visitor.visitFieldId(this);
        }
    }
    
    public abstract static class IndexValueExprContext extends RuleContext {
    }
    
    public static class SingleIndexContext extends IndexValueExprContext {
        private ExpressionContext expression;
        
        public ExpressionContext expression() {
            return expression;
        }
        
        @Override
        public <T> T accept(QLParserBaseVisitor<T> visitor) {
            return visitor.visitSingleIndex(this);
        }
    }
    
    public static class SliceIndexContext extends IndexValueExprContext {
        public ExpressionContext start;
        
        public ExpressionContext end;
        
        @Override
        public <T> T accept(QLParserBaseVisitor<T> visitor) {
            return visitor.visitSliceIndex(this);
        }
    }
    
    public static class ArgumentListContext extends RuleContext {
        private final List<ExpressionContext> expressions = new ArrayList<>();
        
        public List<ExpressionContext> expression() {
            return expressions;
        }
        
        @Override
        public <T> T accept(QLParserBaseVisitor<T> visitor) {
            return visitor.visitArgumentList(this);
        }
    }
    
    public static class LiteralContext extends RuleContext {
        private BoolenLiteralContext boolen;
        
        private DoubleQuoteStringLiteralContext doubleQuoteString;
        
        public TerminalNode IntegerLiteral() {
            return tokenNode(QLParser.IntegerLiteral);
        }
        
        public TerminalNode FloatingPointLiteral() {
            return tokenNode(QLParser.FloatingPointLiteral);
        }
        
        public TerminalNode IntegerOrFloatingLiteral() {
            return tokenNode(QLParser.IntegerOrFloatingLiteral);
        }
        
        public TerminalNode QuoteStringLiteral() {
            return tokenNode(QLParser.QuoteStringLiteral);
        }
        
        public TerminalNode NULL() {
            return tokenNode(QLParser.NULL);
        }
        
        public BoolenLiteralContext boolenLiteral() {
            return boolen;
        }
        
        public DoubleQuoteStringLiteralContext doubleQuoteStringLiteral() {
            return doubleQuoteString;
        }
        
        @Override
        public <T> T accept(QLParserBaseVisitor<T> visitor) {
            return visitor.visitLiteral(this);
        }
    }
    
    public static class DoubleQuoteStringLiteralContext extends RuleContext {
        private TerminalNode staticCharacters;
        
        public TerminalNode StaticStringCharacters() {
            return staticCharacters;
        }
        
        @Override
        public <T> T accept(QLParserBaseVisitor<T> visitor) {
            return visitor.visitDoubleQuoteStringLiteral(this);
        }
    }
    
    public static class StringExpressionContext extends RuleContext {
        private TerminalNode start;
        
        private ExpressionContext expression;
        
        private TerminalNode selectorVariable;
        
        public TerminalNode DyStrExprStart() {
            return start;
        }
        
        public ExpressionContext expression() {
            return expression;
        }
        
        public TerminalNode SelectorVariable_VANME() {
            return selectorVariable;
        }
        
        @Override
        public <T> T accept(QLParserBaseVisitor<T> visitor) {
            return visitor.visitStringExpression(this);
        }
    }
    
    public static class BoolenLiteralContext extends RuleContext {
        public TerminalNode TRUE() {
            return tokenNode(QLParser.TRUE);
        }
        
        public TerminalNode FALSE() {
            return tokenNode(QLParser.FALSE);
        }
        
        @Override
        public <T> T accept(QLParserBaseVisitor<T> visitor) {
            return visitor.visitBoolenLiteral(this);
        }
    }
    
    public static class LambdaExprContext extends PrimaryNoFixNonPathableContext {
        private LambdaParametersContext lambdaParameters;
        
        private TerminalNode arrow;
        
        private BlockStatementsContext blockStatements;
        
        private ExpressionContext expression;
        
        public LambdaParametersContext lambdaParameters() {
            return lambdaParameters;
        }
        
        public TerminalNode ARROW() {
            return arrow;
        }
        
        public BlockStatementsContext blockStatements() {
            return blockStatements;
        }
        
        public ExpressionContext expression() {
            return expression;
        }
        
        @Override
        public <T> T accept(QLParserBaseVisitor<T> visitor) {
            return visitor.visitLambdaExpr(this);
        }
    }
    
    public static class LambdaParametersContext extends RuleContext {
        private VarIdContext varId;
        
        private FormalOrInferredParameterListContext params;
        
        public VarIdContext varId() {
            return varId;
        }
        
        public FormalOrInferredParameterListContext formalOrInferredParameterList() {
            return params;
        }
        
        @Override
        public <T> T accept(QLParserBaseVisitor<T> visitor) {
            return visitor.visitLambdaParameters(this);
        }
    }
    
    public static class FormalOrInferredParameterListContext extends RuleContext {
        private final List<FormalOrInferredParameterContext> params = new ArrayList<>();
        
        public List<FormalOrInferredParameterContext> formalOrInferredParameter() {
            return params;
        }
        
        @Override
        public <T> T accept(QLParserBaseVisitor<T> visitor) {
            return visitor.visitFormalOrInferredParameterList(this);
        }
    }
    
    public static class FormalOrInferredParameterContext extends RuleContext {
        private DeclTypeContext declType;
        
        private VarIdContext varId;
        
        public DeclTypeContext declType() {
            return declType;
        }
        
        public VarIdContext varId() {
            return varId;
        }
        
        @Override
        public <T> T accept(QLParserBaseVisitor<T> visitor) {
            return visitor.visitFormalOrInferredParameter(this);
        }
    }
    
    public abstract static class ImportDeclarationContext extends RuleContext {
        protected final List<VarIdContext> varIds = new ArrayList<>();
        
        public List<VarIdContext> varId() {
            return varIds;
        }
    }
    
    public static class ImportClsContext extends ImportDeclarationContext {
        @Override
        public <T> T accept(QLParserBaseVisitor<T> visitor) {
            return visitor.visitImportCls(this);
        }
    }
    
    public static class ImportPackContext extends ImportDeclarationContext {
        @Override
        public <T> T accept(QLParserBaseVisitor<T> visitor) {
            return visitor.visitImportPack(this);
        }
    }
    
    public static class OpIdContext extends RuleContext {
        @Override
        public <T> T accept(QLParserBaseVisitor<T> visitor) {
            return visitor.visitOpId(this);
        }
    }
    
    public static class VarIdContext extends RuleContext {
        @Override
        public <T> T accept(QLParserBaseVisitor<T> visitor) {
            return visitor.visitVarId(this);
        }
    }
    
    public static class SwitchExprContext extends PrimaryNoFixNonPathableContext {
        private TerminalNode switchToken;
        
        private ExpressionContext expression;
        
        private SwitchCaseGroupsContext groups;
        
        public TerminalNode SWITCH() {
            return switchToken;
        }
        
        public ExpressionContext expression() {
            return expression;
        }
        
        public SwitchCaseGroupsContext switchCaseGroups() {
            return groups;
        }
        
        @Override
        public <T> T accept(QLParserBaseVisitor<T> visitor) {
            return visitor.visitSwitchExpr(this);
        }
    }
    
    public static class SwitchCaseGroupsContext extends RuleContext {
        private final List<SwitchCaseGroupContext> groups = new ArrayList<>();
        
        public List<SwitchCaseGroupContext> switchCaseGroup() {
            return groups;
        }
        
        @Override
        public <T> T accept(QLParserBaseVisitor<T> visitor) {
            return visitor.visitSwitchCaseGroups(this);
        }
    }
    
    public abstract static class SwitchCaseGroupContext extends RuleContext {
    }
    
    public static class SwitchStatementGroupContext extends SwitchCaseGroupContext {
        private SwitchLabelsContext labels;
        
        private BlockStatementsContext blockStatements;
        
        public SwitchLabelsContext switchLabels() {
            return labels;
        }
        
        public BlockStatementsContext blockStatements() {
            return blockStatements;
        }
        
        @Override
        public <T> T accept(QLParserBaseVisitor<T> visitor) {
            return visitor.visitSwitchStatementGroup(this);
        }
    }
    
    public static class SwitchExprGroupContext extends SwitchCaseGroupContext {
        private SwitchExpressionLabelContext label;
        
        private ExpressionContext expression;
        
        public SwitchExpressionLabelContext switchExpressionLabel() {
            return label;
        }
        
        public ExpressionContext expression() {
            return expression;
        }
        
        @Override
        public <T> T accept(QLParserBaseVisitor<T> visitor) {
            return visitor.visitSwitchExprGroup(this);
        }
    }
    
    public static class SwitchLabelsContext extends RuleContext {
        private final List<SwitchLabelContext> labels = new ArrayList<>();
        
        public List<SwitchLabelContext> switchLabel() {
            return labels;
        }
        
        @Override
        public <T> T accept(QLParserBaseVisitor<T> visitor) {
            return visitor.visitSwitchLabels(this);
        }
    }
    
    public static class SwitchLabelContext extends RuleContext {
        private TerminalNode caseToken;
        
        private TerminalNode defaultToken;
        
        private ExpressionContext expression;
        
        public TerminalNode CASE() {
            return caseToken;
        }
        
        public TerminalNode DEFAULT() {
            return defaultToken;
        }
        
        public ExpressionContext expression() {
            return expression;
        }
        
        @Override
        public <T> T accept(QLParserBaseVisitor<T> visitor) {
            return visitor.visitSwitchLabel(this);
        }
    }
    
    public static class SwitchExpressionLabelContext extends RuleContext {
        private TerminalNode caseToken;
        
        private TerminalNode defaultToken;
        
        private ExpressionListContext expressionList;
        
        public TerminalNode CASE() {
            return caseToken;
        }
        
        public TerminalNode DEFAULT() {
            return defaultToken;
        }
        
        public ExpressionListContext expressionList() {
            return expressionList;
        }
        
        @Override
        public <T> T accept(QLParserBaseVisitor<T> visitor) {
            return visitor.visitSwitchExpressionLabel(this);
        }
    }
    
    public static class ExpressionListContext extends RuleContext {
        private final List<ExpressionContext> expressions = new ArrayList<>();
        
        public List<ExpressionContext> expression() {
            return expressions;
        }
        
        @Override
        public <T> T accept(QLParserBaseVisitor<T> visitor) {
            return visitor.visitExpressionList(this);
        }
    }
    
    public static class TryCatchExprContext extends PrimaryNoFixNonPathableContext {
        private TerminalNode tryToken;
        
        private BlockStatementsContext blockStatements;
        
        private TryCatchesContext tryCatches;
        
        private TryFinallyContext tryFinally;
        
        public TerminalNode TRY() {
            return tryToken;
        }
        
        public BlockStatementsContext blockStatements() {
            return blockStatements;
        }
        
        public TryCatchesContext tryCatches() {
            return tryCatches;
        }
        
        public TryFinallyContext tryFinally() {
            return tryFinally;
        }
        
        @Override
        public <T> T accept(QLParserBaseVisitor<T> visitor) {
            return visitor.visitTryCatchExpr(this);
        }
    }
}
