package com.ql.util.express.instruction.op;

import com.ql.util.express.ArraySwap;
import com.ql.util.express.InstructionSetContext;
import com.ql.util.express.OperateData;
import com.ql.util.express.instruction.opdata.OperateDataAlias;
import com.ql.util.express.instruction.opdata.OperateDataAttr;

public class OperatorAlias extends OperatorBase {
    public OperatorAlias(String name) {
        this.name = name;
    }

    public OperatorAlias(String aliasName, String name, String errorInfo) {
        this.name = name;
        this.aliasName = aliasName;
        this.errorInfo = errorInfo;
    }

    @Override
    public OperateData executeInner(InstructionSetContext parent, ArraySwap list) throws Exception {
        String varName = (String)list.get(0).getObjectInner(parent);
        OperateDataAttr realAttr = (OperateDataAttr)list.get(1);
        OperateDataAttr result = new OperateDataAlias(varName, realAttr);
        parent.addSymbol(varName, result);
        return result;
    }
}
