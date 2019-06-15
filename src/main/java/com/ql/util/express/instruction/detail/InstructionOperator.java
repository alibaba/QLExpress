package com.ql.util.express.instruction.detail;

import com.ql.util.express.ArraySwap;
import com.ql.util.express.OperateData;
import com.ql.util.express.RunEnvironment;
import com.ql.util.express.instruction.op.OperatorBase;
import com.ql.util.express.instruction.opdata.OperateDataAttr;

import java.util.List;

public class InstructionOperator extends Instruction {
    private static final long serialVersionUID = -1217916524030161947L;
    OperatorBase operator;
    int opDataNumber;

    public InstructionOperator(OperatorBase aOperator, int aOpDataNumber) {
        this.operator = aOperator;
        this.opDataNumber = aOpDataNumber;
    }

    public OperatorBase getOperator() {
        return this.operator;
    }

    public void execute(RunEnvironment environment, List<String> errorList) throws Exception {
        ArraySwap parameters = environment.popArray(environment.getContext(), this.opDataNumber);
        if (environment.isTrace() && this.log.isDebugEnabled()) {
            String str = this.operator.toString() + "(";
            OperateData p = null;
            for (int i = 0; i < parameters.length; i++) {
                p = parameters.get(i);
                if (i > 0) {
                    str = str + ",";
                }
                if (p instanceof OperateDataAttr) {
                    str = str + p + ":" + p.getObject(environment.getContext());
                } else {
                    str = str + p;
                }
            }
            str = str + ")";
            this.log.debug(str);
        }
        try {
            OperateData result = this.operator.execute(environment.getContext(), parameters, errorList);
            environment.push(result);
            environment.programPointAddOne();
        } catch (Exception e) {
            throw getException(e);
        }
    }

    public String toString() {
        String result = "OP : " + this.operator.toString() + " OPNUMBER[" + this.opDataNumber + "]";
        return result;
    }
}
