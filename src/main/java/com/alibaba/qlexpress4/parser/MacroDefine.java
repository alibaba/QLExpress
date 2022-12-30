package com.alibaba.qlexpress4.parser;

import com.alibaba.qlexpress4.parser.tree.Stmt;

/**
 * Author: DQinYuan
 */
public class MacroDefine {

    private final QvmInstructionGenerator.NodeInstructions nodeInstructions;

    private final Stmt lastStmt;

    public MacroDefine(QvmInstructionGenerator.NodeInstructions nodeInstructions, Stmt lastStmt) {
        this.nodeInstructions = nodeInstructions;
        this.lastStmt = lastStmt;
    }

    public QvmInstructionGenerator.NodeInstructions getNodeInstructions() {
        return nodeInstructions;
    }

    public Stmt getLastStmt() {
        return lastStmt;
    }
}
