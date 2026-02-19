package com.alibaba.qlexpress4.parser.ast;

public class MacroDefinitionNode extends ASTNode implements StatementNode {
    private final String macroName;
    private final BlockNode body;

    public MacroDefinitionNode(int line, int column, String source, String macroName, BlockNode body) {
        super(line, column, source);
        this.macroName = macroName;
        this.body = body;
    }

    @Override
    public <R, C> R accept(ASTVisitor<R, C> visitor, C context) throws Exception {
        return visitor.visit(this, context);
    }

    public String getMacroName() { return macroName; }
    public BlockNode getBody() { return body; }
}
