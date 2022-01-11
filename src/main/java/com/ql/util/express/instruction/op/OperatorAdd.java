package com.ql.util.express.instruction.op;

import com.ql.util.express.Operator;
import com.ql.util.express.OperatorOfNumber;

public class OperatorAdd extends Operator {
    public OperatorAdd(String name) {
        this.name = name;
    }

    public OperatorAdd(String aliasName, String name, String errorInfo) {
        this.name = name;
        this.aliasName = aliasName;
        this.errorInfo = errorInfo;
    }

    @Override
    public Object executeInner(Object[] list) throws Exception {
        return OperatorOfNumber.add(list[0], list[1], this.isPrecise);
    }
}
