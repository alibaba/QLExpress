package com.ql.util.express.instruction.detail;

import com.ql.util.express.InstructionSet;
import com.ql.util.express.QLambda;
import com.ql.util.express.RunEnvironment;
import com.ql.util.express.instruction.OperateDataCacheManager;

import java.util.List;

/**
 * 将一个 QLambda 加载到栈上
 */
public class InstructionLoadLambda extends Instruction {
    private final InstructionSet lambdaSet;

    public InstructionLoadLambda(InstructionSet lambdaSet) {
        this.lambdaSet = lambdaSet;
    }

    @Override
    public void execute(RunEnvironment environment, List<String> errorList) throws Exception {
        environment.push(
                OperateDataCacheManager.fetchOperateData(new QLambda(lambdaSet, environment, errorList, log), null)
        );
        environment.programPointAddOne();
    }

    @Override
    public String toString() {
        return "Load Lambda " + lambdaSet.toString() + "Lambda End";
    }
}
