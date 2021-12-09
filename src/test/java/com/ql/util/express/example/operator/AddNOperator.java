package com.ql.util.express.example.operator;

import com.ql.util.express.Operator;

/**
 * 定义连加的操作符
 */
public class AddNOperator extends Operator {

    public Object executeInner(Object[] list) throws Exception {
        int r = 0;
        for (int i = 0; i < list.length; i++) {
            r = r + (Integer)list[i];
        }
        return r;
    }
}
