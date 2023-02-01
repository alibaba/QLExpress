package com.ql.util.express.instruction.detail;

import java.util.List;

import com.ql.util.express.InstructionSet;
import com.ql.util.express.InstructionSetContext;
import com.ql.util.express.InstructionSetRunner;
import com.ql.util.express.OperateData;
import com.ql.util.express.RunEnvironment;
import com.ql.util.express.instruction.OperateDataCacheManager;

public class InstructionCallMacro extends Instruction {
    private final String name;

    public InstructionCallMacro(String name) {
        this.name = name;
    }

    @Override
    public void execute(RunEnvironment environment, List<String> errorList) throws Exception {
        InstructionSetContext context = environment.getContext();
        Object functionSet = context.getSymbol(this.name);

        Object result = InstructionSetRunner.execute(context.getExpressRunner(), (InstructionSet)functionSet,
            context.getExpressLoader(), context, errorList, environment.isTrace(), false, false,
            environment.getContext().isSupportDynamicFieldName());
        if (result instanceof OperateData) {
            environment.push((OperateData)result);
        } else {
            environment.push(OperateDataCacheManager.fetchOperateData(result, null));
        }

        environment.programPointAddOne();
    }

    @Override
    public String toString() {
        return "call macro " + this.name;
    }
}
