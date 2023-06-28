package com.alibaba.qlexpress4.aparser;

import com.alibaba.qlexpress4.runtime.instruction.QLInstruction;

import java.util.List;

/**
 * Author: DQinYuan
 */
public class MacroDefine {

    private final List<QLInstruction> instructions;

    private final QLGrammarParser.BlockStatementContext lastStmt;

    public MacroDefine(List<QLInstruction> instructions, QLGrammarParser.BlockStatementContext lastStmt) {
        this.instructions = instructions;
        this.lastStmt = lastStmt;
    }

    public List<QLInstruction> getMacroInstructions() {
        return instructions;
    }

    public QLGrammarParser.BlockStatementContext getLastStmt() {
        return lastStmt;
    }
}
