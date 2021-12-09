package com.ql.util.express.instruction;

import com.ql.util.express.CallResult;
import com.ql.util.express.ExpressLoader;
import com.ql.util.express.ExpressRunner;
import com.ql.util.express.IExpressContext;
import com.ql.util.express.InstructionSet;
import com.ql.util.express.InstructionSetContext;
import com.ql.util.express.OperateData;
import com.ql.util.express.RunEnvironment;
import com.ql.util.express.instruction.opdata.OperateDataArrayItem;
import com.ql.util.express.instruction.opdata.OperateDataAttr;
import com.ql.util.express.instruction.opdata.OperateDataField;
import com.ql.util.express.instruction.opdata.OperateDataKeyValue;
import com.ql.util.express.instruction.opdata.OperateDataLocalVar;

class OperateDataCacheImpl4Orig implements IOperateDataCache {

    public OperateData fetchOperateData(Object obj, Class<?> aType) {
        return new OperateData(obj, aType);
    }

    public OperateDataAttr fetchOperateDataAttr(String name, Class<?> aType) {
        return new OperateDataAttr(name, aType);
    }

    public OperateDataLocalVar fetchOperateDataLocalVar(String name, Class<?> aType) {
        return new OperateDataLocalVar(name, aType);
    }

    public OperateDataField fetchOperateDataField(Object aFieldObject, String aFieldName) {
        return new OperateDataField(aFieldObject, aFieldName);
    }

    public OperateDataArrayItem fetchOperateDataArrayItem(OperateData aArrayObject, int aIndex) {
        return new OperateDataArrayItem(aArrayObject, aIndex);
    }

    public OperateDataKeyValue fetchOperateDataKeyValue(OperateData aKey, OperateData aValue) {
        return new OperateDataKeyValue(aKey, aValue);
    }

    public RunEnvironment fetRunEnvironment(InstructionSet aInstructionSet, InstructionSetContext aContext,
        boolean aIsTrace) {
        return new RunEnvironment(aInstructionSet, aContext, aIsTrace);
    }

    public CallResult fetchCallResult(Object aReturnValue, boolean aIsExit) {
        return new CallResult(aReturnValue, aIsExit);
    }

    public InstructionSetContext fetchInstructionSetContext(boolean aIsExpandToParent, ExpressRunner aRunner,
        IExpressContext<String, Object> aParent, ExpressLoader aExpressLoader, boolean aIsSupportDynamicFieldName) {
        return new InstructionSetContext(aIsExpandToParent, aRunner, aParent, aExpressLoader,
            aIsSupportDynamicFieldName);
    }

    public void resetCache() {
    }

    public long getFetchCount() {
        return 0;
    }
}
