package com.ql.util.express.instruction.op;

public class OperatorNotIn extends OperatorIn {
    public OperatorNotIn(String name) {
        super(name);
    }

    public OperatorNotIn(String aliasName, String name, String errorInfo) {
        super(aliasName, name, errorInfo);
    }

    @Override
    public Object executeInner(Object[] list) throws Exception {
        Object res = super.executeInner(list);
        return !(Boolean) res;
    }
}
