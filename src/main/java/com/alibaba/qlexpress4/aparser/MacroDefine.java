package com.alibaba.qlexpress4.aparser;

import com.alibaba.qlexpress4.runtime.instruction.QLInstruction;

import java.util.List;

/**
 * Author: DQinYuan
 */
public class MacroDefine {

    private final List<QLInstruction> instructions;

    private final boolean lastStmtExpress;

    public MacroDefine(List<QLInstruction> instructions, boolean lastStmtExpress) {
        this.instructions = instructions;
        this.lastStmtExpress = lastStmtExpress;
    }

    public List<QLInstruction> getMacroInstructions() {
        return instructions;
    }

    public boolean isLastStmtExpress() {
        return lastStmtExpress;
    }
}
