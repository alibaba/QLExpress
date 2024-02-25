package com.ql.util.express.instruction;

import com.ql.util.express.*;
import com.ql.util.express.instruction.opdata.OperateDataArrayItem;
import com.ql.util.express.instruction.opdata.OperateDataAttr;
import com.ql.util.express.instruction.opdata.OperateDataField;
import com.ql.util.express.instruction.opdata.OperateDataKeyValue;
import com.ql.util.express.instruction.opdata.OperateDataLocalVar;

public interface IOperateDataCache {
    OperateData fetchOperateData(Object obj, Class<?> type);

    OperateDataAttr fetchOperateDataAttr(String name, Class<?> type);

    OperateDataLocalVar fetchOperateDataLocalVar(String name, Class<?> type);

    OperateDataField fetchOperateDataField(Object fieldObject, String fieldName);

    OperateDataArrayItem fetchOperateDataArrayItem(OperateData operateData, int index);

    OperateDataKeyValue fetchOperateDataKeyValue(OperateData key, OperateData value);

    RunEnvironment fetRunEnvironment(InstructionSet instructionSet, InstructionSetContext instructionSetContext,
        boolean isTrace, ExecuteTimeOut executeTimeOut);

    CallResult fetchCallResult(Object returnValue, boolean isExit);

    InstructionSetContext fetchInstructionSetContext(boolean isExpandToParent, ExpressRunner expressRunner,
        IExpressContext<String, Object> parent, ExpressLoader expressLoader, boolean isSupportDynamicFieldName);

    void resetCache();

    long getFetchCount();
}
