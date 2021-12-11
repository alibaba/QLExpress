package com.ql.util.express.instruction.op;

import com.ql.util.express.Operator;
import com.ql.util.express.OperatorOfNumber;

public class OperatorMultiplyDivide extends Operator {
    public OperatorMultiplyDivide(String name) {
        this.name = name;
    }

    @Override
    public Object executeInner(Object[] list) throws Exception {
        return executeInner(list[0], list[1]);
    }

    public Object executeInner(Object op1, Object op2) throws Exception {
        Object result = null;
        switch (this.getName()) {
            case "*":
                result = OperatorOfNumber.multiply(op1, op2, this.isPrecise);
                break;
            case "/":
                result = OperatorOfNumber.divide(op1, op2, this.isPrecise);
                break;
            case "%":
            case "mod":
                result = OperatorOfNumber.modulo(op1, op2);
                break;
            default:
                break;
        }
        return result;
    }
}
