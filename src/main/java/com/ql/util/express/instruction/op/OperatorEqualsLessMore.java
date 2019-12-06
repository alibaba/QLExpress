package com.ql.util.express.instruction.op;

import com.ql.util.express.Operator;
import com.ql.util.express.exception.QLException;

import static com.ql.util.express.config.QLExpressRunStrategy.*;

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
	    
	    if(opStr.equals("==")){
	        return Operator.objectEquals(obj1, obj2);
        }
        if(opStr.equals("!=")||opStr.equals("<>")){
            return !Operator.objectEquals(obj1, obj2);
        }
        //进行其他大小比较操作
        if (obj1 == null || obj2 == null){
	    	if (isCompareNullLessMoreAsFalse()) {
	    		return false;
			}
            throw new QLException("空操作数无法进行数字比较操作：left = " + obj1+",right = "+ obj2);
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
