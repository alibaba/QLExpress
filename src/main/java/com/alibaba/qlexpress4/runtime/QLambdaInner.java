package com.alibaba.qlexpress4.runtime;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.exception.UserDefineException;
import com.alibaba.qlexpress4.runtime.data.AssignableDataValue;
import com.alibaba.qlexpress4.runtime.data.convert.InstanceConversion;
import com.alibaba.qlexpress4.runtime.data.implicit.QLConvertResult;
import com.alibaba.qlexpress4.runtime.data.implicit.QLConvertResultType;
import com.alibaba.qlexpress4.runtime.instruction.QLInstruction;
import com.alibaba.qlexpress4.runtime.scope.QvmBlockScope;

import java.text.MessageFormat;
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

    private QContext inheritScope(Object[] params) throws UserDefineException {
        Map<String, Value> initSymbolTable = new HashMap<>(params.length);
        List<QLambdaDefinitionInner.Param> paramsDefinition = lambdaDefinition.getParamsType();
        for (int i = 0; i < params.length; i++) {
            QLambdaDefinitionInner.Param paramDefinition = paramsDefinition.get(i);
            Object originParamI = params[i];
            Class<?> targetCls = paramDefinition.getClazz();
            QLConvertResult qlConvertResult = InstanceConversion.castObject(originParamI, targetCls);
            if (QLConvertResultType.NOT_TRANS == qlConvertResult.getResultType()) {
                throw new UserDefineException(UserDefineException.INVALID_PARAM,
                        MessageFormat.format(
                                "invalid argument at index {0} (start from 0), required type {1}, but {2} provided",
                                i, targetCls.getName(),
                                originParamI == null? "null": originParamI.getClass().getName())
                );
            }
            initSymbolTable.put(paramDefinition.getName(), new AssignableDataValue(originParamI, targetCls));
        }
        for (int i = params.length; i < paramsDefinition.size(); i++) {
            QLambdaDefinitionInner.Param paramDefinition = paramsDefinition.get(i);
            initSymbolTable.put(paramDefinition.getName(),
                    new AssignableDataValue(null, paramDefinition.getClazz()));
        }
        QvmBlockScope newScope = new QvmBlockScope(qContext, initSymbolTable, lambdaDefinition.getMaxStackSize());
        return new DelegateQContext(qContext, newScope);
    }
}
