package com.ql.util.express.instruction.op;

import com.ql.util.express.Operator;

public class OperatorNor extends Operator {
    public OperatorNor(String name) {
        this.name = name;
    }

    public OperatorNor(String aliasName, String name, String errorInfo) {
        this.name = name;
        this.aliasName = aliasName;
        this.errorInfo = errorInfo;
    }

    @Override
    public Object executeInner(Object[] list) throws Exception {
        return executeInner(list[0], list[1]);
    }

    public Object executeInner(Object op1, Object op2) {
        if (op1 != null) {
            return op1;
        } else {
            return op2;
        }
    }
}
