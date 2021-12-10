package com.ql.util.express.instruction.detail;

import java.util.List;

import com.ql.util.express.RunEnvironment;

public class InstructionClearDataStack extends Instruction {
    private static final long serialVersionUID = 6286430548739444891L;

    @Override
    public void execute(RunEnvironment environment, List<String> errorList) {
        //目前的模式，不需要执行任何操作
        if (environment.isTrace() && log.isDebugEnabled()) {
            log.debug(this);
        }
        environment.clearDataStack();
        environment.programPointAddOne();
    }

    @Override
    public String toString() {
        return "clearDataStack";
    }
}

