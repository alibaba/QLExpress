package com.ql.util.express.instruction.op;

import com.ql.util.express.ArraySwap;
import com.ql.util.express.InstructionSetContext;
import com.ql.util.express.OperateData;
import com.ql.util.express.instruction.opdata.OperateDataAttr;

public class OperatorExportDef extends OperatorBase {
    public OperatorExportDef(String aName) {
        this.name = aName;
    }

    public OperatorExportDef(String aAliasName, String aName, String aErrorInfo) {
        this.name = aName;
        this.aliasName = aAliasName;
        this.errorInfo = aErrorInfo;
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
