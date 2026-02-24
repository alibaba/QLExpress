package com.alibaba.qlexpress4.parser.ast;

public class ImportNode extends ASTNode implements StatementNode {
    private final String importPath;
    
    private final boolean isWildcard;
    
    public ImportNode(int line, int column, int startPosition, String source, String importPath, boolean isWildcard) {
        super(line, column, startPosition, source);
        this.importPath = importPath;
        this.isWildcard = isWildcard;
    }
    
    @Override
    public <R, C> R accept(ASTVisitor<R, C> visitor, C context)
        throws Exception {
        return visitor.visit(this, context);
    }
    
    public String getImportPath() {
        return importPath;
    }
    
    public boolean isWildcard() {
        return isWildcard;
    }
}
