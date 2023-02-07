package com.ql.util.express.instruction.detail;

import java.util.List;

import com.ql.util.express.RunEnvironment;

public class InstructionClearDataStack extends Instruction {
    @Override
    public void execute(RunEnvironment environment, List<String> errorList) {
        // 目前的模式，不需要执行任何操作
        environment.clearDataStack();
        environment.programPointAddOne();
    }

    @Override
    public String toString() {
        return "clearDataStack";
    }
}

