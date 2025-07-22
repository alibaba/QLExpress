package com.alibaba.qlexpress4.aparser;

import org.antlr.v4.runtime.DefaultErrorStrategy;
import org.antlr.v4.runtime.InputMismatchException;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.Vocabulary;
import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.misc.IntervalSet;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: DQinYuan
 */
public class QLErrorStrategy extends DefaultErrorStrategy {
    
    @Override
    protected void reportInputMismatch(Parser recognizer, InputMismatchException e) {
        String msg = "mismatched input " + getTokenErrorDisplay(e.getOffendingToken()) + " expecting "
            + intervalSetString(e.getExpectedTokens(), recognizer.getVocabulary());
        recognizer.notifyErrorListeners(e.getOffendingToken(), msg, e);
    }
    
    private static String intervalSetString(IntervalSet expectedTokens, Vocabulary vocabulary) {
        if (expectedTokens.getIntervals() == null || expectedTokens.getIntervals().isEmpty()) {
            return "{}";
        }
        
        List<String> eleNames = new ArrayList<>();
        for (Interval I : expectedTokens.getIntervals()) {
            int a = I.a;
            int b = I.b;
            for (int i = a; i <= b; i++) {
                if (i != QLexer.NEWLINE) {
                    eleNames.add(elementName(vocabulary, i));
                }
            }
        }
        if (eleNames.isEmpty()) {
            return "{}";
        }
        if (eleNames.size() == 1) {
            return eleNames.get(0);
        }
        return String.join(",", eleNames);
    }
    
    private static String elementName(Vocabulary vocabulary, int a) {
        if (a == Token.EOF) {
            return "<EOF>";
        }
        else if (a == Token.EPSILON) {
            return "<EPSILON>";
        }
        else {
            return vocabulary.getDisplayName(a);
        }
    }
}
