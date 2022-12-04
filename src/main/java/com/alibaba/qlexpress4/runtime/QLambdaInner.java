package com.alibaba.qlexpress4.runtime;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.runtime.data.AssignableDataValue;
import com.alibaba.qlexpress4.runtime.instruction.QLInstruction;
import com.alibaba.qlexpress4.runtime.scope.QvmBlockScope;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author: DQinYuan
 */
public class QLambdaInner implements QLambda {

    private final QLambdaDefinitionInner lambdaDefinition;

    private final QContext qContext;

    private final QLOptions qlOptions;

    private final boolean newEnv;

    public QLambdaInner(QLambdaDefinitionInner lambdaDefinition, QContext qContext, QLOptions qlOptions,
                        boolean newEnv) {
        this.lambdaDefinition = lambdaDefinition;
        this.qContext = qContext;
        this.qlOptions = qlOptions;
        this.newEnv = newEnv;
    }

    public QResult call(Object... params) throws Exception {
        QContext newRuntime = newEnv? inheritScope(params): qContext;

        List<QLInstruction> instructionList = lambdaDefinition.getInstructionList();
        for (QLInstruction qlInstruction : instructionList) {
            QResult qResult = qlInstruction.execute(newRuntime, qlOptions);
            switch (qResult.getResultType()) {
                case RETURN:
                case CASCADE_RETURN:
                case BREAK:
                case CONTINUE:
                    return qResult;
            }
        }

        return QResult.NEXT_INSTRUCTION;
    }

    private QContext inheritScope(Object[] params) {
        Map<String, Value> initSymbolTable = new HashMap<>(params.length);
        List<QLambdaDefinitionInner.Param> paramsDefinition = lambdaDefinition.getParamsType();
        for (int i = 0; i < params.length; i++) {
            QLambdaDefinitionInner.Param paramDefinition = paramsDefinition.get(i);
            initSymbolTable.put(paramDefinition.getName(),
                    new AssignableDataValue(params[i], paramDefinition.getClazz()));
        }
        QvmBlockScope newScope = new QvmBlockScope(qContext, initSymbolTable, lambdaDefinition.getMaxStackSize());
        return new DelegateQContext(qContext, newScope);
    }
}
