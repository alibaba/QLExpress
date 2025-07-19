package com.alibaba.qlexpress4.runtime.trace;

import com.alibaba.qlexpress4.utils.PrintlnUtils;

import java.util.List;

public class ExpressionTrace {
    
    public ExpressionTrace(TraceType type, String token, List<ExpressionTrace> children, Integer line, Integer col,
        Integer position) {
        this.type = type;
        this.token = token;
        this.children = children;
        this.line = line;
        this.col = col;
        this.position = position;
    }
    
    private final TraceType type;
    
    private final String token;
    
    /**
     * Intermediate calculation result of this trace point
     */
    private Object value;
    
    /**
     * true if this point is evaluated in this execution
     * false if short-circuited
     */
    private boolean evaluated;
    
    private final List<ExpressionTrace> children;
    
    /**
     * The corresponding line number in the source code
     */
    private final int line;
    
    /**
     * The corresponding col number in the source code
     */
    private final int col;
    
    /**
     * The corresponding position of character in the source code string
     */
    private final int position;
    
    public String toPrettyString(int indent) {
        StringBuilder nodeStringBuilder = new StringBuilder(
            PrintlnUtils.buildIndentString(indent, type + " " + token + " " + (evaluated ? value : ""))).append('\n');
        for (ExpressionTrace child : children) {
            nodeStringBuilder.append(child.toPrettyString(indent + 2));
        }
        return nodeStringBuilder.toString();
    }
    
    public void valueEvaluated(Object value) {
        this.value = value;
        this.evaluated = true;
    }
    
    public TraceType getType() {
        return type;
    }
    
    public String getToken() {
        return token;
    }
    
    public Object getValue() {
        return value;
    }
    
    public boolean isEvaluated() {
        return evaluated;
    }
    
    public List<ExpressionTrace> getChildren() {
        return children;
    }
    
    public int getLine() {
        return line;
    }
    
    public int getCol() {
        return col;
    }
    
    public int getPosition() {
        return position;
    }
}
