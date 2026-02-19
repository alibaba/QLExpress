package com.alibaba.qlexpress4.parser.ast;

import java.util.List;
import java.util.Collections;

public class SwitchNode extends ASTNode implements StatementNode {
    private final ExpressionNode value;
    private final List<SwitchCaseNode> cases;

    public SwitchNode(int line, int column, String source, ExpressionNode value, List<SwitchCaseNode> cases) {
        super(line, column, source);
        this.value = value;
        this.cases = cases != null ? cases : Collections.emptyList();
    }

    @Override
    public <R, C> R accept(ASTVisitor<R, C> visitor, C context) throws Exception {
        return visitor.visit(this, context);
    }

    public ExpressionNode getValue() { return value; }
    public List<SwitchCaseNode> getCases() { return cases; }
}
