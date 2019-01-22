package com.ql.util.express.instruction.detail;

import java.util.List;

import com.ql.util.express.ArraySwap;
import com.ql.util.express.OperateData;
import com.ql.util.express.RunEnvironment;
import com.ql.util.express.instruction.opdata.OperateDataAttr;
import com.ql.util.express.instruction.opdata.OperateDataVirClass;

public class InstructionNewVirClass extends Instruction {
	private static final long serialVersionUID = -4174411242319009814L;
	String className;
	int opDataNumber;

	public InstructionNewVirClass(String name, int aOpDataNumber) {
		this.className = name;
		this.opDataNumber = aOpDataNumber;
	}
    
    public String getClassName() {
        return className;
    }
    
    public void execute(RunEnvironment environment, List<String> errorList)
			throws Exception {
		ArraySwap parameters = environment.popArray(
				environment.getContext(), this.opDataNumber);
		if (environment.isTrace() && log.isDebugEnabled()) {
			String str = "new VClass(";
			OperateData p;
			for (int i = 0; i < parameters.length; i++) {
				p = parameters.get(i);
				if (i > 0) {
					str = str + ",";
				}
				if (p instanceof OperateDataAttr) {
					str = str + p + ":"
							+ p.getObject(environment.getContext());
				} else {
					str = str + p;
				}
			}
			str = str + ")";
			log.debug(str);
		}
		
		
		//因为会影响堆栈，要先把对象拷贝出来
		OperateData[] list = new OperateData[parameters.length];
		for(int i = 0;i <list.length;i++){			
			list[i] = parameters.get(i);
		}
		
		OperateDataVirClass result = new OperateDataVirClass(className);
		environment.push(result);
		environment.programPointAddOne();
		result.initialInstance(environment.getContext(), list, errorList,
				environment.isTrace(), this.log);
	}

	public String toString() {
		return "new VClass[" + this.className + "] OPNUMBER["
				+ this.opDataNumber + "]";
	}

}
