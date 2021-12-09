package com.ql.util.express.example.operator;

import com.ql.util.express.Operator;

public class ApproveOperator extends Operator {
    int operater;

    public ApproveOperator(int op) {
        this.operater = op;
    }

    public Object executeInner(Object[] list) throws Exception {
        if (this.operater == 1) {
            System.out.println(list[0] + "审批:金额:" + list[1]);
            if (((Integer)list[1]) > 6000) {return false;}
        } else if (this.operater == 2) {System.out.println("报销入卡:金额:" + list[0]);} else {
            System.out.println("重填:申请人:" + list[0]);
        }
        return true;
    }

}
