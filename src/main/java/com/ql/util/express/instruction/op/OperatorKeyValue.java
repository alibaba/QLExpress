package com.ql.util.express.instruction.op;

import com.ql.util.express.ArraySwap;
import com.ql.util.express.InstructionSetContext;
import com.ql.util.express.OperateData;
import com.ql.util.express.instruction.OperateDataCacheManager;

public class OperatorKeyValue extends OperatorBase {
    public OperatorKeyValue(String name) {
        this.name = name;
    }

    public OperatorKeyValue(String aliasName, String name, String errorInfo) {
        this.name = name;
        this.aliasName = aliasName;
        this.errorInfo = errorInfo;
    }

    @Override
    public OperateData executeInner(InstructionSetContext parent, ArraySwap list) {
        return OperateDataCacheManager.fetchOperateDataKeyValue(list.get(0), list.get(1));
    }
}
