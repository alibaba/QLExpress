package com.ql.util.express.instruction.op;

import com.ql.util.express.ArraySwap;
import com.ql.util.express.InstructionSetContext;
import com.ql.util.express.OperateData;
import com.ql.util.express.exception.QLException;

public  class OperatorIf extends OperatorBase {
	public OperatorIf(String aName) {
		this.name = aName;
	}

	public OperatorIf(String aAliasName, String aName, String aErrorInfo) {
		this.name = aName;
		this.aliasName = aAliasName;
		this.errorInfo = aErrorInfo;
	}
	
	public  OperateData executeInner(InstructionSetContext parent, ArraySwap list) throws Exception {
		if(list.length <2){
			throw new QLException("\"" + this.aliasName + "\"操作至少要两个操作数");
		}
		Object obj = list.get(0).getObject(parent);
		if (obj == null) {
			String msg ="\"" + this.aliasName + "\"的判断条件不能为空";
			throw new QLException(msg);
		} else if ((obj instanceof Boolean) == false) {
			String msg = "\"" + this.aliasName + "\"的判断条件 必须是 Boolean,不能是：";
			throw new QLException(msg + obj.getClass().getName());
		} else {
			if (((Boolean)obj).booleanValue() == true){
				return list.get(1);
			}else{
				if(list.length == 3){
					return list.get(2);
				}
			}
			return null;			
		}
	}
}
