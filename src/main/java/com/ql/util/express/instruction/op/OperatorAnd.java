package com.ql.util.express.instruction.op;

import com.ql.util.express.Operator;
import com.ql.util.express.exception.QLException;

public class OperatorAnd extends Operator {
    public OperatorAnd(String name) {
        this.name = name;
    }

    public OperatorAnd(String aliasName, String name, String errorInfo) {
        this.name = name;
        this.aliasName = aliasName;
        this.errorInfo = errorInfo;
    }

    @Override
    public Object executeInner(Object[] list) throws Exception {
        return executeInner(list[0], list[1]);
    }

    public Object executeInner(Object operand1, Object operand2) throws Exception {
        boolean r1;
        if (operand1 == null) {
            r1 = false;
        } else if (operand1 instanceof Boolean) {
            r1 = (Boolean)operand1;
        } else {
            String msg = "没有定义类型" + operand1 + "和" + operand2 + " 的 " + this.name + "操作";
            throw new QLException(msg);
        }

        boolean r2;
        if (operand2 == null) {
            r2 = false;
        } else if (operand2 instanceof Boolean) {
            r2 = (Boolean)operand2;
        } else {
            String msg = "没有定义类型" + operand1 + "和" + operand2 + " 的 " + this.name + "操作";
            throw new QLException(msg);
        }
        return r1 && r2;
    }
}
