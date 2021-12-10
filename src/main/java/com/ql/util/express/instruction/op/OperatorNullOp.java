package com.ql.util.express.instruction.op;

import com.ql.util.express.ArraySwap;
import com.ql.util.express.IExpressContext;
import com.ql.util.express.InstructionSetContext;
import com.ql.util.express.OperateData;

/**
 * 处理 ",","(",")",";"
 */
public class OperatorNullOp extends OperatorBase {
    public OperatorNullOp(String name) {
        this.name = name;
    }

    public OperatorNullOp(String aAliasName, String aName, String aErrorInfo) {
        this.name = aName;
        this.aliasName = aAliasName;
        this.errorInfo = aErrorInfo;
    }

    @Override
    public OperateData executeInner(InstructionSetContext parent, ArraySwap list) throws Exception {
        return executeInner(parent);
    }

    public OperateData executeInner(IExpressContext<String, Object> parent) {
        return null;
    }
}
