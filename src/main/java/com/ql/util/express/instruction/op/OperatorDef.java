package com.ql.util.express.instruction.op;

import com.ql.util.express.ArraySwap;
import com.ql.util.express.InstructionSetContext;
import com.ql.util.express.OperateData;
import com.ql.util.express.instruction.OperateDataCacheManager;
import com.ql.util.express.instruction.opdata.OperateDataLocalVar;
import com.ql.util.express.instruction.opdata.OperateDataVirClass;

public class OperatorDef extends OperatorBase {
    public OperatorDef(String name) {
        this.name = name;
    }

    public OperatorDef(String aliasName, String name, String errorInfo) {
        this.name = name;
        this.aliasName = aliasName;
        this.errorInfo = errorInfo;
    }

    @Override
    public OperateData executeInner(InstructionSetContext parent, ArraySwap list) throws Exception {
        Object type = list.get(0).getObject(parent);
        String varName = (String)list.get(1).getObject(parent);
        Class<?> tmpClass;
        if (type instanceof Class) {
            tmpClass = (Class<?>)type;
        } else {
            tmpClass = OperateDataVirClass.class;
        }
        OperateDataLocalVar result = OperateDataCacheManager.fetchOperateDataLocalVar(varName, tmpClass);
        parent.addSymbol(varName, result);
        return result;
    }
}
