package com.ql.util.express.instruction.op;

import com.ql.util.express.Operator;
import com.ql.util.express.OperatorOfNumber;

public class OperatorMultiDiv extends Operator {
    public OperatorMultiDiv(String name) {
        this.name = name;
    }

    @Override
    public Object executeInner(Object[] list) throws Exception {
        return executeInner(list[0], list[1]);
    }

    public Object executeInner(Object op1, Object op2) throws Exception {
        Object obj = null;
        switch (this.getName()) {
            case "*":
                obj = OperatorOfNumber.multiply(op1, op2, this.isPrecise);
                break;
            case "/":
                obj = OperatorOfNumber.divide(op1, op2, this.isPrecise);
                break;
            case "%":
            case "mod":
                obj = OperatorOfNumber.modulo(op1, op2);
                break;
            default:
                break;
        }

        return obj;
    }
}
