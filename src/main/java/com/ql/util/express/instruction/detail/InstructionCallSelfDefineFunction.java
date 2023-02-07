package com.ql.util.express.instruction.detail;

import java.util.List;

import com.ql.util.express.ArraySwap;
import com.ql.util.express.InstructionSet;
import com.ql.util.express.InstructionSetContext;
import com.ql.util.express.InstructionSetRunner;
import com.ql.util.express.OperateData;
import com.ql.util.express.RunEnvironment;
import com.ql.util.express.exception.QLException;
import com.ql.util.express.instruction.OperateDataCacheManager;
import com.ql.util.express.instruction.opdata.OperateDataLocalVar;

public class InstructionCallSelfDefineFunction extends Instruction {
    private final String functionName;
    private final int opDataNumber;

    public InstructionCallSelfDefineFunction(String name, int opDataNumber) {
        this.functionName = name;
        this.opDataNumber = opDataNumber;
    }

    public String getFunctionName() {
        return functionName;
    }

    public int getOpDataNumber() {
        return opDataNumber;
    }

    @Override
    public void execute(RunEnvironment environment, List<String> errorList) throws Exception {
        ArraySwap parameters = environment.popArray(this.opDataNumber);

        Object function = environment.getContext().getSymbol(functionName);
        if (!(function instanceof InstructionSet)) {
            throw new QLException(
                getExceptionPrefix() + "在Runner的操作符定义和自定义函数中都没有找到" + this.functionName + "的定义");
        }
        InstructionSet functionSet = (InstructionSet)function;
        OperateData result = InstructionCallSelfDefineFunction.executeSelfFunction(environment, functionSet, parameters,
            errorList);
        environment.push(result);
        environment.programPointAddOne();
    }

    public static OperateData executeSelfFunction(RunEnvironment environment, InstructionSet functionSet,
        ArraySwap parameters, List<String> errorList) throws Exception {
        InstructionSetContext context = OperateDataCacheManager.fetchInstructionSetContext(
            true, environment.getContext().getExpressRunner(), environment.getContext(),
            environment.getContext().getExpressLoader(), environment.getContext().isSupportDynamicFieldName());
        OperateDataLocalVar[] vars = functionSet.getParameters();
        for (int i = 0; i < vars.length; i++) {
            //注意此处必须new 一个新的对象，否则就会在多次调用的时候导致数据冲突
            OperateDataLocalVar operateDataLocalVar = OperateDataCacheManager.fetchOperateDataLocalVar(
                vars[i].getName(), vars[i].getOriginalType());
            context.addSymbol(operateDataLocalVar.getName(), operateDataLocalVar);
            operateDataLocalVar.setObject(context, parameters.get(i).getObject(environment.getContext()));
        }
        Object result = InstructionSetRunner.execute(functionSet, context, errorList, environment.isTrace(), false,
            true);
        return OperateDataCacheManager.fetchOperateData(result, null);
    }

    @Override
    public String toString() {
        return "call Function[" + this.functionName + "] OPNUMBER[" + this.opDataNumber + "]";
    }
}
