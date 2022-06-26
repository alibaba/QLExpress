package com.alibaba.qlexpress4.runtime;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.runtime.data.AssignableDataValue;
import com.alibaba.qlexpress4.runtime.instruction.QLInstruction;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author: DQinYuan
 */
public class QLambdaInner implements QLambda {

    private final QLambdaDefinition lambdaDefinition;

    private final QRuntime qRuntime;

    private final QLOptions qlOptions;

    private final boolean newEnv;

    public QLambdaInner(QLambdaDefinition lambdaDefinition, QRuntime qRuntime, QLOptions qlOptions,
                        boolean newEnv) {
        this.lambdaDefinition = lambdaDefinition;
        this.qRuntime = qRuntime;
        this.qlOptions = qlOptions;
        this.newEnv = newEnv;
    }

    public QResult call(Object... params) throws Exception {
        QRuntime newRuntime = newEnv? inheritRuntime(params): qRuntime;

        List<QLInstruction> instructionList = lambdaDefinition.getInstructionList();
        for (QLInstruction qlInstruction : instructionList) {
            QResult qResult = qlInstruction.execute(newRuntime, qlOptions);
            switch (qResult.getResultType()) {
                case RETURN:
                case CASCADE_RETURN:
                    return qResult;
            }
        }

        return QResult.CONTINUE_RESULT;
    }

    private QRuntime inheritRuntime(Object[] params) {
        Map<String, Value> initSymbolTable = new HashMap<>();
        List<QLambdaDefinition.Param> paramsDefinition = lambdaDefinition.getParamsType();
        for (int i = 0; i < params.length; i++) {
            QLambdaDefinition.Param paramDefinition = paramsDefinition.get(i);
            initSymbolTable.put(paramDefinition.getName(),
                    new AssignableDataValue(params[i], paramDefinition.getClazz()));
        }
        return new QvmRuntime(qRuntime, initSymbolTable, lambdaDefinition.getMaxStackSize(),
                qRuntime.scriptStartTimeStamp());
    }
}
