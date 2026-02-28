package com.alibaba.qlexpress4.parser.ast;

import java.util.List;

public class TryCatchNode extends ASTNode implements ExpressionNode {
    private final BlockNode tryBlock;
    
    private final List<CatchClauseNode> catchClauses;
    
    private final BlockNode finallyBlock;
    
    public TryCatchNode(int line, int column, int startPosition, String source, BlockNode tryBlock,
        List<CatchClauseNode> catchClauses, BlockNode finallyBlock) {
        super(line, column, startPosition, source);
        this.tryBlock = tryBlock;
        this.catchClauses = catchClauses;
        this.finallyBlock = finallyBlock;
    }
    
    @Override
    public <R, C> R accept(ASTVisitor<R, C> visitor, C context)
        throws Exception {
        return visitor.visit(this, context);
    }
    
    public BlockNode getTryBlock() {
        return tryBlock;
    }
    
    public List<CatchClauseNode> getCatchClauses() {
        return catchClauses;
    }
    
    public BlockNode getFinallyBlock() {
        return finallyBlock;
    }
}
