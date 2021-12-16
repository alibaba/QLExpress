package com.ql.util.express.instruction;

import com.ql.util.express.InstructionSet;

/**
 * TODO public field
 */
public class FunctionInstructionSet {
    public final String name;
    public final String type;
    public final InstructionSet instructionSet;

    public FunctionInstructionSet(String name, String type, InstructionSet instructionSet) {
        this.name = name;
        this.type = type;
        this.instructionSet = instructionSet;
    }
}
