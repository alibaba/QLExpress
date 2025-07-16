package com.alibaba.qlexpress4.aparser;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;

public class QLExtendLexer extends QLexer {

    private final InterpolationMode interpolationMode;

    private final String selectorStart;

    private final String selectorEnd;

    public QLExtendLexer(CharStream input, InterpolationMode interpolationMode, String selectorStart, String selectorEnd) {
        super(input);
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
        
        while(true) {
            int curChInt = _input.LA(1);
            if (curChInt == Token.EOF) {
                // mismatch
                setType(CATCH_ALL);
                break;
            }
            char curCh = (char) curChInt;
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
