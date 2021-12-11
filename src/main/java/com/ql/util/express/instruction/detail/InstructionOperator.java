package com.ql.util.express.instruction.detail;

import java.util.List;

import com.ql.util.express.ArraySwap;
import com.ql.util.express.InstructionSetContext;
import com.ql.util.express.OperateData;
import com.ql.util.express.RunEnvironment;
import com.ql.util.express.exception.QLBizException;
import com.ql.util.express.exception.QLException;
import com.ql.util.express.instruction.op.OperatorBase;
import com.ql.util.express.instruction.opdata.OperateDataAttr;

public class InstructionOperator extends Instruction {
    private final OperatorBase operator;
    private final int opDataNumber;

    public InstructionOperator(OperatorBase aOperator, int aOpDataNumber) {
        this.operator = aOperator;
        this.opDataNumber = aOpDataNumber;
    }

    public OperatorBase getOperator() {
        return this.operator;
    }

    @Override
    public void execute(RunEnvironment environment, List<String> errorList) throws Exception {
        InstructionSetContext instructionSetContext = environment.getContext();
        ArraySwap parameters = environment.popArray(instructionSetContext, this.opDataNumber);
        if (environment.isTrace() && log.isDebugEnabled()) {
            StringBuilder stringBuilder = new StringBuilder(this.operator.toString() + "(");
            OperateData operateData;
            for (int i = 0; i < parameters.length; i++) {
                operateData = parameters.get(i);
                if (i > 0) {
                    stringBuilder.append(",");
                }
                if (operateData instanceof OperateDataAttr) {
                    stringBuilder.append(operateData).append(":").append(operateData.getObject(instructionSetContext));
                } else {
                    stringBuilder.append(operateData);
                }
            }
            stringBuilder.append(")");
            log.debug(stringBuilder.toString());
        }
        try {
            OperateData result = this.operator.execute(instructionSetContext, parameters, errorList);
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
