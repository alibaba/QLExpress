package com.alibaba.qlexpress4.parser.tree;

import com.alibaba.qlexpress4.parser.Token;

import java.util.List;
import java.util.Map;

/**
 * map literal expression
 * Author: DQinYuan
 */
public class MapExpr extends Expr {

    private final List<Map.Entry<String, Expr>> entries;

    public MapExpr(Token keyToken, List<Map.Entry<String, Expr>> entries) {
        super(keyToken);
        this.entries = entries;
    }

    @Override
    public <R, C> R accept(QLProgramVisitor<R, C> visitor, C context) {
        return visitor.visit(this, context);
    }

    public List<Map.Entry<String, Expr>> getEntries() {
        return entries;
    }
}
