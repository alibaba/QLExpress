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

public class OperateDataCacheImpl implements IOperateDataCache {
    private final OperateData[] dataList;
    private final OperateDataAttr[] attrList;
    private final OperateDataLocalVar[] localVarList;
    private final OperateDataField[] fieldList;
    private final OperateDataArrayItem[] arrayList;
    private final OperateDataKeyValue[] keyValueList;
    private final RunEnvironment[] environmentList;
    private final CallResult[] callResultList;
    private final InstructionSetContext[] contextList;

    private int dataPoint = 0;
    private int attrPoint = 0;
    private int localVarPoint = 0;
    private int fieldPoint = 0;
    private int arrayPoint = 0;
    private int keyValuePoint = 0;
    private int environmentPoint = 0;
    private int callResultPoint = 0;
    private int contextPoint = 0;
    private final int length;

    public OperateDataCacheImpl(int len) {
        length = len;
        dataList = new OperateData[len];
        attrList = new OperateDataAttr[len];
        localVarList = new OperateDataLocalVar[len];
        fieldList = new OperateDataField[len];
        arrayList = new OperateDataArrayItem[len];
        keyValueList = new OperateDataKeyValue[len];
        callResultList = new CallResult[len];
        environmentList = new RunEnvironment[len];
        contextList = new InstructionSetContext[len];
        for (int i = 0; i < len; i++) {
            dataList[i] = new OperateData(null, null);
            attrList[i] = new OperateDataAttr(null, null);
            localVarList[i] = new OperateDataLocalVar(null, null);
            fieldList[i] = new OperateDataField(null, null);
            arrayList[i] = new OperateDataArrayItem(null, -1);
            keyValueList[i] = new OperateDataKeyValue(null, null);
            callResultList[i] = new CallResult(null, false);
            environmentList[i] = new RunEnvironment(null, null, false);
            contextList[i] = new InstructionSetContext(false, null, null, null, false);
        }
    }

    @Override
    public void resetCache() {
        for (int i = 0; i <= dataPoint && i < length; i++) {
            dataList[i].clear();
        }
        for (int i = 0; i <= attrPoint && i < length; i++) {
            attrList[i].clearDataAttr();
        }
        for (int i = 0; i <= localVarPoint && i < length; i++) {
            localVarList[i].clearDataLocalVar();
        }
        for (int i = 0; i <= fieldPoint && i < length; i++) {
            fieldList[i].clearDataField();
        }
        for (int i = 0; i <= arrayPoint && i < length; i++) {
            arrayList[i].clearDataArrayItem();
        }
        for (int i = 0; i <= keyValuePoint && i < length; i++) {
            keyValueList[i].clearDataKeyValue();
        }
        for (int i = 0; i <= callResultPoint && i < length; i++) {
            callResultList[i].clear();
        }
        for (int i = 0; i <= environmentPoint && i < length; i++) {
            environmentList[i].clear();
        }
        for (int i = 0; i <= contextPoint && i < length; i++) {
            contextList[i].clear();
        }

        dataPoint = 0;
        attrPoint = 0;
        localVarPoint = 0;
        fieldPoint = 0;
        arrayPoint = 0;
        keyValuePoint = 0;
        callResultPoint = 0;
        environmentPoint = 0;
        contextPoint = 0;
    }

    @Override
    public InstructionSetContext fetchInstructionSetContext(boolean aIsExpandToParent, ExpressRunner aRunner,
        IExpressContext<String, Object> aParent, ExpressLoader aExpressLoader, boolean aIsSupportDynamicFieldName) {
        InstructionSetContext result;
        if (contextPoint < length) {
            result = contextList[contextPoint];
            result.initial(aIsExpandToParent, aRunner, aParent, aExpressLoader, aIsSupportDynamicFieldName);
            contextPoint = contextPoint + 1;
        } else {
            result = new InstructionSetContext(aIsExpandToParent, aRunner, aParent, aExpressLoader,
                aIsSupportDynamicFieldName);
        }
        return result;
    }

    @Override
    public RunEnvironment fetRunEnvironment(InstructionSet aInstructionSet, InstructionSetContext aContext,
        boolean aIsTrace) {
        RunEnvironment result;
        if (environmentPoint < length) {
            result = environmentList[environmentPoint];
            result.initial(aInstructionSet, aContext, aIsTrace);
            environmentPoint = environmentPoint + 1;
        } else {
            result = new RunEnvironment(aInstructionSet, aContext, aIsTrace);
        }
        return result;
    }

    @Override
    public CallResult fetchCallResult(Object aReturnValue, boolean aIsExit) {
        CallResult result;
        if (callResultPoint < length) {
            result = callResultList[callResultPoint];
            result.initial(aReturnValue, aIsExit);
            callResultPoint = callResultPoint + 1;
        } else {
            result = new CallResult(aReturnValue, aIsExit);
        }
        return result;
    }

    @Override
    public OperateData fetchOperateData(Object obj, Class<?> aType) {
        OperateData result;
        if (dataPoint < length) {
            result = dataList[dataPoint];
            result.initial(obj, aType);
            dataPoint = dataPoint + 1;
        } else {
            result = new OperateData(obj, aType);
        }
        return result;
    }

    @Override
    public OperateDataAttr fetchOperateDataAttr(String name, Class<?> aType) {
        OperateDataAttr result;
        if (attrPoint < length) {
            result = attrList[attrPoint];
            result.initialDataAttr(name, aType);
            attrPoint = attrPoint + 1;
        } else {
            result = new OperateDataAttr(name, aType);
        }
        return result;
    }

    @Override
    public OperateDataLocalVar fetchOperateDataLocalVar(String name, Class<?> aType) {
        OperateDataLocalVar result;
        if (localVarPoint < length) {
            result = localVarList[localVarPoint];
            result.initialDataLocalVar(name, aType);
            localVarPoint = localVarPoint + 1;
        } else {
            result = new OperateDataLocalVar(name, aType);
        }
        return result;
    }

    @Override
    public OperateDataField fetchOperateDataField(Object aFieldObject, String aFieldName) {
        OperateDataField result;
        if (fieldPoint < length) {
            result = fieldList[fieldPoint];
            result.initialDataField(aFieldObject, aFieldName);
            fieldPoint = fieldPoint + 1;
        } else {
            result = new OperateDataField(aFieldObject, aFieldName);
        }
        return result;
    }

    @Override
    public OperateDataArrayItem fetchOperateDataArrayItem(OperateData aArrayObject, int aIndex) {
        OperateDataArrayItem result;
        if (arrayPoint < length) {
            result = arrayList[arrayPoint];
            result.initialDataArrayItem(aArrayObject, aIndex);
            arrayPoint = arrayPoint + 1;
        } else {
            result = new OperateDataArrayItem(aArrayObject, aIndex);
        }
        return result;
    }

    @Override
    public OperateDataKeyValue fetchOperateDataKeyValue(OperateData aKey, OperateData aValue) {
        OperateDataKeyValue result;
        if (this.keyValuePoint < length) {
            result = this.keyValueList[keyValuePoint];
            result.initialDataKeyValue(aKey, aValue);
            keyValuePoint = keyValuePoint + 1;
        } else {
            result = new OperateDataKeyValue(aKey, aValue);
        }
        return result;
    }

    @Override
    public long getFetchCount() {
        return 0;
    }
}
