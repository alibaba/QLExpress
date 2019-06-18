package com.ql.util.express.instruction.op;

import com.ql.util.express.Operator;
import com.ql.util.express.exception.QLException;

public class OperatorNot extends Operator {
	public OperatorNot(String name) {
		this.name = name;
	}
	public OperatorNot(String aAliasName, String aName, String aErrorInfo) {
		this.name = aName;
		this.aliasName = aAliasName;
		this.errorInfo = aErrorInfo;
	}
	public Object executeInner(Object[] list) throws Exception {
		return executeInner(list[0]);
	}

	public Object executeInner(Object op)
			throws Exception {
		Object result = null;
        if (op == null){
        	throw new QLException("null 不能执行操作：" + this.getAliasName());
        }
		if (Boolean.class.equals(op.getClass()) == true) {
			boolean r = !((Boolean) op).booleanValue();
			result = Boolean.valueOf(r);
		} else {
			//
			String msg = "没有定义类型" + op.getClass().getName() + " 的 " + this.name
					+ "操作";
			throw new QLException(msg);
		}
		return result;
	}
}
