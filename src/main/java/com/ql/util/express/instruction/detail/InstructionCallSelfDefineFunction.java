package com.ql.util.express.instruction.detail;

import com.ql.util.express.*;
import com.ql.util.express.instruction.OperateDataCacheManager;
import com.ql.util.express.instruction.opdata.OperateDataAttr;
import com.ql.util.express.instruction.opdata.OperateDataLocalVar;
import org.apache.commons.logging.Log;

import java.util.List;

public class InstructionCallSelfDefineFunction extends Instruction {
    private static final long serialVersionUID = 8315682251443515151L;
    String functionName;
    int opDataNumber;

    public InstructionCallSelfDefineFunction(String name, int aOpDataNumber) {
        this.functionName = name;
        this.opDataNumber = aOpDataNumber;
    }

    public String getFunctionName() {
        return functionName;
    }

    public int getOpDataNumber() {
        return opDataNumber;
    }

    public void execute(RunEnvironment environment, List<String> errorList)
            throws Exception {
        ArraySwap parameters = environment.popArray(
                environment.getContext(), this.opDataNumber);
        if (environment.isTrace() && log.isDebugEnabled()) {
            String str = this.functionName + "(";
            OperateData p;
            for (int i = 0; i < parameters.length; i++) {
                p = parameters.get(i);
                if (i > 0) {
                    str = str + ",";
                }
                if (p instanceof OperateDataAttr) {
                    str = str
                            + p
                            + ":"
                            + p.getObject(environment
                            .getContext());
                } else {
                    str = str + p;
                }
            }
            str = str + ")";
            log.debug(str);
        }

        Object function = environment.getContext().getSymbol(functionName);
        if (function == null || function instanceof InstructionSet == false) {
            throw getException(getExceptionPrefix() + "在Runner的操作符定义和自定义函数中都没有找到\""
                    + this.functionName + "\"的定义");
        }
        InstructionSet functionSet = (InstructionSet) function;
        OperateData result = InstructionCallSelfDefineFunction
                .executeSelfFunction(environment, functionSet, parameters,
                        errorList, this.log);
        environment.push(result);
        environment.programPointAddOne();
    }

    public static OperateData executeSelfFunction(RunEnvironment environment, InstructionSet functionSet,
                                                  ArraySwap parameters, List<String> errorList, Log log) throws Exception {
        InstructionSetContext context = OperateDataCacheManager.fetchInstructionSetContext(
                true, environment.getContext().getExpressRunner(), environment.getContext(), environment.getContext().getExpressLoader(), environment.getContext().isSupportDynamicFieldName());
        OperateDataLocalVar[] vars = functionSet.getParameters();
        for (int i = 0; i < vars.length; i++) {
            //注意此处必须new 一个新的对象，否则就会在多次调用的时候导致数据冲突
            OperateDataLocalVar var = OperateDataCacheManager.fetchOperateDataLocalVar(vars[i].getName(), vars[i].getOrgiType());
            context.addSymbol(var.getName(), var);
            var.setObject(context, parameters.get(i).getObject(environment.getContext()));
        }
        Object result = InstructionSetRunner.execute((InstructionSet) functionSet,
                context, errorList, environment.isTrace(), false, true, log);
        return OperateDataCacheManager.fetchOperateData(result, null);
    }

    public String toString() {
        return "call Function[" + this.functionName + "] OPNUMBER[" + this.opDataNumber + "]";
    }

}
