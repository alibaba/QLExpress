package com.alibaba.qlexpress4.parser.tree;

import com.alibaba.qlexpress4.parser.Token;

import java.util.List;

/**
 * new Type[1][2][3][][]
 * Author: DQinYuan
 */
public class MultiNewArrayExpr extends Expr {

    /**
     * Type[][].class
     */
    private final Class<?> clz;

    private final List<Expr> dims;

    public MultiNewArrayExpr(Token keyToken, Class<?> clz, List<Expr> dims) {
        super(keyToken);
        this.clz = clz;
        this.dims = dims;
    }

    @Override
    public <R, C> R accept(QLProgramVisitor<R, C> visitor, C context) {
        return visitor.visit(this, context);
    }

    public Class<?> getClz() {
        return clz;
    }

    public List<Expr> getDims() {
        return dims;
    }
}
