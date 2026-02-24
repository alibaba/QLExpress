package com.alibaba.qlexpress4.parser.ast;

import java.util.List;
import java.util.Map;

public class MapLiteralNode extends ASTNode implements ExpressionNode {
    private final List<MapEntryNode> entries;
    
    public MapLiteralNode(int line, int column, int startPosition, String source, List<MapEntryNode> entries) {
        super(line, column, startPosition, source);
        this.entries = entries;
    }
    
    @Override
    public <R, C> R accept(ASTVisitor<R, C> visitor, C context)
        throws Exception {
        return visitor.visit(this, context);
    }
    
    public List<MapEntryNode> getEntries() {
        return entries;
    }
}
