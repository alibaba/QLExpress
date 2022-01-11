package com.ql.util.express.instruction.op;

import com.ql.util.express.ArraySwap;
import com.ql.util.express.InstructionSetContext;
import com.ql.util.express.OperateData;
import com.ql.util.express.instruction.opdata.OperateDataAttr;

public class OperatorExportDef extends OperatorBase {
    public OperatorExportDef(String name) {
        this.name = name;
    }

    public OperatorExportDef(String aliasName, String name, String errorInfo) {
        this.name = name;
        this.aliasName = aliasName;
        this.errorInfo = errorInfo;
    }

    @Override
    public OperateData executeInner(InstructionSetContext parent, ArraySwap list) throws Exception {
        Class<?> tmpClass = (Class<?>)list.get(0).getObject(parent);
        String varName = (String)list.get(1).getObject(parent);
        //OperateDataLocalVar result = new OperateDataLocalVar(varName,tmpClass);
        //context.exportSymbol(varName, result);
        OperateDataAttr result = (OperateDataAttr)parent.getSymbol(varName);
        result.setDefineType(tmpClass);
        return result;
    }
}
