package com.ql.util.express.instruction.detail;

import java.util.List;

import com.ql.util.express.ArraySwap;
import com.ql.util.express.OperateData;
import com.ql.util.express.RunEnvironment;
import com.ql.util.express.instruction.opdata.OperateDataAttr;
import com.ql.util.express.instruction.opdata.OperateDataVirClass;

public class InstructionNewVirClass extends Instruction {
    private final String className;
    private final int opDataNumber;

    public InstructionNewVirClass(String name, int opDataNumber) {
        this.className = name;
        this.opDataNumber = opDataNumber;
    }

    public String getClassName() {
        return className;
    }

    @Override
    public void execute(RunEnvironment environment, List<String> errorList) throws Exception {
        ArraySwap parameters = environment.popArray(this.opDataNumber);

        //因为会影响堆栈，要先把对象拷贝出来
        OperateData[] list = new OperateData[parameters.length];
        for (int i = 0; i < list.length; i++) {
            list[i] = parameters.get(i);
        }

        OperateDataVirClass result = new OperateDataVirClass(className);
        environment.push(result);
        environment.programPointAddOne();
        result.initialInstance(environment.getContext(), list, errorList, environment.isTrace());
    }

    @Override
    public String toString() {
        return "new VClass[" + this.className + "] OPNUMBER[" + this.opDataNumber + "]";
    }
}
