package com.alibaba.qlexpress4.parser.ast;

public class ParameterNode {
    private final String typeName;
    private final String parameterName;

    public ParameterNode(String typeName, String parameterName) {
        this.typeName = typeName;
        this.parameterName = parameterName;
    }

    public String getTypeName() { return typeName; }
    public String getParameterName() { return parameterName; }
}
