package com.alibaba.qlexpress4.parser;

import com.alibaba.qlexpress4.parser.tree.Stmt;
import com.alibaba.qlexpress4.runtime.instruction.QLInstruction;

/**
 * Author: DQinYuan
 */
public class MacroDefineV2 {

    private final QLInstruction[] instructions;

    private final Stmt lastStmt;

    private final int maxStackSize;

    public MacroDefineV2(QLInstruction[] instructions, Stmt lastStmt, int maxStackSize) {
        this.instructions = instructions;
        this.lastStmt = lastStmt;
        this.maxStackSize = maxStackSize;
    }

    public Stmt getLastStmt() {
        return lastStmt;
    }

    public QLInstruction[] getInstructions() {
        return instructions;
    }

    public int getMaxStackSize() {
        return maxStackSize;
    }
}
