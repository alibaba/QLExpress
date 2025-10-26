package com.alibaba.qlexpress4.exception;

import java.text.MessageFormat;

/**
 * Author: DQinYuan
 */
public class ExMessageUtil {
    
    private static final String REPORT_TEMPLATE = "[Error {0}: {1}]\n[Near: {2}]\n{3}\n[Line: {4}, Column: {5}]";
    
    private static final int SNIPPET_EXTENSION_LEN = 20;
    
    public static class ExMessage {
        private final String message;
        
        private final String snippet;
        
        public ExMessage(String message, String snippet) {
            this.message = message;
            this.snippet = snippet;
        }
        
        public String getMessage() {
            return message;
        }
        
        public String getSnippet() {
            return snippet;
        }
    }
    
    public static ExMessage format(String script, int tokenStartPos, int tokenLine, int tokenCol, String lexeme,
        String errorCode, String reason) {
        int startReportPos = Math.max(tokenStartPos - SNIPPET_EXTENSION_LEN, 0);
        int endReportPos = Math.min(tokenStartPos + lexeme.length() + SNIPPET_EXTENSION_LEN, script.length());
        
        StringBuilder snippetBuilder = new StringBuilder();
        if (startReportPos > 0) {
            snippetBuilder.append("...");
        }
        for (int i = startReportPos; i < endReportPos; i++) {
            char codeChar = script.charAt(i);
            snippetBuilder.append(codeChar < ' ' ? ' ' : codeChar);
        }
        if (endReportPos < script.length()) {
            snippetBuilder.append("...");
        }
        
        StringBuilder carteBuilder = new StringBuilder().append("       ");
        if (startReportPos > 0) {
            carteBuilder.append("   ");
        }
        for (int i = startReportPos; i < tokenStartPos; i++) {
            carteBuilder.append(' ');
        }
        for (int i = 0; i < lexeme.length(); i++) {
            carteBuilder.append('^');
        }
        
        String snippet = snippetBuilder.toString();
        String message = MessageFormat
            .format(REPORT_TEMPLATE, errorCode, reason, snippet, carteBuilder.toString(), tokenLine, tokenCol);
        return new ExMessage(message, snippet);
    }
    
}
