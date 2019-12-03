package com.ql.util.express.instruction.op;

import com.ql.util.express.Operator;

 /**
 * 处理比较操作符号
 */
public class OperatorEqualsLessMore extends Operator {
	public OperatorEqualsLessMore(String aName) {
		this.name = aName;
	}

	public OperatorEqualsLessMore(String aAliasName, String aName,
			String aErrorInfo) {
		this.name = aName;
		this.aliasName = aAliasName;
		this.errorInfo = aErrorInfo;
	}

	public Object executeInner(Object[] list) throws Exception {
		return executeInner(list[0], list[1]);
	}

	public Object executeInner(Object op1,Object op2) throws Exception {
		boolean result = executeInner(this.name, op1, op2);
		return result;
	}

	public static boolean executeInner(String opStr, Object obj1, Object obj2)
			throws Exception {

		if (obj1 == null && obj2 == null) {
			if (opStr.equals("==")) {
				return true;
			} else if (opStr.equals("!=") || opStr.equals("<>")) {
				return false;
			} else {
				throw new Exception("两个空操作数不能执行这个操作：" + opStr);
			}
		} else if (obj1 == null || obj2 == null) {
			if (opStr.equals("==")) {
				return false;
			} else if (opStr.equals("!=") || opStr.equals("<>")) {
				return true;
			} else if (opStr.equals("<=") || opStr.equals(">=") || opStr.equals("<")  || opStr.equals(">")  ) {
				return false;
			} else {
				throw new Exception("空操作数不能执行这个操作：" + opStr);
			}
		}

		int i = Operator.compareData(obj1, obj2);
		boolean result = false;
		if (i > 0) {
			if (opStr.equals(">") || opStr.equals(">=") || opStr.equals("!=")
					|| opStr.equals("<>"))
				result = true;
			else
				result = false;
		} else if (i == 0) {
			if (opStr.equals("=") || opStr.equals("==") || opStr.equals(">=")
					|| opStr.equals("<="))
				result = true;
			else
				result = false;
		} else if (i < 0) {
			if (opStr.equals("<") || opStr.equals("<=") || opStr.equals("!=")
					|| opStr.equals("<>"))
				result = true;
			else
				result = false;
		}
		return result;
	}
}
