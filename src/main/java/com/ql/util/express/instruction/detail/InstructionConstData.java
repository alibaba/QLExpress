package com.ql.util.express.instruction.detail;

import java.util.List;

import com.ql.util.express.OperateData;
import com.ql.util.express.RunEnvironment;
import com.ql.util.express.instruction.opdata.OperateDataAttr;

public class InstructionConstData extends Instruction {
    private final OperateData operateData;

    public InstructionConstData(OperateData operateData) {
        this.operateData = operateData;
    }

    public OperateData getOperateData() {
        return this.operateData;
    }

    @Override
    public void execute(RunEnvironment environment, List<String> errorList) throws Exception {
        environment.push(this.operateData);
        environment.programPointAddOne();
    }

    @Override
    public String toString() {
        if (this.operateData instanceof OperateDataAttr) {
            return "LoadData attr:" + this.operateData;
        } else {
            return "LoadData " + this.operateData.toString();
        }
    }
}
