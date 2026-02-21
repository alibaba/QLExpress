package com.alibaba.qlexpress4.parser.ast;

import java.util.List;

public class SwitchCaseNode {
    private final ExpressionNode condition;
    
    private final List<StatementNode> statements;
    
    public SwitchCaseNode(ExpressionNode condition, List<StatementNode> statements) {
        this.condition = condition;
        this.statements = statements;
    }
    
    public ExpressionNode getCondition() {
        return condition;
    }
    
    public List<StatementNode> getStatements() {
        return statements;
    }
}
