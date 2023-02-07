package com.ql.util.express.instruction.detail;

import java.util.List;

import com.ql.util.express.InstructionSetContext;
import com.ql.util.express.RunEnvironment;
import com.ql.util.express.instruction.OperateDataCacheManager;

public class InstructionOpenNewArea extends Instruction {
    @Override
    public void execute(RunEnvironment environment, List<String> errorList) {
        //目前的模式，不需要执行任何操作
        InstructionSetContext parentContext = environment.getContext();
        environment.setContext(OperateDataCacheManager.fetchInstructionSetContext(
            true,
            parentContext.getExpressRunner(),
            parentContext,
            parentContext.getExpressLoader(),
            parentContext.isSupportDynamicFieldName()));
        environment.programPointAddOne();
    }

    @Override
    public String toString() {
        return "openNewArea";
    }
}
