package com.ql.util.express.example.operator;

import com.ql.util.express.Operator;

/**
 * 定义连加的操作符
 */
public class AddNOperator extends Operator {
    @Override
    public Object executeInner(Object[] list) {
        int r = 0;
        for (Object item : list) {
            r = r + (Integer)item;
        }
        return r;
    }
}
