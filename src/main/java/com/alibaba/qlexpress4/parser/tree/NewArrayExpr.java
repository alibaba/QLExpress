package com.alibaba.qlexpress4.parser.tree;

import com.alibaba.qlexpress4.parser.Token;

import java.util.List;

/**
 * new Type[] {1,2,3}
 * Author: DQinYuan
 */
public class NewArrayExpr extends Expr {

    private final Class<?> clz;

    private final List<Expr> values;

    public NewArrayExpr(Token keyToken, Class<?> clz, List<Expr> values) {
        super(keyToken);
        this.clz = clz;
        this.values = values;
    }

    @Override
    public <R, C> R accept(QLProgramVisitor<R, C> visitor, C context) {
        return visitor.visit(this, context);
    }

    public Class<?> getClz() {
        return clz;
    }

    public List<Expr> getValues() {
        return values;
    }
}
