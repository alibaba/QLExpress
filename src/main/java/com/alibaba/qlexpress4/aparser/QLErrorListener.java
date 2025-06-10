package com.alibaba.qlexpress4.aparser;

import com.alibaba.qlexpress4.exception.QLErrorCodes;
import com.alibaba.qlexpress4.exception.QLException;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.Token;

public class QLErrorListener extends BaseErrorListener {

    private final String script;

    public QLErrorListener(String script) {
        this.script = script;
    }

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer,
                            Object offendingSymbol,
                            int line,
                            int charPositionInLine,
                            String msg,
                            RecognitionException e)
    {
        Token currentToken = (Token) offendingSymbol;
        String tokenText = currentToken.getType() == Token.EOF? "<EOF>": currentToken.getText();
        String preHandledScript = currentToken.getType() == Token.EOF? script + "<EOF>": script;
        String preHandledMsg = errMsg(((Parser) recognizer).getContext(), currentToken, msg);

        throw QLException.reportScannerErr(preHandledScript, currentToken.getStartIndex(),
                currentToken.getLine(), currentToken.getCharPositionInLine(),
                tokenText, QLErrorCodes.SYNTAX_ERROR.name(), preHandledMsg);
    }

    private String errMsg(ParserRuleContext ruleContext, Token currentToken, String msg) {
        if ("'".equals(currentToken.getText()) || "\"".equals(currentToken.getText())
                || ruleContext.getRuleIndex() == QLParser.RULE_doubleQuoteStringLiteral) {
            return "unterminated string literal";
        }
        if ("import".equals(currentToken.getText())) {
            return "Import statement is not at the beginning of the file.";
        }
        if (ruleContext.getRuleIndex() == QLParser.RULE_importDeclaration && "static".equals(currentToken.getText())) {
            return "'import static' not supported temporarily";
        }
        return msg;
    }
}
