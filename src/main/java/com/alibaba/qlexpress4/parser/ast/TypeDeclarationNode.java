package com.alibaba.qlexpress4.parser.ast;

public class TypeDeclarationNode extends ASTNode implements StatementNode {
    private final String typeName;

    public TypeDeclarationNode(int line, int column, String source, String typeName) {
        super(line, column, source);
        this.typeName = typeName;
    }

    @Override
    public <R, C> R accept(ASTVisitor<R, C> visitor, C context) throws Exception {
        return visitor.visit(this, context);
    }

    public String getTypeName() { return typeName; }
}
