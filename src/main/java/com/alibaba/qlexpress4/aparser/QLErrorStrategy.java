package com.alibaba.qlexpress4.aparser;

import com.alibaba.qlexpress4.exception.QLErrorCodes;
import com.alibaba.qlexpress4.exception.QLException;
import org.antlr.v4.runtime.*;

/**
 * Author: DQinYuan
 */
public class QLErrorStrategy extends DefaultErrorStrategy {

    private static final String OPID_SYMBOL = "OPID";
    private static final String ID_SYMBOL = "ID";
    private static final String DOTMUL_SYMBOL = "DOTMUL";
    private static final String IMPORT_SYMBOL = "IMPORT";
    private static final String STATIC_SYMBOL = "STATIC";
    private static final String BLOCK_STATEMENTS_RULE = "blockStatements";
    private static final String IMPORT_DECLARATION_RULE = "importDeclaration";

    private final String script;

    public QLErrorStrategy(String script) {
        this.script = script;
    }

    @Override
    public void recover(Parser recognizer, RecognitionException e) {
        throwQException(recognizer, e.getOffendingToken());
    }

    @Override
    public Token recoverInline(Parser recognizer) throws RecognitionException {
        throwQException(recognizer, recognizer.getCurrentToken());
        return null;
    }

    private void throwQException(Parser recognizer, Token currentToken) {
        String symbolicName = recognizer.getVocabulary().getSymbolicName(currentToken.getType());
        String ruleName = recognizer.getRuleNames()[recognizer.getContext().getRuleIndex()];
        ParserRuleContext ruleContext = recognizer.getRuleContext();
        String lexeme = currentToken.getText();

        if (ruleContext instanceof QLParser.WhileStatementContext) {
            syntaxErrorThrow(currentToken, "whileStatement");
        } else if (ruleContext instanceof QLParser.MacroStatementContext) {
            syntaxErrorThrow(currentToken, "macroStatement");
        } else if (IMPORT_SYMBOL.equals(symbolicName)) {
            throw QLException.reportScannerErr(script, currentToken.getStartIndex(),
                    currentToken.getLine(), currentToken.getCharPositionInLine() + lexeme.length(),
                    lexeme, QLErrorCodes.IMPORT_STATEMENT_NOT_AT_BEGINNING.name(),
                    QLErrorCodes.IMPORT_STATEMENT_NOT_AT_BEGINNING.getErrorMsg());
        } else if (IMPORT_DECLARATION_RULE.equals(ruleName) && STATIC_SYMBOL.equals(symbolicName)) {
            throw QLException.reportScannerErr(script, currentToken.getStartIndex(),
                    currentToken.getLine(), currentToken.getCharPositionInLine() + lexeme.length(),
                    lexeme, QLErrorCodes.IMPORT_STATIC_NOT_SUPPORTED.name(),
                    QLErrorCodes.IMPORT_STATIC_NOT_SUPPORTED.getErrorMsg());
        } else if (BLOCK_STATEMENTS_RULE.equals(ruleName) &&
                (OPID_SYMBOL.equals(symbolicName) || ID_SYMBOL.equals(symbolicName) || DOTMUL_SYMBOL.equals(symbolicName))
        ) {
            throw QLException.reportScannerErr(script, currentToken.getStartIndex(),
                    currentToken.getLine(), currentToken.getCharPositionInLine() + lexeme.length(),
                    lexeme, QLErrorCodes.UNKNOWN_OPERATOR.name(),
                    QLErrorCodes.UNKNOWN_OPERATOR.getErrorMsg());
        } else if ("\"".equals(lexeme)) {
            syntaxErrorThrow(currentToken, "StringLiteral");
        } else if ("'".equals(lexeme)) {
            syntaxErrorThrow(currentToken, "QuoteStringLiteral");
        }

        syntaxErrorThrow(currentToken, ruleName);
    }

    private void syntaxErrorThrow(Token currentToken, String ruleName) {
        throw QLException.reportScannerErr(script, currentToken.getStartIndex(),
                currentToken.getLine(), currentToken.getCharPositionInLine() + currentToken.getText().length(),
                currentToken.getText(), "SYNTAX_ERROR",
                "invalid " + ruleName);
    }

    @Override
    public void sync(Parser recognizer) { }
}
