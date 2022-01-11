package com.ql.util.express.test;

import com.ql.util.express.Operator;

class LoveOperator extends Operator {
    public LoveOperator(String name) {
        this.name = name;
    }

    @Override
    public Object executeInner(Object[] list) {
        String op1 = list[0].toString();
        String op2 = list[1].toString();
        return op2 + "{" + op1 + "}" + op2;
    }
}
