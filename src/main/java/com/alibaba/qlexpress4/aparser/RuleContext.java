package com.alibaba.qlexpress4.aparser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RuleContext implements ParseTree {
    protected final List<ParseTree> children = new ArrayList<>();
    
    protected Token start;
    
    protected Token stop;
    
    public void addChild(ParseTree child) {
        if (child == null) {
            return;
        }
        children.add(child);
        if (child instanceof TerminalNode) {
            setBounds(((TerminalNode)child).getSymbol());
        }
        else if (child instanceof RuleContext) {
            RuleContext ruleChild = (RuleContext)child;
            if (ruleChild.getStart() != null) {
                setBounds(ruleChild.getStart());
            }
            if (ruleChild.getStop() != null) {
                setBounds(ruleChild.getStop());
            }
        }
    }
    
    public void addToken(Token token) {
        addChild(new TerminalNode(token));
    }
    
    public List<ParseTree> children() {
        return Collections.unmodifiableList(children);
    }
    
    public boolean isEmpty() {
        return children.isEmpty();
    }
    
    public int getChildCount() {
        return children.size();
    }
    
    public ParseTree getChild(int i) {
        return children.get(i);
    }
    
    public <T extends RuleContext> T getChild(Class<T> type, int index) {
        int seen = 0;
        for (ParseTree child : children) {
            if (type.isInstance(child)) {
                if (seen == index) {
                    return type.cast(child);
                }
                seen++;
            }
        }
        return null;
    }
    
    public <T extends RuleContext> List<T> getRuleContexts(Class<T> type) {
        List<T> result = new ArrayList<>();
        for (ParseTree child : children) {
            if (type.isInstance(child)) {
                result.add(type.cast(child));
            }
        }
        return result;
    }
    
    public Token getStart() {
        return start;
    }
    
    public Token getStop() {
        return stop;
    }
    
    public void setStart(Token start) {
        this.start = start;
        if (this.stop == null) {
            this.stop = start;
        }
    }
    
    public void setStop(Token stop) {
        this.stop = stop;
        if (this.start == null) {
            this.start = stop;
        }
    }
    
    protected TerminalNode tokenNode(int type) {
        for (ParseTree child : children) {
            if (child instanceof TerminalNode && ((TerminalNode)child).getSymbol().getType() == type) {
                return (TerminalNode)child;
            }
        }
        return null;
    }
    
    protected List<TerminalNode> tokenNodes(int type) {
        List<TerminalNode> result = new ArrayList<>();
        for (ParseTree child : children) {
            if (child instanceof TerminalNode && ((TerminalNode)child).getSymbol().getType() == type) {
                result.add((TerminalNode)child);
            }
        }
        return result;
    }
    
    private void setBounds(Token token) {
        if (token == null) {
            return;
        }
        if (start == null || token.getStartIndex() < start.getStartIndex()) {
            start = token;
        }
        if (stop == null || token.getStopIndex() >= stop.getStopIndex()) {
            stop = token;
        }
    }
    
    @Override
    public <T> T accept(QLParserBaseVisitor<T> visitor) {
        return visitor.visitChildren(this);
    }
    
    @Override
    public String getText() {
        StringBuilder builder = new StringBuilder();
        for (ParseTree child : children) {
            builder.append(child.getText());
        }
        return builder.toString();
    }
    
    public String toStringTree() {
        if (children.isEmpty()) {
            return getText();
        }
        StringBuilder builder = new StringBuilder();
        builder.append('(').append(getClass().getSimpleName().replace("Context", ""));
        for (ParseTree child : children) {
            builder.append(' ');
            if (child instanceof RuleContext) {
                builder.append(((RuleContext)child).toStringTree());
            }
            else {
                builder.append(child.getText());
            }
        }
        builder.append(')');
        return builder.toString();
    }
}
