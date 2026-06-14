package com.alibaba.qlexpress4.aparser;

public class TerminalNode implements ParseTree {
    private final Token symbol;
    
    public TerminalNode(Token symbol) {
        this.symbol = symbol;
    }
    
    public Token getSymbol() {
        return symbol;
    }
    
    @Override
    public <T> T accept(QLParserBaseVisitor<T> visitor) {
        return visitor.visitTerminal(this);
    }
    
    @Override
    public String getText() {
        return symbol.getText();
    }
    
    @Override
    public String toString() {
        return getText();
    }
}
