package com.ql.util.express.instruction.detail;

import java.util.List;

import com.ql.util.express.RunEnvironment;

public class InstructionReturn extends Instruction {
    private final boolean haveReturnValue;

    public InstructionReturn(boolean haveReturnValue) {
        this.haveReturnValue = haveReturnValue;
    }

    @Override
    public void execute(RunEnvironment environment, List<String> errorList) throws Exception {
        //目前的模式，不需要执行任何操作
        if (this.haveReturnValue) {
            environment.quitExpress(environment.pop().getObject(environment.getContext()));
        } else {
            environment.quitExpress();
        }
        environment.gotoLastWhenReturn();
    }

    @Override
    public String toString() {
        if (this.haveReturnValue) {
            return "return [value]";
        } else {
            return "return";
        }
    }
}
