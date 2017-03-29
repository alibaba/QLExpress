package com.ql.util.express.instruction.op;

import com.ql.util.express.Operator;

public class OperatorNor extends Operator {
	public OperatorNor(String name) {
		this.name = name;
	}

	public OperatorNor(String aAliasName, String aName, String aErrorInfo) {
		this.name = aName;
		this.aliasName = aAliasName;
		this.errorInfo = aErrorInfo;
	}

	public Object executeInner(Object[] list) throws Exception {
		return executeInner(list[0], list[1]);
	}

	public Object executeInner(Object op1, Object op2) throws Exception {

		if (op1 != null) {
			return op1;
		} else {
			return op2;
		}
	}
}
