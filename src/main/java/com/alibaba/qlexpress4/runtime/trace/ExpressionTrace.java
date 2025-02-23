package com.alibaba.qlexpress4.runtime.trace;

import com.alibaba.qlexpress4.utils.PrintlnUtils;

import java.util.List;

public class ExpressionTrace {

    public ExpressionTrace(TraceType type, String token, List<ExpressionTrace> children,
                           Integer line, Integer col, Integer position) {
        this.type = type;
        this.token = token;
        this.children = children;
        this.line = line;
        this.col = col;
        this.position = position;
    }

    private final TraceType type;

    private final String token;

    private Object value;

    private boolean evaluated;

    private final List<ExpressionTrace> children;

    private final int line;

    private final int col;

    private final int position;

    public String toPrettyString(int indent) {
        StringBuilder nodeStringBuilder = new StringBuilder(
                PrintlnUtils.buildIndentString(indent, type + " " + token + " " + (evaluated ? value : ""))
        ).append('\n');
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
