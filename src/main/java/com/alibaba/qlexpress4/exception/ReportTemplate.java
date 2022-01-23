package com.alibaba.qlexpress4.exception;

import com.alibaba.qlexpress4.parser.Token;

import java.text.MessageFormat;

public class ReportTemplate {

    private static final String REPORT_TEMPLATE = "[Error: {0}]\n[Near: {1}]\n{2}\n[Line: {3}, Column: {4}]";

    public static String report(String script, Token token, String reason) {
        return report(script, token.getPos(), token.getLine(), token.getCol(), token.getLexeme(), reason);
    }

    public static String report(String script, int tokenPos,
                                int tokenLine, int tokenCol, String lexeme, String reason) {
        int tokenStartPos = tokenPos - lexeme.length();
        int startReportPos = Math.max(tokenStartPos - 10, 0);
        int endReportPos = Math.min(tokenPos + 10, script.length());

        StringBuilder reportCode = new StringBuilder();
        for (int i = startReportPos; i < endReportPos; i++) {
            char codeChar = script.charAt(i);
            reportCode.append(codeChar < ' '? ' ': codeChar);
        }

        StringBuilder carteBuilder = new StringBuilder().append("       ");
        for (int i = startReportPos; i < tokenStartPos; i++) {
            carteBuilder.append(' ');
        }
        for (int i = 0; i < lexeme.length(); i++) {
            carteBuilder.append('^');
        }

        return MessageFormat.format(REPORT_TEMPLATE, reason, reportCode.toString(),
                carteBuilder.toString(), tokenLine, tokenCol - lexeme.length());
    }

}
