package com.ql.util.express.instruction.op;

import com.ql.util.express.ArraySwap;
import com.ql.util.express.InstructionSetContext;
import com.ql.util.express.OperateData;
import com.ql.util.express.instruction.OperateDataCacheManager;
import com.ql.util.express.instruction.opdata.OperateDataLocalVar;
import com.ql.util.express.instruction.opdata.OperateDataVirClass;

public class OperatorDef extends OperatorBase {
	public OperatorDef(String aName) {
		this.name = aName;
	}
	public OperatorDef(String aAliasName, String aName, String aErrorInfo) {
		this.name = aName;
		this.aliasName = aAliasName;
		this.errorInfo = aErrorInfo;
	}
	
	public OperateData executeInner(InstructionSetContext context, ArraySwap list) throws Exception {
		Object type = list.get(0).getObject(context);
		String varName = (String)list.get(1).getObject(context);	
		Class<?> tmpClass = null;
		if(type instanceof Class ){
			tmpClass = (Class<?>) type;
		}else{
			tmpClass = OperateDataVirClass.class;
		}
		OperateDataLocalVar result = OperateDataCacheManager.fetchOperateDataLocalVar(varName,tmpClass);
		context.addSymbol(varName, result);
		return result;
	}
}
