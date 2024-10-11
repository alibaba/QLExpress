package com.ql.util.express.instruction.op;

import com.ql.util.express.Operator;

public class OperatorUnaryPlus extends Operator {
    public OperatorUnaryPlus(String name) {
        this.name = name;
    }

    public OperatorUnaryPlus(String aliasName, String name, String errorInfo) {
        this.name = name;
        this.aliasName = aliasName;
        this.errorInfo = errorInfo;
    }

    @Override
    public Object executeInner(Object[] list) throws Exception {
        if (list[0] instanceof Number) {
            return list[0];
        }
        throw new Exception("Cannot apply unary plus to non-number type");
    }
}
