package com.ql.util.express.instruction.op;

import com.ql.util.express.Operator;
import com.ql.util.express.exception.QLException;

public class OperatorPrint extends Operator {
	public OperatorPrint(String name) {
		this.name = name;
	}
	public OperatorPrint(String aAliasName, String aName, String aErrorInfo) {
		this.name = aName;
		this.aliasName = aAliasName;
		this.errorInfo = aErrorInfo;
	}
	public Object executeInner(Object[] list) throws Exception {
		if (list.length != 1 ){
			throw new QLException("操作数异常,有且只能有一个操作数");
		}
        System.out.print(list[0]);
        return null;
	}


}
