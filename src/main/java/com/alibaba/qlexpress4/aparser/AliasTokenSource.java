package com.alibaba.qlexpress4.aparser;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenFactory;
import org.antlr.v4.runtime.TokenSource;

public class AliasTokenSource implements TokenSource {

    private final TokenSource tokenSource;

    private final ParserOperatorManager operatorManager;

    AliasTokenSource(TokenSource tokenSource, ParserOperatorManager operatorManager) {
        this.tokenSource = tokenSource;
        this.operatorManager = operatorManager;
    }

    @Override
    public Token nextToken() {
        return SyntaxTreeFactory.preHandleToken(tokenSource.nextToken(), operatorManager);
    }

    @Override
    public int getLine() {
        return tokenSource.getLine();
    }

    @Override
    public int getCharPositionInLine() {
        return tokenSource.getCharPositionInLine();
    }

    @Override
    public CharStream getInputStream() {
        return tokenSource.getInputStream();
    }

    @Override
    public String getSourceName() {
        return tokenSource.getSourceName();
    }

    @Override
    public void setTokenFactory(TokenFactory<?> factory) {
        tokenSource.setTokenFactory(factory);
    }

    @Override
    public TokenFactory<?> getTokenFactory() {
        return tokenSource.getTokenFactory();
    }
}
