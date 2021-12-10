package com.ql.util.express.instruction.op;

import com.ql.util.express.ArraySwap;
import com.ql.util.express.InstructionSetContext;
import com.ql.util.express.OperateData;
import com.ql.util.express.instruction.opdata.OperateDataAlias;
import com.ql.util.express.instruction.opdata.OperateDataAttr;

public class OperatorMacro extends OperatorBase {
    public OperatorMacro(String aName) {
        this.name = aName;
    }

    public OperatorMacro(String aAliasName, String aName, String aErrorInfo) {
        this.name = aName;
        this.aliasName = aAliasName;
        this.errorInfo = aErrorInfo;
    }

    @Override
    public OperateData executeInner(InstructionSetContext context, ArraySwap list) throws Exception {
        String varName = (String)list.get(0).getObjectInner(context);
        OperateDataAttr realAttr = (OperateDataAttr)list.get(1);
        OperateDataAttr result = new OperateDataAlias(varName, realAttr);
        context.addSymbol(varName, result);
        return result;
    }
}
