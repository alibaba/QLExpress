package com.ql.util.express.instruction.op;

public class OperatorNotLike extends OperatorLike {
    public OperatorNotLike(String name) {
        super(name);
    }

    @Override
    public Object executeInner(Object[] list) throws Exception {
        Object res = super.executeInner(list);
        return !(Boolean) res;
    }
}
