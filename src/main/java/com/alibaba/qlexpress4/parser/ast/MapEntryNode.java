package com.alibaba.qlexpress4.parser.ast;

public class MapEntryNode {
    private final ExpressionNode key;
    private final ExpressionNode value;

    public MapEntryNode(ExpressionNode key, ExpressionNode value) {
        this.key = key;
        this.value = value;
    }

    public ExpressionNode getKey() { return key; }
    public ExpressionNode getValue() { return value; }
}
