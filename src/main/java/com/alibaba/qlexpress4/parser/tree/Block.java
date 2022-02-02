package com.alibaba.qlexpress4.parser.tree;

import com.alibaba.qlexpress4.parser.Token;

import java.util.List;

public class Block extends Stmt {

    private final List<Stmt> stmtList;

    public Block(Token keyToken, List<Stmt> stmtList) {
        super(keyToken);
        this.stmtList = stmtList;
    }

    public List<Stmt> getStmtList() {
        return stmtList;
    }
}
