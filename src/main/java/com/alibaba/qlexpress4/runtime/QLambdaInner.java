package com.alibaba.qlexpress4.runtime;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.exception.UserDefineException;
import com.alibaba.qlexpress4.runtime.data.AssignableDataValue;
import com.alibaba.qlexpress4.runtime.data.convert.ObjTypeConvertor;
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
    
    public QResult call(Object... params)
        throws Throwable {
        QContext newRuntime = newEnv ? inheritScope(params) : qContext;
        
        QLInstruction[] instructions = lambdaDefinition.getInstructions();
        for (int i = 0; i < instructions.length; i++) {
            QResult qResult = instructions[i].execute(newRuntime, qlOptions);
            switch (qResult.getResultType()) {
                case JUMP:
                    i += (int)qResult.getResult().get();
                    continue;
                case RETURN:
                case BREAK:
                case CONTINUE:
                    return qResult;
            }
        }
        
        return QResult.NEXT_INSTRUCTION;
    }
    
    private QContext inheritScope(Object[] params)
        throws UserDefineException {
        Map<String, Value> initSymbolTable = new HashMap<>(params.length);
        List<QLambdaDefinitionInner.Param> paramsDefinition = lambdaDefinition.getParamsType();
        for (int i = 0; i < Math.min(params.length, paramsDefinition.size()); i++) {
            QLambdaDefinitionInner.Param paramDefinition = paramsDefinition.get(i);
            Object originParamI = params[i];
            Class<?> targetCls = paramDefinition.getClazz();
            ObjTypeConvertor.QConverted qlConvertResult = ObjTypeConvertor.cast(originParamI, targetCls);
            if (!qlConvertResult.isConvertible()) {
                throw new UserDefineException(UserDefineException.ExceptionType.INVALID_ARGUMENT,
                    MessageFormat.format(
                        "invalid argument at index {0} (start from 0), required type {1}, but {2} provided",
                        i,
                        targetCls.getName(),
                        originParamI == null ? "null" : originParamI.getClass().getName()));
            }
            initSymbolTable.put(paramDefinition.getName(),
                new AssignableDataValue(paramDefinition.getName(), qlConvertResult.getConverted(), targetCls));
        }
        // null for rest params
        for (int i = params.length; i < paramsDefinition.size(); i++) {
            QLambdaDefinitionInner.Param paramDefinition = paramsDefinition.get(i);
            initSymbolTable.put(paramDefinition.getName(),
                new AssignableDataValue(paramDefinition.getName(), null, paramDefinition.getClazz()));
        }
        QvmBlockScope newScope =
            new QvmBlockScope(qContext, initSymbolTable, lambdaDefinition.getMaxStackSize(), ExceptionTable.EMPTY);
        return new DelegateQContext(qContext, newScope);
    }
}
