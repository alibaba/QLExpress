package com.ql.util.express.instruction.op;

import com.ql.util.express.Operator;
import com.ql.util.express.exception.QLException;

public class OperatorPrintln extends Operator {
    public OperatorPrintln(String name) {
        this.name = name;
    }

    public OperatorPrintln(String aliasName, String name, String errorInfo) {
        this.name = name;
        this.aliasName = aliasName;
        this.errorInfo = errorInfo;
    }

    @Override
    public Object executeInner(Object[] list) throws Exception {
        if (list.length != 1) {
            throw new QLException("操作数异常,有且只能有一个操作数");
        }
        System.out.println(list[0]);
        return null;
    }
}
