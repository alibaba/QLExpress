package com.ql.util.express.instruction.detail;

import java.util.List;

import com.ql.util.express.InstructionSetContext;
import com.ql.util.express.RunEnvironment;
import com.ql.util.express.instruction.OperateDataCacheManager;

public class InstructionOpenNewArea extends Instruction {
    private static final long serialVersionUID = -118527079334123637L;

    public void execute(RunEnvironment environment, List<String> errorList) throws Exception {
        //目前的模式，不需要执行任何操作
        if (environment.isTrace() && log.isDebugEnabled()) {
            log.debug(this);
        }
        InstructionSetContext parentContext = environment.getContext();
        environment.setContext(OperateDataCacheManager.fetchInstructionSetContext(
            true,
            parentContext.getExpressRunner(),
            parentContext,
            parentContext.getExpressLoader(),
            parentContext.isSupportDynamicFieldName()));
        environment.programPointAddOne();
    }

    public String toString() {
        return "openNewArea";
    }
}
