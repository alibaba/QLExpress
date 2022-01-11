package com.ql.util.express.instruction.op;

import com.ql.util.express.ArraySwap;
import com.ql.util.express.InstructionSetContext;
import com.ql.util.express.OperateData;
import com.ql.util.express.instruction.opdata.OperateDataAlias;
import com.ql.util.express.instruction.opdata.OperateDataAttr;

public class OperatorExportAlias extends OperatorBase {
    public OperatorExportAlias(String name) {
        this.name = name;
    }

    public OperatorExportAlias(String aliasName, String name, String errorInfo) {
        this.name = name;
        this.aliasName = aliasName;
        this.errorInfo = errorInfo;
    }

    @Override
    public OperateData executeInner(InstructionSetContext parent, ArraySwap list) throws Exception {
        String varName = (String)list.get(0).getObjectInner(parent);
        OperateDataAttr realAttr = (OperateDataAttr)list.get(1);
        OperateDataAttr result = new OperateDataAlias(varName, realAttr);
        parent.exportSymbol(varName, result);
        return result;
    }
}
