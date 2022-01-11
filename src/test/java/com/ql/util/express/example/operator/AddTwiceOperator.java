package com.ql.util.express.example.operator;

import com.ql.util.express.Operator;

public class AddTwiceOperator extends Operator {
    @Override
    public Object executeInner(Object[] list) {
        int a = (Integer)list[0];
        int b = (Integer)list[1];
        return a + b + b;
    }
}
