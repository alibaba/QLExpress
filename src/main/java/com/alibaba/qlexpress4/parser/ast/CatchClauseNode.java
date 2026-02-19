package com.alibaba.qlexpress4.parser.ast;

import java.util.List;

public class CatchClauseNode {
    private final List<String> exceptionTypes;
    private final String variableName;
    private final BlockNode body;

    public CatchClauseNode(List<String> exceptionTypes, String variableName, BlockNode body) {
        this.exceptionTypes = exceptionTypes;
        this.variableName = variableName;
        this.body = body;
    }

    public List<String> getExceptionTypes() { return exceptionTypes; }
    public String getVariableName() { return variableName; }
    public BlockNode getBody() { return body; }
}
