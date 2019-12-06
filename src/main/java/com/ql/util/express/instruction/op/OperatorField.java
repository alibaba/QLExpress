package com.ql.util.express.instruction.op;

import com.ql.util.express.ArraySwap;
import com.ql.util.express.InstructionSetContext;
import com.ql.util.express.OperateData;
import com.ql.util.express.config.QLExpressRunStrategy;
import com.ql.util.express.instruction.OperateDataCacheManager;
import com.ql.util.express.instruction.opdata.OperateDataField;

public  class OperatorField extends OperatorBase {
	String filedName;
	public OperatorField() {
		this.name = "FieldCall";
	}
	public OperatorField(String aFieldName) {
		this.name = "FieldCall";
		this.filedName = aFieldName;
		
	}
	public OperateData executeInner(InstructionSetContext parent, ArraySwap list) throws Exception {
		OperateData operateData = list.get(0);
		if (operateData == null && QLExpressRunStrategy.isAvoidNullPointer()) {
			return null;
		}
		Object obj = operateData.getObject(parent);
		return OperateDataCacheManager.fetchOperateDataField(obj,this.filedName);
	}
    public String toString(){
    	return this.name + ":" + this.filedName;
    } 	
}
