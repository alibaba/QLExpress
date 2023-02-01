package com.ql.util.express.instruction.detail;

import java.util.List;

import com.ql.util.express.InstructionSetContext;
import com.ql.util.express.RunEnvironment;

public class InstructionCloseNewArea extends Instruction {
    @Override
    public void execute(RunEnvironment environment, List<String> errorList) {
        //目前的模式，不需要执行任何操作
        environment.setContext((InstructionSetContext)environment.getContext().getParent());
        environment.programPointAddOne();
    }

    @Override
    public String toString() {
        return "closeNewArea";
    }
}
