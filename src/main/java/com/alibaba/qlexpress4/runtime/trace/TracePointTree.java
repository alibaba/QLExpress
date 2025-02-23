package com.alibaba.qlexpress4.runtime.trace;

import com.alibaba.qlexpress4.utils.PrintlnUtils;

import java.util.List;

public class TracePointTree {
    private final TraceType type;

    private final String token;

    private final List<TracePointTree> children;

    private final int line;

    private final int col;

    private final int position;

    public TracePointTree(TraceType type, String token, List<TracePointTree> children, int line, int col, int position) {
        this.type = type;
        this.token = token;
        this.children = children;
        this.line = line;
        this.col = col;
        this.position = position;
    }

    public String toPrettyString(int indent) {
        StringBuilder nodeStringBuilder = new StringBuilder(
                PrintlnUtils.buildIndentString(indent, type + " " + token)
        ).append('\n');
        for (TracePointTree child : children) {
            nodeStringBuilder.append(child.toPrettyString(indent + 2));
        }
        return nodeStringBuilder.toString();
    }

    public TraceType getType() {
        return type;
    }

    public String getToken() {
        return token;
    }

    public List<TracePointTree> getChildren() {
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
