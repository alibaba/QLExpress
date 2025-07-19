package com.alibaba.qlexpress4.aparser;

import org.antlr.v4.runtime.BufferedTokenStream;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenSource;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.misc.Interval;

public class AliasTokenStream implements TokenStream {
    
    private final BufferedTokenStream stream;
    
    private final ParserOperatorManager operatorManager;
    
    public AliasTokenStream(BufferedTokenStream stream, ParserOperatorManager operatorManager) {
        this.stream = stream;
        this.operatorManager = operatorManager;
    }
    
    @Override
    public Token LT(int k) {
        return SyntaxTreeFactory.preHandleToken(stream.LT(k), operatorManager);
    }
    
    @Override
    public Token get(int index) {
        return SyntaxTreeFactory.preHandleToken(stream.get(index), operatorManager);
    }
    
    @Override
    public TokenSource getTokenSource() {
        return new AliasTokenSource(stream.getTokenSource(), operatorManager);
    }
    
    @Override
    public String getText(Interval interval) {
        return stream.getText(interval);
    }
    
    @Override
    public String getText() {
        return stream.getText();
    }
    
    @Override
    public String getText(RuleContext ctx) {
        return stream.getText(ctx);
    }
    
    @Override
    public String getText(Token start, Token stop) {
        return stream.getText(start, stop);
    }
    
    @Override
    public void consume() {
        stream.consume();
    }
    
    @Override
    public int LA(int i) {
        return SyntaxTreeFactory.preHandleToken(LT(i), operatorManager).getType();
    }
    
    @Override
    public int mark() {
        return stream.mark();
    }
    
    @Override
    public void release(int marker) {
        stream.release(marker);
    }
    
    @Override
    public int index() {
        return stream.index();
    }
    
    @Override
    public void seek(int index) {
        stream.seek(index);
    }
    
    @Override
    public int size() {
        return stream.size();
    }
    
    @Override
    public String getSourceName() {
        return stream.getSourceName();
    }
}
