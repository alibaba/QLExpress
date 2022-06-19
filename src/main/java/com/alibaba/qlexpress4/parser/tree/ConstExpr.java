package com.alibaba.qlexpress4.parser.tree;

import com.alibaba.qlexpress4.parser.Token;

/**
 * literal, like number, string
 */
public class ConstExpr extends Expr {

    private final Object constValue;

    public ConstExpr(Token keyToken, Object constValue) {
        super(keyToken);
        this.constValue = constValue;
    }

    public Object getConstValue() {
        return constValue;
    }

    @Override
    public <R, C> R accept(QLProgramVisitor<R, C> visitor, C context) {
        return visitor.visit(this, context);
    }
}
