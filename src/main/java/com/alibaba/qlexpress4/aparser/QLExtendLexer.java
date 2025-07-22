package com.alibaba.qlexpress4.aparser;

import com.alibaba.qlexpress4.exception.QLErrorCodes;
import com.alibaba.qlexpress4.exception.QLException;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;

public class QLExtendLexer extends QLexer {
    
    private final String script;
    
    private final InterpolationMode interpolationMode;
    
    private final String selectorStart;
    
    private final String selectorEnd;
    
    public QLExtendLexer(CharStream input, String script, InterpolationMode interpolationMode, String selectorStart,
        String selectorEnd) {
        super(input);
        this.script = script;
        this.interpolationMode = interpolationMode;
        this.selectorStart = selectorStart;
        this.selectorEnd = selectorEnd;
    }
    
    @Override
    protected InterpolationMode getInterpolationMode() {
        return interpolationMode;
    }
    
    @Override
    protected String getSelectorStart() {
        return selectorStart;
    }
    
    @Override
    protected void consumeSelectorVariable() {
        StringBuilder t = new StringBuilder();
        int selectorEndLength = selectorEnd.length();
        char lastCharOfSelector = selectorEnd.charAt(selectorEndLength - 1);
        
        t.ensureCapacity(selectorEndLength * 2);
        
        while (true) {
            int curChInt = _input.LA(1);
            if (curChInt == Token.EOF || curChInt == '\n') {
                // mismatch
                throw QLException.reportScannerErr(script,
                    this._tokenStartCharIndex,
                    this._tokenStartLine,
                    this._tokenStartCharPositionInLine,
                    t.toString(),
                    QLErrorCodes.SYNTAX_ERROR.name(),
                    "unterminated selector");
            }
            char curCh = (char)curChInt;
            t.append(curCh);
            _input.consume();
            
            if (curCh == lastCharOfSelector && t.length() >= selectorEndLength) {
                if (checkEndsWith(t, selectorEnd)) {
                    // match
                    String text = t.toString();
                    setText(text.substring(0, text.length() - selectorEndLength));
                    popMode();
                    break;
                }
            }
        }
    }
    
    private boolean checkEndsWith(StringBuilder sb, String suffix) {
        int suffixLength = suffix.length();
        int sbLength = sb.length();
        
        for (int i = 0; i < suffixLength; i++) {
            if (sb.charAt(sbLength - suffixLength + i) != suffix.charAt(i)) {
                return false;
            }
        }
        return true;
    }
}
