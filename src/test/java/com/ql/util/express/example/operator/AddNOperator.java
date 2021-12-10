package com.ql.util.express.example.operator;

import com.ql.util.express.Operator;

/**
 * 定义连加的操作符
 */
public class AddNOperator extends Operator {
    @Override
    public Object executeInner(Object[] list) {
        int r = 0;
        for (int i = 0; i < list.length; i++) {
            r = r + (Integer)list[i];
        }
        return r;
    }
}
