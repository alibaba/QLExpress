package com.ql.util.express.instruction.detail;

import java.util.List;

import com.ql.util.express.ArraySwap;
import com.ql.util.express.OperateData;
import com.ql.util.express.RunEnvironment;
import com.ql.util.express.exception.QLBizException;
import com.ql.util.express.exception.QLException;
import com.ql.util.express.instruction.op.OperatorBase;
import com.ql.util.express.instruction.opdata.OperateDataAttr;

public class InstructionOperator extends Instruction {

    final OperatorBase operator;
    final int opDataNumber;

    public InstructionOperator(OperatorBase aOperator, int aOpDataNumber) {
        this.operator = aOperator;
        this.opDataNumber = aOpDataNumber;
    }

    public OperatorBase getOperator() {
        return this.operator;
    }

    @Override
    public void execute(RunEnvironment environment, List<String> errorList) throws Exception {
        ArraySwap parameters = environment.popArray(environment.getContext(), this.opDataNumber);
        if (environment.isTrace() && log.isDebugEnabled()) {
            StringBuilder str = new StringBuilder(this.operator.toString() + "(");
            OperateData p;
            for (int i = 0; i < parameters.length; i++) {
                p = parameters.get(i);
                if (i > 0) {
                    str.append(",");
                }
                if (p instanceof OperateDataAttr) {
                    str.append(p).append(":").append(p.getObject(environment.getContext()));
                } else {
                    str.append(p);
                }
            }
            str.append(")");
            log.debug(str.toString());
        }
        try {
            OperateData result = this.operator.execute(environment.getContext(), parameters, errorList);
            environment.push(result);
            environment.programPointAddOne();
        } catch (QLException e) {
            throw new QLException(getExceptionPrefix(), e);
        } catch (Throwable t) {
            throw new QLBizException(getExceptionPrefix(), t);
        }
    }

    @Override
    public String toString() {
        return "OP : " + this.operator.toString() + " OPNUMBER[" + this.opDataNumber + "]";
    }
}
