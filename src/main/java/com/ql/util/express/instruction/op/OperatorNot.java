package com.ql.util.express.instruction.op;

import com.ql.util.express.Operator;
import com.ql.util.express.exception.QLException;

public class OperatorNot extends Operator {
    public OperatorNot(String name) {
        this.name = name;
    }

    public OperatorNot(String aliasName, String name, String errorInfo) {
        this.name = name;
        this.aliasName = aliasName;
        this.errorInfo = errorInfo;
    }

    @Override
    public Object executeInner(Object[] list) throws Exception {
        return executeInner(list[0]);
    }

    public Object executeInner(Object op) throws Exception {
        Object result;
        if (op == null) {
            throw new QLException("null 不能执行操作：" + this.getAliasName());
        }
        if (Boolean.class.equals(op.getClass())) {
            result = !(Boolean)op;
        } else {
            String msg = "没有定义类型" + op.getClass().getName() + " 的 " + this.name + "操作";
            throw new QLException(msg);
        }
        return result;
    }
}
